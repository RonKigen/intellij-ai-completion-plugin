package com.github.ronkigen.intellijaicompletionplugin.completion

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Service
class AIService {
    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(java.time.Duration.ofSeconds(10))
        .build()
    
    private val json = Json { ignoreUnknownKeys = true }
    
    suspend fun getCompletions(
        contextBefore: String,
        contextAfter: String,
        language: String,
        project: Project
    ): List<AICodeSuggestion> = withContext(Dispatchers.IO) {
        
        val settings = AISettings.getInstance()
        if (!settings.isEnabled || settings.apiKey.isEmpty()) {
            return@withContext emptyList()
        }
        
        val requestBody = buildJsonObject {
            putJsonObject("contents") {
                putJsonArray("parts") {
                    addJsonObject {
                        put("text", buildPrompt(contextBefore, contextAfter, language))
                    }
                }
            }
            putJsonObject("generationConfig") {
                put("temperature", settings.temperature)
                put("topP", 0.95)
                put("topK", 40)
                put("maxOutputTokens", settings.maxTokens)
                putJsonArray("stopSequences") {
                    add("\n\n")
                    add("```")
                }
            }
        }
        
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${settings.apiEndpoint}?key=${settings.apiKey}"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build()
        
        try {
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            
            if (response.statusCode() == 200) {
                parseGeminiCompletions(response.body())
            } else {
                throw Exception("API request failed with status ${response.statusCode()}")
            }
        } catch (e: Exception) {
            throw Exception("Failed to get AI completions: ${e.message}")
        }
    }
    
    private fun buildPrompt(contextBefore: String, contextAfter: String, language: String): String {
        return """
            You are an AI code completion assistant. Complete the code at the cursor position.
            
            Language: $language
            
            Code before cursor:
            ```$language
            $contextBefore
            ```
            
            Code after cursor:
            ```$language
            $contextAfter
            ```
            
            Provide only the code completion without explanations. The completion should:
            1. Be syntactically correct
            2. Follow the existing code style
            3. Be contextually appropriate
            4. Be concise and focused
            
            Completion:
        """.trimIndent()
    }
    
    private fun parseGeminiCompletions(responseBody: String): List<AICodeSuggestion> {
        return try {
            val response = json.decodeFromString<JsonObject>(responseBody)
            val candidates = response["candidates"]?.jsonArray ?: return emptyList()
            
            candidates.mapNotNull { candidate ->
                val content = candidate.jsonObject["content"]?.jsonObject
                val parts = content?.get("parts")?.jsonArray
                val text = parts?.firstOrNull()?.jsonObject?.get("text")?.jsonPrimitive?.content
                
                if (!text.isNullOrBlank()) {
                    val cleanText = text.trim()
                    AICodeSuggestion(
                        code = cleanText,
                        displayText = cleanText.lines().first(),
                        confidence = 0.8f
                    )
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
