package com.example.skilltracker.data.local.dao

import androidx.room.*
import com.example.skilltracker.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE courseId = :courseId ORDER BY dayNumber, sessionNumber")
    fun getSessionsByCourse(courseId: String): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE courseId = :courseId AND dayNumber = :dayNumber ORDER BY sessionNumber")
    fun getSessionsByDay(courseId: String, dayNumber: Int): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: String): SessionEntity?

    @Query("SELECT * FROM sessions WHERE courseId = :courseId AND isCompleted = 0 ORDER BY dayNumber, sessionNumber LIMIT 1")
    suspend fun getNextPendingSession(courseId: String): SessionEntity?

    @Query("SELECT COUNT(*) FROM sessions WHERE courseId = :courseId AND isCompleted = 1")
    suspend fun getCompletedSessionCount(courseId: String): Int

    @Query("SELECT COUNT(*) FROM sessions WHERE courseId = :courseId")
    suspend fun getTotalSessionCount(courseId: String): Int

    @Query("SELECT SUM(durationMinutes) FROM sessions WHERE courseId = :courseId AND isCompleted = 1")
    suspend fun getTotalMinutesStudied(courseId: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<SessionEntity>)

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Query("DELETE FROM sessions WHERE courseId = :courseId")
    suspend fun deleteSessionsByCourse(courseId: String)

    @Query("SELECT * FROM sessions WHERE courseId = :courseId AND dayNumber > :dayNumber ORDER BY dayNumber, sessionNumber")
    suspend fun getUpcomingSessions(courseId: String, dayNumber: Int): List<SessionEntity>
}
