package com.example.skilltracker.data.repository

import com.example.skilltracker.data.local.dao.ChatMessageDao
import com.example.skilltracker.data.local.toDomain
import com.example.skilltracker.data.local.toEntity
import com.example.skilltracker.data.remote.GeminiService
import com.example.skilltracker.domain.model.ChatMessage
import com.example.skilltracker.domain.model.QuizQuestion
import com.example.skilltracker.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val geminiService: GeminiService,
    private val chatMessageDao: ChatMessageDao
) : AiRepository {

    override suspend fun generateQuizQuestions(
        skillName: String,
        courseName: String,
        startTime: String,
        endTime: String
    ): List<QuizQuestion> {
        return geminiService.generateQuizQuestions(skillName, courseName, startTime, endTime)
    }

    override fun sendChatMessage(
        skillName: String,
        courseName: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Flow<String> {
        return geminiService.sendChatMessage(skillName, courseName, history, userMessage)
    }

    override fun getChatHistory(courseId: String): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessagesByCourse(courseId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveChatMessage(courseId: String, message: ChatMessage) {
        chatMessageDao.insertMessage(message.toEntity(courseId))
    }

    override suspend fun clearChatHistory(courseId: String) {
        chatMessageDao.clearMessages(courseId)
    }
}
