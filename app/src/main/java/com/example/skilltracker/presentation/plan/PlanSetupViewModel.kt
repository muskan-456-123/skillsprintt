package com.example.skilltracker.presentation.plan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilltracker.domain.model.Course
import com.example.skilltracker.domain.usecase.GenerateLearningPlanUseCase
import com.example.skilltracker.domain.usecase.GetCourseByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanSetupState(
    val course: Course? = null,
    val totalDays: Int = 14,
    val dailyHours: Float = 2f,
    val isGenerating: Boolean = false,
    val navigateToDashboard: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PlanSetupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCourseByIdUseCase: GetCourseByIdUseCase,
    private val generateLearningPlanUseCase: GenerateLearningPlanUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlanSetupState())
    val state: StateFlow<PlanSetupState> = _state.asStateFlow()

    init {
        val courseId = savedStateHandle.get<String>("courseId") ?: ""
        viewModelScope.launch {
            val course = getCourseByIdUseCase(courseId)
            course?.let {
                val suggestedDays = maxOf(1, it.durationMinutes / 60) // ~1 hour per day
                _state.value = _state.value.copy(
                    course = it,
                    totalDays = minOf(suggestedDays, 60)
                )
            }
        }
    }

    fun onDaysChanged(days: Int) {
        _state.value = _state.value.copy(totalDays = days.coerceIn(1, 60))
    }

    fun onDailyHoursChanged(hours: Float) {
        _state.value = _state.value.copy(dailyHours = hours)
    }

    fun onGeneratePlan() {
        val course = _state.value.course ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isGenerating = true, error = null)
            try {
                generateLearningPlanUseCase(
                    courseId = course.id,
                    totalMinutes = course.durationMinutes,
                    totalDays = _state.value.totalDays
                )
                _state.value = _state.value.copy(
                    isGenerating = false,
                    navigateToDashboard = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isGenerating = false,
                    error = e.message ?: "Failed to generate plan"
                )
            }
        }
    }

    fun onNavigationHandled() {
        _state.value = _state.value.copy(navigateToDashboard = false)
    }
}
