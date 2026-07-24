package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.ai.GeminiProviderStrategy
import com.nexus.porsuk.data.ai.PromptEngine
import com.nexus.porsuk.data.local.dao.AiWorkspaceDao
import com.nexus.porsuk.data.local.entity.AiWorkspaceEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIWorkspaceRepositoryImpl @Inject constructor(
    private val geminiProvider: GeminiProviderStrategy
) : AIWorkspaceRepository {

    override fun getWorkspaceReport(type: AiWorkspaceType): Flow<AiWorkspaceReport> = flow {
        val markdown = geminiProvider.generateCompletion("Generate ${type.displayName} executive report", null)
        emit(
            AiWorkspaceReport(
                title = "${type.displayName} Raporu",
                workspaceType = type,
                summaryMarkdown = markdown
            )
        )
    }
}

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val geminiProvider: GeminiProviderStrategy
) : ChatRepository {

    private val sampleMessages = mutableListOf(
        AiChatMessage(senderName = "Orakul AI", text = "Merhaba! Porsuk Orakul AI Finansal Asistanınız hizmetinizde. Portföyünüz, şirket analizleri veya piyasa durumu hakkında sorularınızı sorabilirsiniz.", isUser = false)
    )

    override fun getChatMessages(): Flow<List<AiChatMessage>> = flow {
        emit(sampleMessages)
    }

    override suspend fun sendMessage(userText: String): Flow<AiChatMessage> = flow {
        val aiReplyText = geminiProvider.generateCompletion(userText, null)
        val aiMsg = AiChatMessage(senderName = "Orakul AI", text = aiReplyText, isUser = false)
        sampleMessages.add(AiChatMessage(senderName = "Siz", text = userText, isUser = true))
        sampleMessages.add(aiMsg)
        emit(aiMsg)
    }
}

@Singleton
class AiAnalysisQueryRepositoryImpl @Inject constructor() : AiAnalysisQueryRepository {
    override fun parseNaturalLanguageQuery(queryText: String): Flow<NaturalLanguageScanQuery> = flow {
        emit(
            NaturalLanguageScanQuery(
                userQueryText = queryText,
                parsedCriteriaText = "Orakul AI Filtresi: Dividend Yield > %5.0, Risk Level = LOW",
                matchedCount = 8
            )
        )
    }
}

@Singleton
class PromptLibraryRepositoryImpl @Inject constructor(
    private val promptEngine: PromptEngine
) : PromptLibraryRepository {
    override fun getPromptTemplates(): Flow<List<AiPromptTemplate>> = flow {
        emit(promptEngine.getDefaultTemplates())
    }
}

@Singleton
class WorkspaceRepositoryImpl @Inject constructor(
    private val dao: AiWorkspaceDao
) : WorkspaceRepository {

    override fun getSavedReports(): Flow<List<AiWorkspaceReport>> {
        return dao.getAllSavedRecords().map { list ->
            list.map { entity ->
                AiWorkspaceReport(
                    reportId = entity.recordId,
                    title = entity.title,
                    workspaceType = AiWorkspaceType.valueOf(entity.workspaceType),
                    summaryMarkdown = entity.content
                )
            }
        }
    }
}
