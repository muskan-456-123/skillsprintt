package com.example.skilltracker.data.remote

import com.example.skilltracker.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub FirestoreService – all methods are no-ops.
 * TODO: Replace with real Firebase implementation once google-services.json is added.
 */
@Singleton
class FirestoreService @Inject constructor() {

    // Profile
    suspend fun saveProfile(name: String, email: String) { /* no-op */ }

    // Skill
    suspend fun saveSelectedSkill(skill: Skill) { /* no-op */ }

    // Course
    suspend fun saveCourse(course: Course) { /* no-op */ }

    // Sessions
    suspend fun saveSessions(courseId: String, sessions: List<Session>) { /* no-op */ }

    suspend fun updateSession(courseId: String, session: Session) { /* no-op */ }

    // Progress
    suspend fun updateProgress(courseId: String, progress: UserProgress) { /* no-op */ }

    // Streak
    suspend fun getStreak(): Streak = Streak(
        currentStreak = 0,
        longestStreak = 0,
        lastActiveDate = ""
    )

    suspend fun updateStreak(streak: Streak) { /* no-op */ }

    // Wellness
    suspend fun saveWellnessBreak(date: String, completedBreaks: Int, totalBreaks: Int) { /* no-op */ }

    // Real-time sessions listener
    fun observeSessions(courseId: String): Flow<List<Session>> = flowOf(emptyList())
}
