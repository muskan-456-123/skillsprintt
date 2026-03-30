package com.example.skilltracker.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilltracker.domain.model.Course
import com.example.skilltracker.domain.model.Session
import com.example.skilltracker.domain.model.Streak
import com.example.skilltracker.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailySessionState(
    val course: Course? = null,
    val sessions: List<Session> = emptyList(),
    val currentDay: Int = 1,
    val streak: Streak = Streak(),
    val isLoading: Boolean = true,
    val showConfetti: Boolean = false,
    val showFeedbackSheet: Boolean = false,
    val completedSession: Session? = null,
    val showWellnessBreak: Boolean = false,
    val completedTodayCount: Int = 0
)

@HiltViewModel
class DailySessionViewModel @Inject constructor(
    private val getLatestCourseUseCase: GetLatestCourseUseCase,
    private val getAllSessionsUseCase: GetAllSessionsUseCase,
    private val completeSessionUseCase: CompleteSessionUseCase,
    private val adjustDifficultyUseCase: AdjustDifficultyUseCase,
    private val checkAndUpdateStreakUseCase: CheckAndUpdateStreakUseCase,
    private val getProgressUseCase: GetProgressUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DailySessionState())
    val state: StateFlow<DailySessionState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val course = getLatestCourseUseCase()
            if (course != null) {
                _state.value = _state.value.copy(course = course)

                // Load all sessions and determine current day
                getAllSessionsUseCase(course.id).collect { sessions ->
                    val completedCount = sessions.count { it.isCompleted }
                    val nextPending = sessions.firstOrNull { !it.isCompleted }
                    val currentDay = nextPending?.dayNumber ?: sessions.lastOrNull()?.dayNumber ?: 1
                    val todaySessions = sessions.filter { it.dayNumber == currentDay }

                    _state.value = _state.value.copy(
                        sessions = todaySessions,
                        currentDay = currentDay,
                        isLoading = false,
                        completedTodayCount = todaySessions.count { it.isCompleted }
                    )
                }
            } else {
                _state.value = _state.value.copy(isLoading = false)
            }

            // Check streak
            val streak = checkAndUpdateStreakUseCase()
            _state.value = _state.value.copy(streak = streak)
        }
    }

    fun onCompleteSession(session: Session) {
        viewModelScope.launch {
            val completed = completeSessionUseCase(session)

            // Update streak
            val streak = checkAndUpdateStreakUseCase()

            val todayCompleted = _state.value.completedTodayCount + 1
            val showWellness = todayCompleted % 2 == 0 && todayCompleted > 0

            _state.value = _state.value.copy(
                showConfetti = true,
                completedSession = completed,
                showFeedbackSheet = true,
                streak = streak,
                completedTodayCount = todayCompleted,
                showWellnessBreak = showWellness
            )
        }
    }

    fun onFeedbackSubmitted(feedback: String) {
        val session = _state.value.completedSession ?: return
        val courseId = _state.value.course?.id ?: return

        viewModelScope.launch {
            adjustDifficultyUseCase(session, feedback, courseId)
            _state.value = _state.value.copy(
                showFeedbackSheet = false,
                completedSession = null
            )
        }
    }

    fun onDismissFeedback() {
        _state.value = _state.value.copy(showFeedbackSheet = false)
    }

    fun onConfettiDone() {
        _state.value = _state.value.copy(showConfetti = false)
    }

    fun onDismissWellness() {
        _state.value = _state.value.copy(showWellnessBreak = false)
    }
}
