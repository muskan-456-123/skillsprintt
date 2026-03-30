package com.example.skilltracker.util

object Constants {
    // Firestore Collections
    const val USERS_COLLECTION = "users"
    const val COURSES_COLLECTION = "courses"
    const val SESSIONS_COLLECTION = "sessions"
    const val WELLNESS_COLLECTION = "wellness"

    // Firestore Fields
    const val PROFILE_FIELD = "profile"
    const val STREAK_FIELD = "streak"
    const val PROGRESS_FIELD = "progress"
    const val INFO_FIELD = "info"

    // YouTube API
    const val YOUTUBE_BASE_URL = "https://www.googleapis.com/youtube/v3/"
    const val YOUTUBE_SEARCH_PART = "snippet"
    const val YOUTUBE_VIDEO_PART = "contentDetails,snippet"
    const val YOUTUBE_MAX_RESULTS = 10
    const val YOUTUBE_VIDEO_TYPE = "video"
    const val YOUTUBE_ORDER = "relevance"
    const val YOUTUBE_DURATION = "long"

    // Session Defaults
    const val MAX_SESSION_DURATION_MINUTES = 60
    const val DEFAULT_PLAN_DAYS = 14
    const val MIN_PLAN_DAYS = 1
    const val MAX_PLAN_DAYS = 60

    // Difficulty Adjustment
    const val EASY_REDUCTION_FACTOR = 0.9
    const val HARD_INCREASE_FACTOR = 1.15

    // Quiz
    const val QUIZ_QUESTION_COUNT = 5

    // Notification
    const val REMINDER_WORK_NAME = "study_reminder"
    const val DEFAULT_REMINDER_HOUR = 9
    const val DEFAULT_REMINDER_MINUTE = 0

    // DataStore Keys
    const val PREFERENCES_NAME = "skill_tracker_prefs"

    // Wellness
    const val WELLNESS_BREAK_INTERVAL = 2 // sessions between breaks
}
