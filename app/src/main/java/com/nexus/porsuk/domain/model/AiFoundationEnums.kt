package com.nexus.porsuk.domain.model

/**
 * 7 Yapay Zekâ Sağlayıcı Türü (AI Provider Types)
 */
enum class AiProviderType(val displayName: String) {
    OPENAI("OpenAI (GPT-4o)"),
    GEMINI("Google Gemini (1.5 Pro)"),
    CLAUDE("Anthropic Claude (3.5 Sonnet)"),
    AZURE_OPENAI("Azure OpenAI Enterprise"),
    OLLAMA("Ollama Yerel AI (Llama 3)"),
    OPENROUTER("OpenRouter Unified API"),
    CUSTOM("Özel Kurumsal AI Model");
}

/**
 * 6 AI İstemci Kipi (AI Client Modes)
 */
enum class AiClientMode(val displayName: String) {
    CHAT("Sohbet İstemcisi (Chat)"),
    COMPLETION("Tamamlama İstemcisi (Completion)"),
    EMBEDDING("Vektör İstemcisi (Embedding)"),
    VISION("Görsel Analiz (Vision)"),
    TOOL_CALLING("Araç Çağırma (Tool Calling)"),
    STREAMING("Akış İstemcisi (Streaming)");
}
