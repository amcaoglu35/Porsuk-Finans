package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.ScreenerUltimateEngine
import com.nexus.porsuk.data.local.dao.ScreenerFilterDao
import com.nexus.porsuk.data.local.entity.ScreenerFilterPresetEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenerRepositoryImpl @Inject constructor(
    private val screenerEngine: ScreenerUltimateEngine
) : ScreenerRepository {

    override fun executeScan(criteria: ScreenerUltimateCriteria): Flow<List<ScreenerResultItem>> = flow {
        emit(screenerEngine.executeUltimateScan(criteria))
    }
}

@Singleton
class FilterRepositoryImpl @Inject constructor(
    private val dao: ScreenerFilterDao
) : FilterRepository {

    override fun getSavedFilterPresets(): Flow<List<ScreenerUltimateCriteria>> {
        return dao.getAllSavedPresets().map { list ->
            list.map {
                ScreenerUltimateCriteria(
                    maxPeRatio = it.maxPeRatio,
                    minRoePct = it.minRoePct,
                    minMasterScore = it.minMasterScore
                )
            }
        }
    }

    override suspend fun saveFilterPreset(name: String, criteria: ScreenerUltimateCriteria) {
        val entity = ScreenerFilterPresetEntity(
            presetName = name,
            categoryName = criteria.presetCategory.name,
            maxPeRatio = criteria.maxPeRatio,
            minRoePct = criteria.minRoePct,
            minMasterScore = criteria.minMasterScore
        )
        dao.insertPreset(entity)
    }
}

@Singleton
class PresetRepositoryImpl @Inject constructor() : PresetRepository {
    override fun getPresetBundles(): Flow<List<SmartFilterPresetCategory>> = flow {
        emit(SmartFilterPresetCategory.entries)
    }
}

@Singleton
class ScanResultRepositoryImpl @Inject constructor(
    private val screenerEngine: ScreenerUltimateEngine
) : ScanResultRepository {
    override fun getCachedResults(): Flow<List<ScreenerResultItem>> = flow {
        emit(screenerEngine.executeUltimateScan(ScreenerUltimateCriteria()))
    }
}
