package com.example.skilltracker.domain.usecase

import com.example.skilltracker.domain.model.Session
import com.example.skilltracker.domain.repository.SessionRepository
import com.example.skilltracker.util.Constants
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GenerateLearningPlanUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(
        courseId: String,
        totalMinutes: Int,
        totalDays: Int
    ): List<Session> {
        val dailyMinutes = totalMinutes / totalDays
        val sessions = mutableListOf<Session>()
        var currentMinute = 0

        for (day in 1..totalDays) {
            var remainingForDay = dailyMinutes
            var sessionNumber = 1

            // Handle the last day - assign remaining minutes
            if (day == totalDays) {
                remainingForDay = totalMinutes - currentMinute
            }

            while (remainingForDay > 0) {
                val sessionDuration = minOf(
                    remainingForDay,
                    Constants.MAX_SESSION_DURATION_MINUTES
                )
                if (sessionDuration <= 0) break

                sessions.add(
                    Session(
                        id = UUID.randomUUID().toString(),
                        courseId = courseId,
                        dayNumber = day,
                        sessionNumber = sessionNumber,
                        startMinute = currentMinute,
                        endMinute = currentMinute + sessionDuration,
                        durationMinutes = sessionDuration,
                        difficulty = "normal"
                    )
                )
                currentMinute += sessionDuration
                remainingForDay -= sessionDuration
                sessionNumber++
            }
        }

        repository.saveSessions(courseId, sessions)
        return sessions
    }
}

class GetSessionsByDayUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    operator fun invoke(courseId: String, dayNumber: Int): Flow<List<Session>> {
        return repository.getSessionsByDay(courseId, dayNumber)
    }
}

class GetAllSessionsUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    operator fun invoke(courseId: String): Flow<List<Session>> {
        return repository.getSessionsByCourse(courseId)
    }
}

class CompleteSessionUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(session: Session): Session {
        val updated = session.copy(
            isCompleted = true,
            completedAt = System.currentTimeMillis()
        )
        repository.updateSession(updated)
        return updated
    }
}

class AdjustDifficultyUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(
        session: Session,
        feedback: String,
        courseId: String
    ) {
        // Update feedback on current session
        val updatedSession = session.copy(feedback = feedback)
        repository.updateSession(updatedSession)

        // Adjust upcoming sessions
        val upcomingSessions = repository.getUpcomingSessions(courseId, session.dayNumber)
        val adjustedSessions = upcomingSessions.map { upcoming ->
            when (feedback) {
                "too_easy" -> {
                    val newDuration = (upcoming.durationMinutes * Constants.EASY_REDUCTION_FACTOR).toInt()
                    upcoming.copy(
                        durationMinutes = maxOf(newDuration, 15),
                        endMinute = upcoming.startMinute + maxOf(newDuration, 15),
                        difficulty = "easy"
                    )
                }
                "too_hard" -> {
                    val newDuration = (upcoming.durationMinutes * Constants.HARD_INCREASE_FACTOR).toInt()
                    upcoming.copy(
                        durationMinutes = newDuration,
                        endMinute = upcoming.startMinute + newDuration,
                        difficulty = "hard"
                    )
                }
                else -> upcoming
            }
        }
        adjustedSessions.forEach { repository.updateSession(it) }
    }
}

class GetProgressUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(courseId: String): Pair<Int, Int> {
        val completed = repository.getCompletedSessionCount(courseId)
        val total = repository.getTotalSessionCount(courseId)
        return completed to total
    }
}

class GetTotalMinutesStudiedUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(courseId: String): Int {
        return repository.getTotalMinutesStudied(courseId)
    }
}
