package com.example.skilltracker.presentation.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilltracker.domain.model.ChatMessage
import com.example.skilltracker.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatbotState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isTyping: Boolean = false,
    val skillName: String = "",
    val courseName: String = "",
    val courseId: String = ""
)

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val saveChatMessageUseCase: SaveChatMessageUseCase,
    private val clearChatHistoryUseCase: ClearChatHistoryUseCase,
    private val getLatestCourseUseCase: GetLatestCourseUseCase,
    private val getLatestSkillUseCase: GetLatestSkillUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatbotState())
    val state: StateFlow<ChatbotState> = _state.asStateFlow()

    init {
        loadContext()
    }

    private fun loadContext() {
        viewModelScope.launch {
            val skill = getLatestSkillUseCase()
            val course = getLatestCourseUseCase()
            if (skill != null && course != null) {
                _state.value = _state.value.copy(
                    skillName = skill.name,
                    courseName = course.title,
                    courseId = course.id
                )

                getChatHistoryUseCase(course.id).collect { history ->
                    _state.value = _state.value.copy(messages = history)
                }
            }
        }
    }

    fun onInputChanged(text: String) {
        _state.value = _state.value.copy(inputText = text)
    }

    fun onSendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = text,
            isUser = true
        )

        viewModelScope.launch {
            // Save user message
            saveChatMessageUseCase(_state.value.courseId, userMessage)

            _state.value = _state.value.copy(
                inputText = "",
                isTyping = true
            )

            // Get AI response via streaming
            val aiMessageId = UUID.randomUUID().toString()
            var fullResponse = ""

            try {
                sendChatMessageUseCase(
                    skillName = _state.value.skillName,
                    courseName = _state.value.courseName,
                    history = _state.value.messages,
                    userMessage = text
                ).collect { chunk ->
                    fullResponse += chunk
                    // Update UI with streaming text
                    val currentMessages = _state.value.messages.toMutableList()
                    val existingAiIdx = currentMessages.indexOfLast { it.id == aiMessageId }
                    val aiMsg = ChatMessage(
                        id = aiMessageId,
                        content = fullResponse,
                        isUser = false
                    )
                    if (existingAiIdx >= 0) {
                        currentMessages[existingAiIdx] = aiMsg
                    } else {
                        currentMessages.add(aiMsg)
                    }
                    _state.value = _state.value.copy(messages = currentMessages)
                }

                // Save final AI message
                val finalAiMessage = ChatMessage(
                    id = aiMessageId,
                    content = fullResponse,
                    isUser = false
                )
                saveChatMessageUseCase(_state.value.courseId, finalAiMessage)
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    id = aiMessageId,
                    content = "Sorry, I couldn't process that. Please try again.",
                    isUser = false
                )
                saveChatMessageUseCase(_state.value.courseId, errorMsg)
            }

            _state.value = _state.value.copy(isTyping = false)
        }
    }

    fun onClearChat() {
        viewModelScope.launch {
            clearChatHistoryUseCase(_state.value.courseId)
            _state.value = _state.value.copy(messages = emptyList())
        }
    }
}
