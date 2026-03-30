package com.example.skilltracker.domain.model

data class UserProgress(
    val courseId: String = "",
    val completedSessions: Int = 0,
    val totalSessions: Int = 0,
    val percentComplete: Float = 0f,
    val totalMinutesStudied: Int = 0,
    val daysRemaining: Int = 0
)
