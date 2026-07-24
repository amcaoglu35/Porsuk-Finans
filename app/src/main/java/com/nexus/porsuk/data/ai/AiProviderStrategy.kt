package com.nexus.porsuk.data.ai

import com.nexus.porsuk.domain.model.AiClientMode
import com.nexus.porsuk.domain.model.AiContextFrame
import com.nexus.porsuk.domain.model.AiProviderType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Orakul AI Foundation Platform — Soyut Provider Strateji Arayüzü (AiProviderStrategy)
 *
 * Provider Pattern + Strategy Pattern: OpenAI, Gemini, Claude, Ollama ve kurumsal modeller bu arayüz uygulanarak eklenir.
 * Hiçbir dış kütüphaneye veya AI firmasına sıkı sıkıya bağımlı değildir.
 */
interface AiProviderStrategy {
    fun getProviderType(): AiProviderType
    fun generateCompletion(prompt: String, context: AiContextFrame?): String
}

/**
 * Google Gemini Provider Somut Sınıfı (GeminiProviderStrategy)
 */
@Singleton
class GeminiProviderStrategy @Inject constructor() : AiProviderStrategy {
    override fun getProviderType() = AiProviderType.GEMINI

    override fun generateCompletion(prompt: String, context: AiContextFrame?): String {
        return "Orakul AI (Gemini 1.5 Pro): Sorunuz finansal modeller ve bilanço oranları çerçevesinde incelenmiştir."
    }
}

/**
 * OpenAI Provider Somut Sınıfı (OpenAiProviderStrategy)
 */
@Singleton
class OpenAiProviderStrategy @Inject constructor() : AiProviderStrategy {
    override fun getProviderType() = AiProviderType.OPENAI

    override fun generateCompletion(prompt: String, context: AiContextFrame?): String {
        return "Orakul AI (OpenAI GPT-4o): Sorunuz finansal veri akışlarımız üzerinden değerlendirilmiştir."
    }
}
