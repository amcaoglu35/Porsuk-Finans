package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.TefasFundDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TefasEngineModule {

    @Provides
    fun provideTefasFundDao(db: PorsukDatabase): TefasFundDao {
        return db.tefasFundDao()
    }
}
