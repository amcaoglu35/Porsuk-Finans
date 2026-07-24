package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.AiWorkspaceDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiWorkspaceDaoModule {

    @Provides
    fun provideAiWorkspaceDao(db: PorsukDatabase): AiWorkspaceDao {
        return db.aiWorkspaceDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AiFoundationRepositoriesModule {

    @Provides
    @Singleton
    fun provideAIRepository(impl: AIRepositoryImpl): AIRepository = impl

    @Provides
    @Singleton
    fun providePromptRepository(impl: PromptRepositoryImpl): PromptRepository = impl

    @Provides
    @Singleton
    fun provideContextRepository(impl: ContextRepositoryImpl): ContextRepository = impl

    @Provides
    @Singleton
    fun provideMemoryRepository(impl: MemoryRepositoryImpl): MemoryRepository = impl

    @Provides
    @Singleton
    fun provideProviderRepository(impl: ProviderRepositoryImpl): ProviderRepository = impl
}

@Module
@InstallIn(SingletonComponent::class)
object AiWorkspaceRepositoriesModule {

    @Provides
    @Singleton
    fun provideAIWorkspaceRepository(impl: AIWorkspaceRepositoryImpl): AIWorkspaceRepository = impl

    @Provides
    @Singleton
    fun provideChatRepository(impl: ChatRepositoryImpl): ChatRepository = impl

    @Provides
    @Singleton
    fun provideAiAnalysisQueryRepository(impl: AiAnalysisQueryRepositoryImpl): AiAnalysisQueryRepository = impl

    @Provides
    @Singleton
    fun providePromptLibraryRepository(impl: PromptLibraryRepositoryImpl): PromptLibraryRepository = impl

    @Provides
    @Singleton
    fun provideWorkspaceRepository(impl: WorkspaceRepositoryImpl): WorkspaceRepository = impl
}
