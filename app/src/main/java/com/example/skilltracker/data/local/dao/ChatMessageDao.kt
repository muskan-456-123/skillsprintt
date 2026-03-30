package com.example.skilltracker.data.local.dao

import androidx.room.*
import com.example.skilltracker.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE courseId = :courseId ORDER BY timestamp ASC")
    fun getMessagesByCourse(courseId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE courseId = :courseId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(courseId: String, limit: Int = 50): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE courseId = :courseId")
    suspend fun clearMessages(courseId: String)
}
