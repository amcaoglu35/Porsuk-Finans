package com.nexus.porsuk.data.calculator

import com.google.common.truth.Truth.assertThat
import com.nexus.porsuk.data.local.entity.PortfolioAssetEntity
import com.nexus.porsuk.data.local.entity.PortfolioTransactionEntity
import com.nexus.porsuk.domain.model.AssetCategory
import org.junit.Before
import org.junit.Test

/**
 * PortfolioCalculatorEngine Unit Testleri
 *
 * Test Edilen:
 * 1. calculateAverageCostAndQuantity — ortalama maliyet & kalan lot
 * 2. calculateRealizedProfitLoss    — gerçekleşen kar/zarar
 * 3. calculateBreakdowns            — 5 boyutlu dağılım analizi
 */
class PortfolioCalculatorEngineTest {

    private lateinit var engine: PortfolioCalculatorEngine

    @Before
    fun setUp() {
        engine = PortfolioCalculatorEngine()
    }

    // ─────────────────────────────────────────────────────────────
    // calculateAverageCostAndQuantity
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `boş işlem listesiyle ortalama maliyet sıfır ve lot sıfır döner`() {
        val (avgCost, qty) = engine.calculateAverageCostAndQuantity(emptyList())
        assertThat(avgCost).isEqualTo(0.0)
        assertThat(qty).isEqualTo(0.0)
    }

    @Test
    fun `tek alım işleminde ortalama maliyet fiyat artı komisyon bölü lot olur`() {
        // 100 lot × 50 TL + 10 TL komisyon = 5010 TL → avg = 50.10
        val txList = listOf(
            makeBuy(quantity = 100.0, price = 50.0, fee = 10.0)
        )
        val (avgCost, qty) = engine.calculateAverageCostAndQuantity(txList)
        assertThat(avgCost).isWithin(1e-6).of(50.10)
        assertThat(qty).isEqualTo(100.0)
    }

    @Test
    fun `iki farklı fiyattan alımda ağırlıklı ortalama hesaplanır`() {
        // 100 lot × 50 TL = 5000
        // 50 lot × 60 TL = 3000
        // Toplam: 8000 / 150 = 53.33...
        val txList = listOf(
            makeBuy(quantity = 100.0, price = 50.0, timestamp = 1000L),
            makeBuy(quantity = 50.0, price = 60.0, timestamp = 2000L)
        )
        val (avgCost, qty) = engine.calculateAverageCostAndQuantity(txList)
        assertThat(avgCost).isWithin(0.01).of(53.33)
        assertThat(qty).isEqualTo(150.0)
    }

    @Test
    fun `satış sonrası lot azalır ortalama maliyet sabit kalır`() {
        // 100 lot × 50 = 5000, satış 40 lot → kalan 60 lot, avg ~50 TL
        val txList = listOf(
            makeBuy(quantity = 100.0, price = 50.0, timestamp = 1000L),
            makeSell(quantity = 40.0, price = 70.0, timestamp = 2000L)
        )
        val (avgCost, qty) = engine.calculateAverageCostAndQuantity(txList)
        assertThat(qty).isWithin(1e-6).of(60.0)
        assertThat(avgCost).isWithin(0.01).of(50.0)
    }

    @Test
    fun `tüm lotlar satılınca ortalama maliyet sıfır olur`() {
        val txList = listOf(
            makeBuy(quantity = 100.0, price = 50.0, timestamp = 1000L),
            makeSell(quantity = 100.0, price = 70.0, timestamp = 2000L)
        )
        val (avgCost, qty) = engine.calculateAverageCostAndQuantity(txList)
        assertThat(qty).isEqualTo(0.0)
        assertThat(avgCost).isEqualTo(0.0)
    }

    @Test
    fun `bedelsiz sermaye artırımı lot artırır ortalama maliyeti düşürür`() {
        // 100 lot × 50 TL = 5000, +50 bedelsiz → 150 lot, avg = 33.33
        val txList = listOf(
            makeBuy(quantity = 100.0, price = 50.0, timestamp = 1000L),
            makeBonusIssue(quantity = 50.0, timestamp = 2000L)
        )
        val (avgCost, qty) = engine.calculateAverageCostAndQuantity(txList)
        assertThat(qty).isEqualTo(150.0)
        assertThat(avgCost).isWithin(0.01).of(33.33)
    }

    @Test
    fun `sıfır fiyatlı alım işlemi sıfır ortalama maliyet üretmez (edge case)`() {
        // Bedelli artırım 0 TL ile yapılmış olsa bile lot artmalı
        val txList = listOf(
            makeBuy(quantity = 100.0, price = 0.0, fee = 0.0)
        )
        val (avgCost, qty) = engine.calculateAverageCostAndQuantity(txList)
        assertThat(qty).isEqualTo(100.0)
        assertThat(avgCost).isEqualTo(0.0) // 0/100 = 0 — bölme hatası olmamalı
    }

