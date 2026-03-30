package com.example.skilltracker.domain.usecase

import com.example.skilltracker.domain.model.Course
import com.example.skilltracker.domain.repository.CourseRepository
import com.example.skilltracker.util.Resource
import javax.inject.Inject

class SearchCoursesUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(skill: String): Resource<List<Course>> {
        return try {
            val courses = repository.searchYouTubeCourses(skill)
            Resource.Success(courses)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to search courses")
        }
    }
}

class SaveCourseUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(course: Course) = repository.saveCourse(course)
}

class GetLatestCourseUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke() = repository.getLatestCourse()
}

class GetCourseByIdUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(id: String) = repository.getCourseById(id)
}
