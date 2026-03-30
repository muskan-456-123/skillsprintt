package com.example.skilltracker.domain.model

data class Session(
    val id: String = "",
    val courseId: String = "",
    val dayNumber: Int = 0,
    val sessionNumber: Int = 0,
    val startMinute: Int = 0,
    val endMinute: Int = 0,
    val durationMinutes: Int = 0,
    val isCompleted: Boolean = false,
    val isSkipped: Boolean = false,
    val difficulty: String = "normal", // easy, normal, hard
    val feedback: String = "", // too_easy, just_right, too_hard
    val quizScore: Int = -1, // -1 = not taken
    val completedAt: Long = 0L
)
