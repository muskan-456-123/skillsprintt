package com.example.skilltracker.domain.model

data class Streak(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String = "" // yyyy-MM-dd format
)
