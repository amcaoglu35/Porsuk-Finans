package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiCopilotRepositoryImpl @Inject constructor() : AiCopilotRepository {
    private val activeProviderState = MutableStateFlow(LlmProviderType.OPENAI)

    override fun streamCopilotResponse(prompt: String, provider: LlmProviderType): Flow<ChatMessage> = flow {
        val simulatedTokens = listOf(
            "## 🤖 Porsuk AI Copilot Analizi\n\n",
            "Sorduğunuz **'$prompt'** istemine binaen ",
            "portföy verileriniz ve güncel piyasa dinamikleri incelemiştir.\n\n",
            "### 📈 Öne Çıkan Değerlendirmeler:\n",
            "• **Portföy Yoğunlaşması:** Portföyünüzde Havacılık sektörü %35 ağırlığa sahiptir.\n",
            "• **Değerleme Oranları:** BIST 100 ortalama F/K oranı 7.4 seviyesindedir.\n",
            "• **Temettü Verimi:** Yıllık tahmini temettü veriminiz %8.2 olarak hesaplanmıştır.\n\n",
            "> 💡 **Öneri:** Risk dengesini artırmak için Sanayi ve Teknoloji sektörlerinde kademeli alım yapabilirsiniz."
        )

        var accumulatedContent = ""
        for (token in simulatedTokens) {
            delay(150)
            accumulatedContent += token
            emit(
                ChatMessage(
                    sender = MessageSender.ASSISTANT,
                    content = accumulatedContent,
                    providerUsed = provider,
                    isStreaming = token != simulatedTokens.last()
                )
            )
        }
    }

    override suspend fun getActiveProvider(): LlmProviderType = activeProviderState.value

    override suspend fun setActiveProvider(provider: LlmProviderType) {
        activeProviderState.value = provider
    }
}

@Singleton
class ConversationRepositoryImpl @Inject constructor() : ConversationRepository {
    private val defaultThreads = listOf(
        ConversationThread(
            threadId = "thread_default",
            title = "BIST 100 Risk & Portföy Analizi",
            lastMessageSnippet = "Portföyün %35 Havacılık yoğunlaşması içeriyor..."
        ),
        ConversationThread(
            threadId = "thread_dividend",
            title = "Temettü Verimi & Nakit Akışı",
            lastMessageSnippet = "Yıllık tahmini temettü ödemeniz 42.500 TL..."
        )
    )

    private val threadsState = MutableStateFlow(defaultThreads)
    private val messagesMap = mutableMapOf<String, MutableList<ChatMessage>>(
        "thread_default" to mutableListOf(
            ChatMessage(
                sender = MessageSender.ASSISTANT,
                content = "Merhaba! Ben **Porsuk AI Copilot**. Yatırım portföyünüz, hisse analizleri ve makro ekonomik gelişmeler hakkında sorularınızı yanıtlamaya hazırım."
            )
        )
    )

    override fun getConversationThreads(): Flow<List<ConversationThread>> = threadsState.asStateFlow()

    override fun getThreadMessages(threadId: String): Flow<List<ChatMessage>> = flow {
        emit(messagesMap[threadId] ?: emptyList())
    }

    override suspend fun createNewThread(title: String): ConversationThread {
        val newThread = ConversationThread(
            threadId = "thread_${System.currentTimeMillis()}",
            title = title
        )
        threadsState.update { current -> listOf(newThread) + current }
        messagesMap[newThread.threadId] = mutableListOf()
        return newThread
    }

    override suspend fun saveMessage(threadId: String, message: ChatMessage) {
        val list = messagesMap.getOrPut(threadId) { mutableListOf() }
        list.add(message)
    }
}

@Singleton
class AiCopilotMemoryRepositoryImpl @Inject constructor() : AiCopilotMemoryRepository {
    private val memoryState = MutableStateFlow(UserAiMemory())

    override fun getUserAiMemory(): Flow<UserAiMemory> = memoryState.asStateFlow()

    override suspend fun updateInvestmentGoal(goal: String) {
        memoryState.update { it.copy(investmentGoal = goal) }
    }

    override suspend fun addFavoriteSymbol(symbol: String) {
        memoryState.update { it.copy(favoriteSymbols = it.favoriteSymbols + symbol) }
    }
}

@Singleton
class AiCopilotPromptRepositoryImpl @Inject constructor() : AiCopilotPromptRepository {
    private val templates = listOf(
        SystemPromptTemplate(
            promptId = "p_financial_expert",
            name = "Kurumsal Finans Uzmanı",
            templateText = "Sen Bloomberg Terminal ve CFA standartlarında analiz yapan bir Porsuk AI Copilot asistanısın."
        )
    )

    override fun getSystemPromptTemplates(): Flow<List<SystemPromptTemplate>> = flowOf(templates)

    override suspend fun getFormattedPrompt(intent: CopilotIntent, contextData: String): String {
        return "${intent.promptTemplate}\n\n[Mevcut Portföy Bağlamı: $contextData]"
    }
}

@Singleton
class AiCopilotWorkflowRepositoryImpl @Inject constructor() : AiCopilotWorkflowRepository {
    override suspend fun generateDailyBriefReport(): ChatMessage {
        return ChatMessage(
            sender = MessageSender.ASSISTANT,
            content = "## 📰 Günlük Borsa & Piyasa Özeti (24 Temmuz 2026)\n\n• **BIST 100:** 10.850 puanda (+%1.41)\n• **Dolar/TL:** 32.50 (+%0.31)\n• **Öne Çıkan Hisse:** THYAO bilançosu beklentileri aştı."
        )
    }

    override suspend fun generatePortfolioHealthCheck(): ChatMessage {
        return ChatMessage(
            sender = MessageSender.ASSISTANT,
            content = "## 🩺 Portföy Sağlık Raporu\n\n• **Çeşitlendirme Skoru:** 88 / 100 (Optimal)\n• **Sektörel Yoğunlaşma:** Havacılık %35 (Orta Risk)"
        )
    }
}
