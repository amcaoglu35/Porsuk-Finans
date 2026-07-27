package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexus.porsuk.domain.model.ChartType
import com.nexus.porsuk.domain.model.TimeFrame

/**
 * Kayıtlı Grafik Düzeni
 */
@Entity(tableName = "chart_layouts")
data class ChartLayoutEntity(
    @PrimaryKey
    val layoutId: String,
    val name: String,
    val symbol: String,
    val chartType: String, // ChartType.name
    val timeFrame: String, // TimeFrame.name
    val indicatorsJson: String, // List<IndicatorConfig> as JSON
    val drawingsJson: String,   // List<DrawingConfig> as JSON
    val lastModified: Long = System.currentTimeMillis()
)

/**
 * Favori İndikatörler
 */
@Entity(tableName = "favorite_indicators")
data class FavoriteIndicatorEntity(
    @PrimaryKey
    val indicatorType: String // IndicatorType.name
)
