package com.example.skilltracker.domain.repository

import com.example.skilltracker.domain.model.Streak

interface StreakRepository {
    suspend fun getStreak(): Streak
    suspend fun updateStreak(streak: Streak)
    suspend fun checkAndUpdateStreak(): Streak
}
