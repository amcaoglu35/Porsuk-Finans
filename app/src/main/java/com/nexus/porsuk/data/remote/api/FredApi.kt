package com.nexus.porsuk.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query

interface FredApi {
    @GET("fred/series/observations")
    suspend fun getObservations(
        @Query("series_id") seriesId: String,
        @Query("file_type") fileType: String = "json"
    ): FredResponseDto
}

data class FredResponseDto(
    val realtime_start: String?,
    val realtime_end: String?,
    val observation_start: String?,
    val observation_end: String?,
    val units: String?,
    val output_type: Int?,
    val file_type: String?,
    val order_by: String?,
    val sort_order: String?,
    val count: Int?,
    val offset: Int?,
    val limit: Int?,
    val observations: List<FredObservationDto>?
)

data class FredObservationDto(
    val realtime_start: String?,
    val realtime_end: String?,
    val date: String,
    val value: String
)
