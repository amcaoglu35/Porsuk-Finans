package com.nexus.porsuk.ui.analysis

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * BankruptcyAndManipulationDetector Unit Testleri
 *
 * Altman Z-Score Formülü:
 *   Z = 1.2×X1 + 1.4×X2 + 3.3×X3 + 0.6×X4 + 0.999×X5
 *   Z ≥ 2.99 → Güvenli, 1.81–2.99 → Gri, <1.81 → İflas Riski
 *
 * Beneish M-Score Formülü:
 *   M = -4.84 + 0.92×DSRI + 0.528×GMI + 0.404×AQI + 0.892×SGI
 *     + 0.115×DEPI - 0.172×SGAI + 4.679×TATA - 0.327×LVGI
 *   M > -1.78 → Manipülasyon Riski Yüksek
 */
class BankruptcyAndManipulationDetectorTest {

    @Test
    fun `varsayılan değerlerle güvenli bölge ve temiz muhasebe döner`() {
        val result = BankruptcyAndManipulationDetector.analyze()
        assertThat(result.altmanZScore).isGreaterThan(2.99)
        assertThat(result.altmanZone).contains("GÜVENLİ")
        assertThat(result.isManipulationRiskHigh).isFalse()
        assertThat(result.beneishRating).contains("TEMİZ")
    }

    // ─────────────────────────────────────────────────────────────
    // Altman Z-Score testleri
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `yüksek karlılık ve düşük borçla güvenli bölge skoru elde edilir`() {
        val result = BankruptcyAndManipulationDetector.analyze(
            workingCapitalToAssets = 0.5,      // Çok iyi likidite
            retainedEarningsToAssets = 0.6,    // Yüksek birikmişler
            ebitToAssets = 0.3,                // Yüksek EBIT
            marketCapToTotalLiabilities = 4.0, // Düşük borç yükü
            salesToAssets = 1.5                // Yüksek verimlilik
        )
        assertThat(result.altmanZScore).isGreaterThan(2.99)
        assertThat(result.altmanZone).contains("GÜVENLİ")
    }

    @Test
    fun `negatif işletme sermayesi ve düşük karlılıkla iflas riski bölgesine girilir`() {
        val result = BankruptcyAndManipulationDetector.analyze(
            workingCapitalToAssets = -0.3,     // Negatif işletme sermayesi
            retainedEarningsToAssets = -0.4,   // Birikmiş zararlar
            ebitToAssets = -0.15,              // EBIT negatif
            marketCapToTotalLiabilities = 0.2, // Çok yüksek borç yükü
            salesToAssets = 0.3                // Düşük ciro
        )
        assertThat(result.altmanZScore).isLessThan(1.81)
        assertThat(result.altmanZone).contains("İFLAS")
    }

    @Test
    fun `gri bölge skoru 1_81 ile 2_99 arasında kalır`() {
        // Bilinçli olarak gri bölgeye tekabül eden değerler
        val result = BankruptcyAndManipulationDetector.analyze(
            workingCapitalToAssets = 0.1,
            retainedEarningsToAssets = 0.1,
            ebitToAssets = 0.12,
            marketCapToTotalLiabilities = 0.9,
            salesToAssets = 0.95
        )
        assertThat(result.altmanZScore).isAtLeast(1.81)
        assertThat(result.altmanZScore).isAtMost(2.99)
        assertThat(result.altmanZone).contains("GRİ")
    }

    @Test
    fun `Z-Score formülü doğru ağırlıklarla hesaplanır (manuel doğrulama)`() {
        // Z = 1.2×0.2 + 1.4×0.3 + 3.3×0.15 + 0.6×1.5 + 0.999×1.0
        // Z = 0.24 + 0.42 + 0.495 + 0.9 + 0.999 = 3.054
        val result = BankruptcyAndManipulationDetector.analyze(
            workingCapitalToAssets = 0.2,
            retainedEarningsToAssets = 0.3,
            ebitToAssets = 0.15,
            marketCapToTotalLiabilities = 1.5,
            salesToAssets = 1.0,
            // M-Score bileşenleri varsayılan değerlerde bırakıldı
        )
        val expected = 1.2 * 0.2 + 1.4 * 0.3 + 3.3 * 0.15 + 0.6 * 1.5 + 0.999 * 1.0
        assertThat(result.altmanZScore).isWithin(1e-6).of(expected)
    }

