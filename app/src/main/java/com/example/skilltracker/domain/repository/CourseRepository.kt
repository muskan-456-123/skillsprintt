package com.example.skilltracker.domain.repository

import com.example.skilltracker.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getAllCourses(): Flow<List<Course>>
    suspend fun getLatestCourse(): Course?
    suspend fun getCourseById(id: String): Course?
    suspend fun saveCourse(course: Course)
    suspend fun searchYouTubeCourses(skill: String): List<Course>
    suspend fun getVideoDetails(videoId: String): Course?
}
