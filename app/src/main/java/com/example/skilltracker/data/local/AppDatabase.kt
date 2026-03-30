package com.example.skilltracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.skilltracker.data.local.dao.*
import com.example.skilltracker.data.local.entity.*

@Database(
    entities = [
        SkillEntity::class,
        CourseEntity::class,
        SessionEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun skillDao(): SkillDao
    abstract fun courseDao(): CourseDao
    abstract fun sessionDao(): SessionDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        const val DATABASE_NAME = "skill_tracker_db"
    }
}
