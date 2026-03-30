package com.example.skilltracker.data.remote

import com.example.skilltracker.domain.model.ChatMessage
import com.example.skilltracker.domain.model.QuizQuestion
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor(
    private val generativeModel: GenerativeModel
) {
    suspend fun generateQuizQuestions(
        skillName: String,
        courseName: String,
        startTime: String,
        endTime: String
    ): List<QuizQuestion> {
        val prompt = """
            Generate 5 multiple choice questions for a $skillName beginner who 
            just studied the section from $startTime to $endTime of $courseName. 
            Return ONLY a JSON array with no other text: 
            [{"question": "...", "options": ["A", "B", "C", "D"], "answer": "A"}]
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)
        val jsonText = response.text?.trim() ?: return emptyList()

        // Remove markdown code fences if present
        val cleanJson = jsonText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return try {
            val type = object : TypeToken<List<QuizQuestion>>() {}.type
            Gson().fromJson(cleanJson, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun sendChatMessage(
        skillName: String,
        courseName: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Flow<String> = flow {
        val systemPrompt = """
            You are a helpful AI tutor for $skillName. The user is currently learning 
            $courseName. Answer questions clearly, give examples, and help debug problems. 
            Keep answers concise and beginner-friendly.
        """.trimIndent()

        val chatContent = buildString {
            appendLine(systemPrompt)
            appendLine()
            history.takeLast(10).forEach { msg ->
                val role = if (msg.isUser) "User" else "Assistant"
                appendLine("$role: ${msg.content}")
            }
            appendLine("User: $userMessage")
            appendLine("Assistant:")
        }

        val response = generativeModel.generateContentStream(content { text(chatContent) })
        response.collect { chunk ->
            chunk.text?.let { emit(it) }
        }
    }
}
