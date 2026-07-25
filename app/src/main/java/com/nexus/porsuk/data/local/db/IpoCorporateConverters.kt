package com.nexus.porsuk.data.local.db

import androidx.room.TypeConverter
import com.nexus.porsuk.domain.model.CorporateActionType
import com.nexus.porsuk.domain.model.IpoStatus

class IpoCorporateConverters {

    @TypeConverter
    fun fromIpoStatus(value: IpoStatus): String = value.name

    @TypeConverter
    fun toIpoStatus(value: String): IpoStatus = IpoStatus.valueOf(value)

    @TypeConverter
    fun fromCorporateActionType(value: CorporateActionType): String = value.name

    @TypeConverter
    fun toCorporateActionType(value: String): CorporateActionType = CorporateActionType.valueOf(value)
}
