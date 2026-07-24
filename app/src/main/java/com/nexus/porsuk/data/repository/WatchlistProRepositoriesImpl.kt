package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.WatchlistProDao
import com.nexus.porsuk.data.local.entity.WatchlistGroupEntity
import com.nexus.porsuk.data.local.entity.WatchlistItemProEntity
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.SmartCategory
import com.nexus.porsuk.domain.model.WatchlistGroup
import com.nexus.porsuk.domain.model.WatchlistItemPro
import com.nexus.porsuk.domain.repository.FavoriteRepository
import com.nexus.porsuk.domain.repository.WatchlistItemProRepository
import com.nexus.porsuk.domain.repository.WatchlistProRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistProRepositoryImpl @Inject constructor(
    private val dao: WatchlistProDao,
    private val logger: DataLogger
) : WatchlistProRepository {

    override fun getAllWatchlistGroups(): Flow<List<WatchlistGroup>> {
        return dao.getAllWatchlistGroups().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getFavoriteGroups(): Flow<List<WatchlistGroup>> {
        return dao.getFavoriteWatchlistGroups().map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun createWatchlistGroup(title: String, smartCategory: SmartCategory?): String {
        val newId = UUID.randomUUID().toString()
        val entity = WatchlistGroupEntity(
            groupId = newId,
            title = title,
            smartCategory = smartCategory?.name
        )
        dao.insertWatchlistGroup(entity)
        logger.logSyncEvent("WatchlistPro", "Yeni takip listesi oluşturuldu: $title ($newId)")
        return newId
    }

    override suspend fun updateWatchlistGroup(group: WatchlistGroup) {
        dao.updateWatchlistGroup(group.toEntityModel())
    }

    override suspend fun deleteWatchlistGroup(groupId: String) {
        val group = dao.getWatchlistGroupById(groupId).first()
        if (group != null) {
            dao.deleteWatchlistGroup(group)
            logger.logSyncEvent("WatchlistPro", "Takip listesi silindi: ${group.title}")
        }
    }

    override suspend fun setFavoriteGroup(groupId: String, isFavorite: Boolean) {
        val group = dao.getWatchlistGroupById(groupId).first()
        if (group != null) {
            dao.updateWatchlistGroup(group.copy(isFavorite = isFavorite))
        }
    }
}

@Singleton
class WatchlistItemProRepositoryImpl @Inject constructor(
    private val dao: WatchlistProDao,
    private val logger: DataLogger
) : WatchlistItemProRepository {

    override fun getItemsForGroup(groupId: String): Flow<List<WatchlistItemPro>> {
        return dao.getItemsForGroup(groupId).map { list -> list.map { it.toDomainModel() } }
    }

    override fun isSymbolInGroup(groupId: String, symbol: String): Flow<Boolean> {
        return dao.isSymbolInGroup(groupId, symbol)
    }

    override suspend fun addItemToGroup(groupId: String, symbol: String, notes: String?, tags: List<String>) {
        val entity = WatchlistItemProEntity(
            groupId = groupId,
            symbol = symbol,
            notes = notes,
            tags = tags.joinToString(",")
        )
        dao.insertWatchlistItem(entity)
    }

    override suspend fun addBulkItemsToGroup(groupId: String, symbols: List<String>) {
        val entities = symbols.map { sym ->
            WatchlistItemProEntity(
                groupId = groupId,
                symbol = sym
            )
        }
        dao.insertWatchlistItems(entities)
        logger.logSyncEvent("WatchlistPro", "${symbols.size} sembol toplu olarak listeye eklendi ($groupId)")
    }

    override suspend fun removeItemsFromGroup(groupId: String, symbols: List<String>) {
        dao.deleteItemsFromGroup(groupId, symbols)
        logger.logSyncEvent("WatchlistPro", "${symbols.size} sembol listeden silindi ($groupId)")
    }

    override suspend fun updateItemNotesAndTags(groupId: String, symbol: String, notes: String?, tags: List<String>) {
        val item = dao.getItemsForGroup(groupId).first().firstOrNull { it.symbol == symbol }
        if (item != null) {
            dao.insertWatchlistItem(item.copy(notes = notes, tags = tags.joinToString(",")))
        }
    }
}

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val dao: WatchlistProDao
) : FavoriteRepository {

    companion object {
        private const val DEFAULT_FAV_GROUP_ID = "default_favorites_group"
    }

    override fun getFavoriteSymbols(): Flow<Set<String>> {
        return dao.getItemsForGroup(DEFAULT_FAV_GROUP_ID).map { list ->
            list.map { it.symbol }.toSet()
        }
    }

    override suspend fun toggleFavoriteSymbol(symbol: String) {
        val isFav = dao.isSymbolInGroup(DEFAULT_FAV_GROUP_ID, symbol).first()
        if (isFav) {
            dao.deleteItemsFromGroup(DEFAULT_FAV_GROUP_ID, listOf(symbol))
        } else {
            // Önce varsayılan grubu oluştur
            dao.insertWatchlistGroup(
                WatchlistGroupEntity(groupId = DEFAULT_FAV_GROUP_ID, title = "Favoriler", isFavorite = true)
            )
            dao.insertWatchlistItem(
                WatchlistItemProEntity(groupId = DEFAULT_FAV_GROUP_ID, symbol = symbol)
            )
        }
    }
}

// Mappers
private fun WatchlistGroupEntity.toDomainModel() = WatchlistGroup(
    groupId = groupId,
    title = title,
    isFavorite = isFavorite,
    smartCategory = SmartCategory.fromString(smartCategory),
    createdAt = createdAt
)

private fun WatchlistGroup.toEntityModel() = WatchlistGroupEntity(
    groupId = groupId,
    title = title,
    isFavorite = isFavorite,
    smartCategory = smartCategory?.name,
    createdAt = createdAt
)

private fun WatchlistItemProEntity.toDomainModel() = WatchlistItemPro(
    itemId = itemId,
    groupId = groupId,
    symbol = symbol,
    name = symbol,
    category = AssetCategory.fromSymbol(symbol),
    notes = notes,
    tags = if (tags.isBlank()) emptyList() else tags.split(","),
    addedAt = addedAt
)
