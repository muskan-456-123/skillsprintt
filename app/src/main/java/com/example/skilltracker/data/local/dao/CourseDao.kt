package com.example.skilltracker.data.local.dao

import androidx.room.*
import com.example.skilltracker.data.local.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY startDate DESC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: String): CourseEntity?

    @Query("SELECT * FROM courses WHERE skill = :skill ORDER BY startDate DESC")
    fun getCoursesBySkill(skill: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestCourse(): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)
}
