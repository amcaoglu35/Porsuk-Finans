package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.TefasFundDao
import com.nexus.porsuk.data.remote.datasource.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TefasEngineModule {

    @Provides
    fun provideTefasFundDao(db: PorsukDatabase): TefasFundDao {
        return db.tefasFundDao()
    }

    @Provides
    @Singleton
    fun provideTefasRemoteDataSource(impl: TefasRemoteDataSourceImpl): TefasRemoteDataSource = impl

    @Provides
    @Singleton
    fun provideTefasEngineRemoteDataSource(impl: TefasEngineRemoteDataSourceImpl): TefasEngineRemoteDataSource = impl
}
