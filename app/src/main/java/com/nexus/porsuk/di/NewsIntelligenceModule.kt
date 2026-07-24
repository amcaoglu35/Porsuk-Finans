package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.NewsIntelligenceDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsIntelligenceDaoModule {

    @Provides
    fun provideNewsIntelligenceDao(db: PorsukDatabase): NewsIntelligenceDao {
        return db.newsIntelligenceDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NewsIntelligenceRepositoriesModule {

    @Provides
    @Singleton
    fun provideNewsIntelligenceRepository(impl: NewsIntelligenceRepositoryImpl): NewsIntelligenceRepository = impl

    @Provides
    @Singleton
    fun provideNewsCategoryRepository(impl: NewsCategoryRepositoryImpl): NewsCategoryRepository = impl

    @Provides
    @Singleton
    fun provideNewsSourceRepository(impl: NewsSourceRepositoryImpl): NewsSourceRepository = impl
}
