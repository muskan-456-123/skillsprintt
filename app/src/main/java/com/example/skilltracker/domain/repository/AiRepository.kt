package com.example.skilltracker.domain.repository

import com.example.skilltracker.domain.model.ChatMessage
import com.example.skilltracker.domain.model.QuizQuestion
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    suspend fun generateQuizQuestions(
        skillName: String,
        courseName: String,
        startTime: String,
        endTime: String
    ): List<QuizQuestion>

    fun sendChatMessage(
        skillName: String,
        courseName: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Flow<String>

    fun getChatHistory(courseId: String): Flow<List<ChatMessage>>
    suspend fun saveChatMessage(courseId: String, message: ChatMessage)
    suspend fun clearChatHistory(courseId: String)
}
