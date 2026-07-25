package com.nexus.porsuk.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nexus.porsuk.domain.model.FundType
import com.nexus.porsuk.domain.model.ReplicationMethod

class FundConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromFundType(value: FundType): String = value.name

    @TypeConverter
    fun toFundType(value: String): FundType = FundType.valueOf(value)

    @TypeConverter
    fun fromReplicationMethod(value: ReplicationMethod?): String? = value?.name

    @TypeConverter
    fun toReplicationMethod(value: String?): ReplicationMethod? = value?.let { ReplicationMethod.valueOf(it) }

    @TypeConverter
    fun fromMap(value: Map<String, Double>): String = gson.toJson(value)

    @TypeConverter
    fun toMap(value: String): Map<String, Double> {
        val type = object : TypeToken<Map<String, Double>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromHoldingsList(value: List<com.nexus.porsuk.domain.model.FundHolding>): String = gson.toJson(value)

    @TypeConverter
    fun toHoldingsList(value: String): List<com.nexus.porsuk.domain.model.FundHolding> {
        val type = object : TypeToken<List<com.nexus.porsuk.domain.model.FundHolding>>() {}.type
        return gson.fromJson(value, type)
    }
}
