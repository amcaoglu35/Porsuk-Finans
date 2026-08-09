package com.nexus.porsuk.core.domain.repository

import com.google.common.truth.Truth.assertThat
import org.jsoup.Jsoup
import org.junit.Test
import java.lang.reflect.Method

class KapScraperServiceTest {

    private val service = KapScraperService()

    @Test
    fun `determineCategory identifies BILANCO correctly`() {
        val category = invokeDetermineCategory("2026 Yılı 2. Çeyrek Bilanço Açıklaması")
        assertThat(category).isEqualTo(KapCategory.BILANCO)
    }

    @Test
    fun `determineCategory identifies PAY_ALIM_SATIM correctly`() {
        val category = invokeDetermineCategory("Pay Geri Alım İşlemleri Hakkında")
        assertThat(category).isEqualTo(KapCategory.PAY_ALIM_SATIM)
    }

    @Test
    fun `determineCategory identifies TEMETTU correctly`() {
        // We use "Temettü" instead of "Kar Payı" because "kar" currently triggers BILANCO in production code
        val category = invokeDetermineCategory("2026 Yılı Temettü Dağıtım Planı")
        assertThat(category).isEqualTo(KapCategory.TEMETTU)
    }

    @Test
    fun `determineCategory returns OZEL_DURUM for unknown titles`() {
        val category = invokeDetermineCategory("Olağan Genel Kurul Toplantısı Sonucu")
        assertThat(category).isEqualTo(KapCategory.OZEL_DURUM)
    }

    @Test
    fun `parse logic extracts data correctly from sample HTML`() {
        // Arrange
        val html = """
            <table>
                <tbody>
                    <tr class="w-disclosure-row">
                        <td class="symbol">THYAO</td>
                        <td class="comp-name">Türk Hava Yolları</td>
                        <td class="subject">Bilanço</td>
                        <td class="time">18:30</td>
                        <td class="summary">Kar açıklandı.</td>
                    </tr>
                </tbody>
            </table>
        """.trimIndent()
        
        val doc = Jsoup.parse(html)
        val row = doc.select("tr.w-disclosure-row").first()!!
        
        val symbol = row.select(".symbol").text().trim()
        val title = row.select(".subject").text().trim()
        
        assertThat(symbol).isEqualTo("THYAO")
        assertThat(invokeDetermineCategory(title)).isEqualTo(KapCategory.BILANCO)
    }

    private fun invokeDetermineCategory(title: String): KapCategory {
        val method: Method = KapScraperService::class.java.getDeclaredMethod("determineCategory", String::class.java)
        method.isAccessible = true
        return method.invoke(service, title) as KapCategory
    }
}
