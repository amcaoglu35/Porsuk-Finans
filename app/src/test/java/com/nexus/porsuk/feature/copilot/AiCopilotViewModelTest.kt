package com.nexus.porsuk.feature.copilot

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Porsuk AI Copilot & Autonomous Investment Assistant — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AiCopilotViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeAIRepository = object : AiCopilotRepository {
        override fun streamCopilotResponse(prompt: String, provider: LlmProviderType) = flowOf(
            ChatMessage(sender = MessageSender.ASSISTANT, content = "Test AI Response")
        )
        override suspend fun getActiveProvider() = LlmProviderType.OPENAI
        override suspend fun setActiveProvider(provider: LlmProviderType) {}
    }

    private val fakeConversationRepository = object : ConversationRepository {
        override fun getConversationThreads() = flowOf(listOf(ConversationThread()))
        override fun getThreadMessages(threadId: String) = flowOf(listOf(ChatMessage(content = "Welcome")))
        override suspend fun createNewThread(title: String) = ConversationThread()
        override suspend fun saveMessage(threadId: String, message: ChatMessage) {}
    }

    private val fakeMemoryRepository = object : AiCopilotMemoryRepository {
        override fun getUserAiMemory() = flowOf(UserAiMemory())
        override suspend fun updateInvestmentGoal(goal: String) {}
        override suspend fun addFavoriteSymbol(symbol: String) {}
    }

    private val fakePromptRepository = object : AiCopilotPromptRepository {
        override fun getSystemPromptTemplates() = flowOf(emptyList<SystemPromptTemplate>())
        override suspend fun getFormattedPrompt(intent: CopilotIntent, contextData: String) = "Formatted Prompt"
    }

    private val fakeWorkflowRepository = object : AiCopilotWorkflowRepository {
        override suspend fun generateDailyBriefReport() = ChatMessage(content = "Daily Brief")
        override suspend fun generatePortfolioHealthCheck() = ChatMessage(content = "Health Check")
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCopilotData updates uiState with threads, initial messages, and memory`() = runTest {
        val viewModel = AiCopilotViewModel(
            aiRepository = fakeAIRepository,
            conversationRepository = fakeConversationRepository,
            memoryRepository = fakeMemoryRepository,
            promptRepository = fakePromptRepository,
            workflowRepository = fakeWorkflowRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(LlmProviderType.OPENAI, state.activeProvider)
        assertEquals(1, state.threads.size)
        assertEquals(1, state.messages.size)
        assertEquals("Welcome", state.messages[0].content)
        assertEquals(false, state.isLoading)
        assertNotNull(state.userMemory)
    }

    @Test
    fun `sendUserPrompt updates messages and triggers streamed response`() = runTest {
        val viewModel = AiCopilotViewModel(
            aiRepository = fakeAIRepository,
            conversationRepository = fakeConversationRepository,
            memoryRepository = fakeMemoryRepository,
            promptRepository = fakePromptRepository,
            workflowRepository = fakeWorkflowRepository
        )

        viewModel.sendUserPrompt("Analyze THYAO")
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.messages.size) // Welcome + User Prompt + Assistant Response
        assertEquals("Analyze THYAO", state.messages[1].content)
        assertEquals("Test AI Response", state.messages[2].content)
    }
}
