package com.example.skilltracker.presentation.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilltracker.domain.model.QuizQuestion
import com.example.skilltracker.domain.usecase.GenerateQuizUseCase
import com.example.skilltracker.domain.usecase.GetCourseByIdUseCase
import com.example.skilltracker.domain.usecase.GetLatestSkillUseCase
import com.example.skilltracker.util.DateUtils
import com.example.skilltracker.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: String? = null,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val generateQuizUseCase: GenerateQuizUseCase,
    private val getCourseByIdUseCase: GetCourseByIdUseCase,
    private val getLatestSkillUseCase: GetLatestSkillUseCase
) : ViewModel() {

    private val sessionId = savedStateHandle.get<String>("sessionId") ?: ""
    private val courseId = savedStateHandle.get<String>("courseId") ?: ""

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    init {
        generateQuiz()
    }

    fun generateQuiz() {
        viewModelScope.launch {
            _state.value = QuizState(isLoading = true)
            val skill = getLatestSkillUseCase()
            val course = getCourseByIdUseCase(courseId)

            if (skill == null || course == null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Could not load course data"
                )
                return@launch
            }

            when (val result = generateQuizUseCase(
                skillName = skill.name,
                courseName = course.title,
                startTime = "0:00",
                endTime = DateUtils.formatMinutes(course.durationMinutes)
            )) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        questions = result.data,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onAnswerSelected(answer: String) {
        val currentQuestion = _state.value.questions.getOrNull(_state.value.currentIndex) ?: return
        val isCorrect = answer == currentQuestion.answer

        _state.value = _state.value.copy(
            selectedAnswer = answer,
            showResult = true,
            isCorrect = isCorrect,
            score = if (isCorrect) _state.value.score + 1 else _state.value.score
        )
    }

    fun onNextQuestion() {
        val nextIndex = _state.value.currentIndex + 1
        if (nextIndex >= _state.value.questions.size) {
            _state.value = _state.value.copy(isFinished = true)
        } else {
            _state.value = _state.value.copy(
                currentIndex = nextIndex,
                selectedAnswer = null,
                showResult = false,
                isCorrect = false
            )
        }
    }
}
