package com.example.skilltracker.presentation.course

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilltracker.domain.model.Course
import com.example.skilltracker.domain.usecase.SaveCourseUseCase
import com.example.skilltracker.domain.usecase.SearchCoursesUseCase
import com.example.skilltracker.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CoursePickerState(
    val skillName: String = "",
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedCourse: Course? = null,
    val showBottomSheet: Boolean = false,
    val navigateToPlanSetup: String? = null
)

@HiltViewModel
class CoursePickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val searchCoursesUseCase: SearchCoursesUseCase,
    private val saveCourseUseCase: SaveCourseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CoursePickerState())
    val state: StateFlow<CoursePickerState> = _state.asStateFlow()

    init {
        val skillName = savedStateHandle.get<String>("skillName") ?: ""
        _state.value = _state.value.copy(skillName = skillName)
        searchCourses()
    }

    fun searchCourses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = searchCoursesUseCase(_state.value.skillName)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        courses = result.data,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> { }
            }
        }
    }

    fun onCourseClicked(course: Course) {
        _state.value = _state.value.copy(
            selectedCourse = course,
            showBottomSheet = true
        )
    }

    fun onDismissBottomSheet() {
        _state.value = _state.value.copy(showBottomSheet = false)
    }

    fun onSelectCourse() {
        val course = _state.value.selectedCourse ?: return
        viewModelScope.launch {
            val courseWithSkill = course.copy(skill = _state.value.skillName)
            saveCourseUseCase(courseWithSkill)
            _state.value = _state.value.copy(
                showBottomSheet = false,
                navigateToPlanSetup = courseWithSkill.id
            )
        }
    }

    fun onNavigationHandled() {
        _state.value = _state.value.copy(navigateToPlanSetup = null)
    }
}