    // ─────────────────────────────────────────────────────────────
    // Beneish M-Score testleri
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `yüksek DSRI ve yüksek TATA manipülasyon riskine yol açar`() {
        // M-Score bileşenleri manipülasyona işaret eden değerler
        val result = BankruptcyAndManipulationDetector.analyze(
            dsri = 2.0,  // Alacak artışı — kırmızı bayrak
            gmi = 1.5,   // Brüt marj düşüşü
            aqi = 1.5,   // Varlık kalitesi düşüşü
            sgi = 1.4,   // Satış artışı
            depi = 0.8,  // Amortisman düşüşü
            sgai = 1.2,  // Genel gider artışı
            lvgi = 1.2,  // Kaldıraç artışı
            tata = 0.15  // Yüksek tahakkuk
        )
        assertThat(result.isManipulationRiskHigh).isTrue()
        assertThat(result.beneishRating).contains("MANİPÜLASYON")
    }

    @Test
    fun `temiz finansal göstergelerle manipülasyon riski düşük olur`() {
        val result = BankruptcyAndManipulationDetector.analyze(
            dsri = 0.9,   // Alacaklar düştü (iyi)
            gmi = 0.98,   // Brüt marj stabil
            aqi = 0.95,   // Varlık kalitesi iyi
            sgi = 1.05,   // Normal büyüme
            depi = 1.0,
            sgai = 0.98,
            lvgi = 0.9,
            tata = 0.01   // Düşük tahakkuk
        )
        assertThat(result.isManipulationRiskHigh).isFalse()
        assertThat(result.beneishRating).contains("TEMİZ")
    }

    @Test
    fun `M-Score eşiği -1_78 doğru çalışıyor (sınır değer testi)`() {
        // -4.84 + 0.92×1 + 0.528×1 + 0.404×1 + 0.892×1 + 0.115×1 - 0.172×1 + 4.679×0 - 0.327×1
        // = -4.84 + 0.92 + 0.528 + 0.404 + 0.892 + 0.115 - 0.172 + 0 - 0.327
        // = -2.48 → düşük risk (< -1.78)
        val result = BankruptcyAndManipulationDetector.analyze(
            dsri = 1.0, gmi = 1.0, aqi = 1.0, sgi = 1.0,
            depi = 1.0, sgai = 1.0, lvgi = 1.0, tata = 0.0
        )
        assertThat(result.beneishMScore).isLessThan(-1.78)
        assertThat(result.isManipulationRiskHigh).isFalse()
    }

    @Test
    fun `Z-Score renk kodları bölgelere göre doğru atanır`() {
        // Güvenli → yeşil (0xFF00A878)
        val safeResult = BankruptcyAndManipulationDetector.analyze(
            workingCapitalToAssets = 0.5,
            retainedEarningsToAssets = 0.6,
            ebitToAssets = 0.3,
            marketCapToTotalLiabilities = 4.0,
            salesToAssets = 1.5
        )
        assertThat(safeResult.altmanZoneColorHex).isEqualTo(0xFF00A878)

        // İflas riski → kırmızı (0xFFEF4A5F)
        val distressResult = BankruptcyAndManipulationDetector.analyze(
            workingCapitalToAssets = -0.5,
            retainedEarningsToAssets = -0.5,
            ebitToAssets = -0.2,
            marketCapToTotalLiabilities = 0.1,
            salesToAssets = 0.2
        )
        assertThat(distressResult.altmanZoneColorHex).isEqualTo(0xFFEF4A5F)
    }
}
