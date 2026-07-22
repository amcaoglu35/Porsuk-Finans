package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.remote.SourceReliabilityEngine

class NewsAgent : PorsukAgent {
    override val agentName: String = "News Agent"

    override suspend fun runAnalysis(request: AgentRequest): String {
        if (request.newsTitles.isEmpty()) {
            return "Kritik yeni haber akışı bulunmuyor. Piyasa duyarlılığı nötr."
        }
        val count = request.newsTitles.size
        val headlinesFormatted = request.newsTitles.take(3).joinToString("\n") { title ->
            val trustInfo = SourceReliabilityEngine.getReliabilityInfo(title)
            "• [${trustInfo.sourceName} | Güven: %${trustInfo.trustScore}]: \"$title\""
        }
        return "Son $count haber ağırlıklandırıldı:\n$headlinesFormatted"
    }
}