    // ─────────────────────────────────────────────────────────────
    // calculateRealizedProfitLoss
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `sadece alım işlemi varken gerçekleşen kar sıfırdır`() {
        val txList = listOf(makeBuy(quantity = 100.0, price = 50.0))
        val pnl = engine.calculateRealizedProfitLoss(txList)
        assertThat(pnl).isEqualTo(0.0)
    }

    @Test
    fun `alım ve karlı satış durumunda gerçekleşen kar pozitif döner`() {
        // Alım: 100 lot × 50 + 0 komisyon = 5000 maliyet
        // Satış: 100 lot × 60 - 0 komisyon - 0 vergi = 6000 gelir
        // Realized PnL = 6000 - 5000 = 1000
        val txList = listOf(
            makeBuy(quantity = 100.0, price = 50.0, timestamp = 1000L),
            makeSell(quantity = 100.0, price = 60.0, timestamp = 2000L)
        )
        val pnl = engine.calculateRealizedProfitLoss(txList)
        assertThat(pnl).isWithin(1e-6).of(1000.0)
    }

    @Test
    fun `alım ve zararına satış durumunda gerçekleşen kar negatif döner`() {
        // Alım: 100 × 50 = 5000
        // Satış: 100 × 40 = 4000 → Zarar: -1000
        val txList = listOf(
            makeBuy(quantity = 100.0, price = 50.0, timestamp = 1000L),
            makeSell(quantity = 100.0, price = 40.0, timestamp = 2000L)
        )
        val pnl = engine.calculateRealizedProfitLoss(txList)
        assertThat(pnl).isWithin(1e-6).of(-1000.0)
    }

    @Test
    fun `komisyon ve vergi hesaba katılır`() {
        // Alım: 100 × 50 + 10 komisyon = 5010
        // Satış: 100 × 60 - 5 komisyon - 10 vergi = 5985 gelir
        // Avg cost = 5010/100 = 50.10; cost of sold = 5010; pnl = 5985 - 5010 = 975
        val txList = listOf(
            makeBuy(quantity = 100.0, price = 50.0, fee = 10.0, timestamp = 1000L),
            makeSell(quantity = 100.0, price = 60.0, fee = 5.0, tax = 10.0, timestamp = 2000L)
        )
        val pnl = engine.calculateRealizedProfitLoss(txList)
        assertThat(pnl).isWithin(0.01).of(975.0)
    }

    @Test
    fun `temettü geliri gerçekleşen kara eklenir`() {
        // Sadece temettü: 500 brüt - 75 vergi = 425 net
        val txList = listOf(
            makeDividend(totalAmount = 500.0, tax = 75.0)
        )
        val pnl = engine.calculateRealizedProfitLoss(txList)
        assertThat(pnl).isWithin(1e-6).of(425.0)
    }

    @Test
    fun `elinde lot yokken satış işlemi gerçekleşen kara etki etmez (0 bölme koruması)`() {
        // Önce satış, sonra alım — hiçbir şey satılamaz
        val txList = listOf(
            makeSell(quantity = 100.0, price = 60.0, timestamp = 1000L),
            makeBuy(quantity = 100.0, price = 50.0, timestamp = 2000L)
        )
        // Satış anında totalQuantity=0 olduğu için `if (totalQuantity > 0)` bloğuna girmez
        val pnl = engine.calculateRealizedProfitLoss(txList)
        // Sadece alım yapıldı; realized PnL 0 olmalı
        assertThat(pnl).isEqualTo(0.0)
    }

    @Test
    fun `boş işlem listesiyle gerçekleşen kar sıfır döner`() {
        val pnl = engine.calculateRealizedProfitLoss(emptyList())
        assertThat(pnl).isEqualTo(0.0)
    }

    @Test
    fun `kısmi satışta kalan lot doğru hesaplanır ve kalan satış da doğru pnl verir`() {
        // 200 lot × 50 = 10000 maliyet
        // Sat 100 lot × 60 → pnl1 = (6000 - 5000) = 1000, kalan 100 lot, kalan maliyet 5000
        // Sat 100 lot × 70 → pnl2 = (7000 - 5000) = 2000
        // Toplam: 3000
        val txList = listOf(
            makeBuy(quantity = 200.0, price = 50.0, timestamp = 1000L),
            makeSell(quantity = 100.0, price = 60.0, timestamp = 2000L),
            makeSell(quantity = 100.0, price = 70.0, timestamp = 3000L)
        )
        val pnl = engine.calculateRealizedProfitLoss(txList)
        assertThat(pnl).isWithin(1e-6).of(3000.0)
    }

    // ─────────────────────────────────────────────────────────────
    // calculateBreakdowns
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `boş varlık listesiyle breakdown boş döner ve yüzdeler hesaplanamaz`() {
        val breakdown = engine.calculateBreakdowns(emptyList())
        assertThat(breakdown.assetCategoryBreakdown).isEmpty()
        assertThat(breakdown.cashRatioPercentage).isEqualTo(0.0)
    }

