package com.example.skilltracker.domain.model

data class WellnessBreak(
    val date: String = "", // yyyy-MM-dd
    val completedBreaks: Int = 0,
    val totalBreaks: Int = 0
)

data class Exercise(
    val name: String,
    val description: String,
    val durationSeconds: Int,
    val iconName: String
)
