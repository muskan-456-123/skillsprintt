package com.example.skilltracker.domain.usecase

import com.example.skilltracker.domain.model.Streak
import com.example.skilltracker.domain.repository.StreakRepository
import javax.inject.Inject

class GetStreakUseCase @Inject constructor(
    private val repository: StreakRepository
) {
    suspend operator fun invoke(): Streak = repository.getStreak()
}

class CheckAndUpdateStreakUseCase @Inject constructor(
    private val repository: StreakRepository
) {
    suspend operator fun invoke(): Streak = repository.checkAndUpdateStreak()
}
