package com.example.skilltracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val dayNumber: Int,
    val sessionNumber: Int,
    val startMinute: Int,
    val endMinute: Int,
    val durationMinutes: Int,
    val isCompleted: Boolean,
    val isSkipped: Boolean,
    val difficulty: String,
    val feedback: String,
    val quizScore: Int,
    val completedAt: Long
)
