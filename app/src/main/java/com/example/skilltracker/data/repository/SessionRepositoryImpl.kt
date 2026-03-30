package com.example.skilltracker.data.repository

import com.example.skilltracker.data.local.dao.SessionDao
import com.example.skilltracker.data.local.toDomain
import com.example.skilltracker.data.local.toEntity
import com.example.skilltracker.data.remote.FirestoreService
import com.example.skilltracker.domain.model.Session
import com.example.skilltracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val firestoreService: FirestoreService
) : SessionRepository {

    override fun getSessionsByCourse(courseId: String): Flow<List<Session>> {
        return sessionDao.getSessionsByCourse(courseId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSessionsByDay(courseId: String, dayNumber: Int): Flow<List<Session>> {
        return sessionDao.getSessionsByDay(courseId, dayNumber).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSessionById(sessionId: String): Session? {
        return sessionDao.getSessionById(sessionId)?.toDomain()
    }

    override suspend fun getNextPendingSession(courseId: String): Session? {
        return sessionDao.getNextPendingSession(courseId)?.toDomain()
    }

    override suspend fun getCompletedSessionCount(courseId: String): Int {
        return sessionDao.getCompletedSessionCount(courseId)
    }

    override suspend fun getTotalSessionCount(courseId: String): Int {
        return sessionDao.getTotalSessionCount(courseId)
    }

    override suspend fun getTotalMinutesStudied(courseId: String): Int {
        return sessionDao.getTotalMinutesStudied(courseId) ?: 0
    }

    override suspend fun saveSessions(courseId: String, sessions: List<Session>) {
        sessionDao.insertSessions(sessions.map { it.toEntity() })
        try {
            firestoreService.saveSessions(courseId, sessions)
        } catch (_: Exception) { }
    }

    override suspend fun updateSession(session: Session) {
        sessionDao.updateSession(session.toEntity())
        try {
            firestoreService.updateSession(session.courseId, session)
        } catch (_: Exception) { }
    }

    override suspend fun getUpcomingSessions(courseId: String, dayNumber: Int): List<Session> {
        return sessionDao.getUpcomingSessions(courseId, dayNumber).map { it.toDomain() }
    }
}
