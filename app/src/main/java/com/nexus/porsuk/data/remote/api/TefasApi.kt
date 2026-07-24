package com.nexus.porsuk.data.remote.api

import com.nexus.porsuk.data.remote.dto.TefasFundDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Porsuk TEFAS Engine — Retrofit REST API Arayüzü
 */
interface TefasApi {

    /**
     * TEFAS'taki tüm aktif yatırım fonlarının listesini çeker.
     */
    @GET("api/v1/tefas/funds")
    suspend fun getAllFunds(): List<TefasFundDto>

    /**
     * Belirtilen fon kodunun detay verilerini çeker.
     */
    @GET("api/v1/tefas/fund-detail")
    suspend fun getFundDetail(
        @Query("code") fundCode: String
    ): TefasFundDto
}
