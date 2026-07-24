package com.nexus.porsuk.di

import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiCopilotEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideAiCopilotRepository(impl: AiCopilotRepositoryImpl): AiCopilotRepository = impl

    @Provides
    @Singleton
    fun provideConversationRepository(impl: ConversationRepositoryImpl): ConversationRepository = impl

    @Provides
    @Singleton
    fun provideAiCopilotMemoryRepository(impl: AiCopilotMemoryRepositoryImpl): AiCopilotMemoryRepository = impl

    @Provides
    @Singleton
    fun provideAiCopilotPromptRepository(impl: AiCopilotPromptRepositoryImpl): AiCopilotPromptRepository = impl

    @Provides
    @Singleton
    fun provideAiCopilotWorkflowRepository(impl: AiCopilotWorkflowRepositoryImpl): AiCopilotWorkflowRepository = impl
}