    @Test
    fun `tek varlık yüzde 100 olarak hesaplanır`() {
        val assets = listOf(
            makeAsset(symbol = "THYAO.IS", totalValue = 10000.0, category = AssetCategory.BIST_STOCK.name)
        )
        val breakdown = engine.calculateBreakdowns(assets)
        assertThat(breakdown.assetCategoryBreakdown).hasSize(1)
        assertThat(breakdown.assetCategoryBreakdown[0].percentage).isWithin(1e-6).of(100.0)
    }

    @Test
    fun `iki farklı kategorideki varlıklar için yüzdeler toplamı 100 olur`() {
        val assets = listOf(
            makeAsset(symbol = "THYAO.IS", totalValue = 6000.0, category = AssetCategory.BIST_STOCK.name),
            makeAsset(symbol = "AAPL", totalValue = 4000.0, category = AssetCategory.NASDAQ_STOCK.name)
        )
        val breakdown = engine.calculateBreakdowns(assets)
        val total = breakdown.assetCategoryBreakdown.sumOf { it.percentage }
        assertThat(total).isWithin(1e-6).of(100.0)
    }

    @Test
    fun `para birimi dağılımı IS ile biten semboller TRY olarak gruplandırır`() {
        val assets = listOf(
            makeAsset(symbol = "THYAO.IS", totalValue = 6000.0, category = AssetCategory.BIST_STOCK.name),
            makeAsset(symbol = "AAPL", totalValue = 4000.0, category = AssetCategory.NASDAQ_STOCK.name)
        )
        val breakdown = engine.calculateBreakdowns(assets)
        val try_ = breakdown.currencyBreakdown.find { it.categoryName == "TRY" }
        val usd = breakdown.currencyBreakdown.find { it.categoryName == "USD" }
        assertThat(try_?.percentage).isWithin(1e-6).of(60.0)
        assertThat(usd?.percentage).isWithin(1e-6).of(40.0)
    }

    @Test
    fun `tüm varlıklar sıfır değerindeyse dağılım boş döner (sıfıra bölme koruması)`() {
        val assets = listOf(
            makeAsset(symbol = "THYAO.IS", totalValue = 0.0, category = AssetCategory.BIST_STOCK.name)
        )
        val breakdown = engine.calculateBreakdowns(assets)
        // grandTotalValuation <= 0, erken çıkış bekleniyor
        assertThat(breakdown.assetCategoryBreakdown).isEmpty()
        assertThat(breakdown.cashRatioPercentage).isEqualTo(0.0)
    }

    // ─────────────────────────────────────────────────────────────
    // Yardımcı factory metotlar
    // ─────────────────────────────────────────────────────────────

    private fun makeBuy(
        quantity: Double,
        price: Double,
        fee: Double = 0.0,
        tax: Double = 0.0,
        timestamp: Long = System.currentTimeMillis()
    ) = PortfolioTransactionEntity(
        portfolioId = "test-portfolio",
        symbol = "TEST.IS",
        transactionType = "BUY",
        quantity = quantity,
        price = price,
        totalAmount = quantity * price + fee + tax,
        fee = fee,
        tax = tax,
        timestamp = timestamp
    )

    private fun makeSell(
        quantity: Double,
        price: Double,
        fee: Double = 0.0,
        tax: Double = 0.0,
        timestamp: Long = System.currentTimeMillis()
    ) = PortfolioTransactionEntity(
        portfolioId = "test-portfolio",
        symbol = "TEST.IS",
        transactionType = "SELL",
        quantity = quantity,
        price = price,
        totalAmount = quantity * price - fee - tax,
        fee = fee,
        tax = tax,
        timestamp = timestamp
    )

    private fun makeBonusIssue(quantity: Double, timestamp: Long = System.currentTimeMillis()) =
        PortfolioTransactionEntity(
            portfolioId = "test-portfolio",
            symbol = "TEST.IS",
            transactionType = "BONUS_ISSUE_FREE",
            quantity = quantity,
            price = 0.0,
            totalAmount = 0.0,
            timestamp = timestamp
        )

    private fun makeDividend(totalAmount: Double, tax: Double) = PortfolioTransactionEntity(
        portfolioId = "test-portfolio",
        symbol = "TEST.IS",
        transactionType = "DIVIDEND",
        quantity = 0.0,
        price = 0.0,
        totalAmount = totalAmount,
        tax = tax
    )

    private fun makeAsset(symbol: String, totalValue: Double, category: String) =
        PortfolioAssetEntity(
            portfolioId = "test-portfolio",
            symbol = symbol,
            name = symbol,
            quantity = 1.0,
            averageCost = totalValue,
            currentPrice = totalValue,
            totalValue = totalValue,
            totalCost = totalValue,
            profitLoss = 0.0,
            profitPercent = 0.0,
            assetCategory = category
        )
}
