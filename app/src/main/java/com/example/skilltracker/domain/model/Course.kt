package com.example.skilltracker.domain.model

data class Course(
    val id: String = "",
    val videoId: String = "",
    val title: String = "",
    val channelName: String = "",
    val thumbnailUrl: String = "",
    val durationMinutes: Int = 0,
    val durationFormatted: String = "",
    val skill: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val totalDays: Int = 0,
    val dailyMinutes: Int = 0
)
