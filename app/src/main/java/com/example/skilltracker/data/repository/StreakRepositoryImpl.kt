package com.example.skilltracker.data.repository

import com.example.skilltracker.data.remote.FirestoreService
import com.example.skilltracker.domain.model.Streak
import com.example.skilltracker.domain.repository.StreakRepository
import com.example.skilltracker.util.DateUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
) : StreakRepository {

    override suspend fun getStreak(): Streak {
        return try {
            firestoreService.getStreak()
        } catch (_: Exception) {
            Streak()
        }
    }

    override suspend fun updateStreak(streak: Streak) {
        try {
            firestoreService.updateStreak(streak)
        } catch (_: Exception) { }
    }

    override suspend fun checkAndUpdateStreak(): Streak {
        val current = getStreak()
        val today = DateUtils.today()

        // Already active today
        if (current.lastActiveDate == today) return current

        val newStreak = when {
            DateUtils.isYesterday(current.lastActiveDate) -> {
                // Continue streak
                val newCurrent = current.currentStreak + 1
                current.copy(
                    currentStreak = newCurrent,
                    longestStreak = maxOf(current.longestStreak, newCurrent),
                    lastActiveDate = today
                )
            }
            current.lastActiveDate.isEmpty() -> {
                // First ever activity
                current.copy(
                    currentStreak = 1,
                    longestStreak = maxOf(current.longestStreak, 1),
                    lastActiveDate = today
                )
            }
            else -> {
                // Streak broken
                current.copy(
                    currentStreak = 1,
                    lastActiveDate = today
                )
            }
        }
        updateStreak(newStreak)
        return newStreak
    }
}
