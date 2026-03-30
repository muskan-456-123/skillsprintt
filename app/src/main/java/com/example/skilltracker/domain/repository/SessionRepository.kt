package com.example.skilltracker.domain.repository

import com.example.skilltracker.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getSessionsByCourse(courseId: String): Flow<List<Session>>
    fun getSessionsByDay(courseId: String, dayNumber: Int): Flow<List<Session>>
    suspend fun getSessionById(sessionId: String): Session?
    suspend fun getNextPendingSession(courseId: String): Session?
    suspend fun getCompletedSessionCount(courseId: String): Int
    suspend fun getTotalSessionCount(courseId: String): Int
    suspend fun getTotalMinutesStudied(courseId: String): Int
    suspend fun saveSessions(courseId: String, sessions: List<Session>)
    suspend fun updateSession(session: Session)
    suspend fun getUpcomingSessions(courseId: String, dayNumber: Int): List<Session>
}
