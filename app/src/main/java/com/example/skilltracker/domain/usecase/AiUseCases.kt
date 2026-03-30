package com.example.skilltracker.domain.usecase

import com.example.skilltracker.domain.model.ChatMessage
import com.example.skilltracker.domain.model.QuizQuestion
import com.example.skilltracker.domain.repository.AiRepository
import com.example.skilltracker.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateQuizUseCase @Inject constructor(
    private val repository: AiRepository
) {
    suspend operator fun invoke(
        skillName: String,
        courseName: String,
        startTime: String,
        endTime: String
    ): Resource<List<QuizQuestion>> {
        return try {
            val questions = repository.generateQuizQuestions(skillName, courseName, startTime, endTime)
            if (questions.isNotEmpty()) {
                Resource.Success(questions)
            } else {
                Resource.Error("Failed to generate quiz questions")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Quiz generation failed")
        }
    }
}

class SendChatMessageUseCase @Inject constructor(
    private val repository: AiRepository
) {
    operator fun invoke(
        skillName: String,
        courseName: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Flow<String> {
        return repository.sendChatMessage(skillName, courseName, history, userMessage)
    }
}

class GetChatHistoryUseCase @Inject constructor(
    private val repository: AiRepository
) {
    operator fun invoke(courseId: String): Flow<List<ChatMessage>> {
        return repository.getChatHistory(courseId)
    }
}

class SaveChatMessageUseCase @Inject constructor(
    private val repository: AiRepository
) {
    suspend operator fun invoke(courseId: String, message: ChatMessage) {
        repository.saveChatMessage(courseId, message)
    }
}

class ClearChatHistoryUseCase @Inject constructor(
    private val repository: AiRepository
) {
    suspend operator fun invoke(courseId: String) {
        repository.clearChatHistory(courseId)
    }
}
