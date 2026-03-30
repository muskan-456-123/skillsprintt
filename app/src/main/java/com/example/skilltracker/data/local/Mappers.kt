package com.example.skilltracker.data.local

import com.example.skilltracker.data.local.entity.*
import com.example.skilltracker.domain.model.*

// Skill mappers
fun SkillEntity.toDomain() = Skill(
    id = id, name = name, description = description,
    iconName = iconName, isCustom = isCustom
)

fun Skill.toEntity() = SkillEntity(
    id = id, name = name, description = description,
    iconName = iconName, isCustom = isCustom
)

// Course mappers
fun CourseEntity.toDomain() = Course(
    id = id, videoId = videoId, title = title,
    channelName = channelName, thumbnailUrl = thumbnailUrl,
    durationMinutes = durationMinutes, durationFormatted = durationFormatted,
    skill = skill, startDate = startDate, totalDays = totalDays,
    dailyMinutes = dailyMinutes
)

fun Course.toEntity() = CourseEntity(
    id = id, videoId = videoId, title = title,
    channelName = channelName, thumbnailUrl = thumbnailUrl,
    durationMinutes = durationMinutes, durationFormatted = durationFormatted,
    skill = skill, startDate = startDate, totalDays = totalDays,
    dailyMinutes = dailyMinutes
)

// Session mappers
fun SessionEntity.toDomain() = Session(
    id = id, courseId = courseId, dayNumber = dayNumber,
    sessionNumber = sessionNumber, startMinute = startMinute,
    endMinute = endMinute, durationMinutes = durationMinutes,
    isCompleted = isCompleted, isSkipped = isSkipped,
    difficulty = difficulty, feedback = feedback,
    quizScore = quizScore, completedAt = completedAt
)

fun Session.toEntity() = SessionEntity(
    id = id, courseId = courseId, dayNumber = dayNumber,
    sessionNumber = sessionNumber, startMinute = startMinute,
    endMinute = endMinute, durationMinutes = durationMinutes,
    isCompleted = isCompleted, isSkipped = isSkipped,
    difficulty = difficulty, feedback = feedback,
    quizScore = quizScore, completedAt = completedAt
)

// Chat message mappers
fun ChatMessageEntity.toDomain() = ChatMessage(
    id = id, content = content, isUser = isUser, timestamp = timestamp
)

fun ChatMessage.toEntity(courseId: String) = ChatMessageEntity(
    id = id, content = content, isUser = isUser,
    timestamp = timestamp, courseId = courseId
)
