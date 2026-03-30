package com.example.skilltracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val title: String,
    val channelName: String,
    val thumbnailUrl: String,
    val durationMinutes: Int,
    val durationFormatted: String,
    val skill: String,
    val startDate: Long,
    val totalDays: Int,
    val dailyMinutes: Int
)
