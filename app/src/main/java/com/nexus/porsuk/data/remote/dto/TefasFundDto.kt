package com.nexus.porsuk.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * TEFAS Fon Uzak Veri Transfer Nesnesi (DTO)
 *
 * TEFAS platformundan alınan ham fon verilerini temsil eder.
 */
data class TefasFundDto(
    @SerializedName("FONKODU") val code: String,
    @SerializedName("FONUNVAN") val name: String,
    @SerializedName("KURUCU") val founder: String? = null,
    @SerializedName("YONETICI") val manager: String? = null,
    @SerializedName("SEMSIYEFON") val umbrellaFund: String? = null,
    @SerializedName("FONTURU") val fundType: String? = null,
    @SerializedName("RISKSEVIYESI") val riskLevel: Int? = null,
    @SerializedName("PARABIRIMI") val currency: String? = null,
    @SerializedName("FIYAT") val price: Double? = null,
    @SerializedName("TOPLAMDEGER") val totalAssets: Double? = null,
    @SerializedName("KASASAYISI") val investorCount: Long? = null,
    @SerializedName("YONETIMUCRETI") val managementFee: Double? = null,
    @SerializedName("TARIH") val lastUpdatedDate: String? = null
)
