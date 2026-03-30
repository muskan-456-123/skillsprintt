package com.example.skilltracker.presentation.progress

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

data class ProgressState(
    val course: Course? = null,
    val allSessions: List<Session> = emptyList(),
    val completedSessions: Int = 0,
    val totalSessions: Int = 0,
    val percentComplete: Float = 0f,
    val totalMinutesStudied: Int = 0,
    val daysRemaining: Int = 0,
    val streak: Streak = Streak(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val getLatestCourseUseCase: GetLatestCourseUseCase,
    private val getAllSessionsUseCase: GetAllSessionsUseCase,
    private val getProgressUseCase: GetProgressUseCase,
    private val getTotalMinutesStudiedUseCase: GetTotalMinutesStudiedUseCase,
    private val getStreakUseCase: GetStreakUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    init {
        loadProgress()
    }

    private fun loadProgress() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val course = getLatestCourseUseCase()
            if (course != null) {
                _state.value = _state.value.copy(course = course)

                val (completed, total) = getProgressUseCase(course.id)
                val minutesStudied = getTotalMinutesStudiedUseCase(course.id)
                val streak = getStreakUseCase()
                val percent = if (total > 0) completed.toFloat() / total else 0f

                // Calculate days remaining
                val maxDay = total // approximate
                val currentDay = completed // approximate
                val daysRemaining = maxOf(0, maxDay - currentDay)

                getAllSessionsUseCase(course.id).collect { sessions ->
                    _state.value = _state.value.copy(
                        allSessions = sessions,
                        completedSessions = completed,
                        totalSessions = total,
                        percentComplete = percent,
                        totalMinutesStudied = minutesStudied,
                        daysRemaining = daysRemaining,
                        streak = streak,
                        isLoading = false
                    )
                }
            } else {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
