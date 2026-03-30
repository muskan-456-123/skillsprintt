package com.example.skilltracker.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun today(): String = LocalDate.now().format(dateFormatter)

    fun yesterday(): String = LocalDate.now().minusDays(1).format(dateFormatter)

    fun parseDate(dateStr: String): LocalDate = LocalDate.parse(dateStr, dateFormatter)

    fun isYesterday(dateStr: String): Boolean {
        return try {
            parseDate(dateStr) == LocalDate.now().minusDays(1)
        } catch (e: Exception) {
            false
        }
    }

    fun isToday(dateStr: String): Boolean {
        return try {
            parseDate(dateStr) == LocalDate.now()
        } catch (e: Exception) {
            false
        }
    }

    fun daysAgo(days: Int): String {
        return LocalDate.now().minusDays(days.toLong()).format(dateFormatter)
    }

    /**
     * Parse ISO 8601 duration (PT#H#M#S) into total minutes.
     * Examples: PT1H30M -> 90, PT45M -> 45, PT2H -> 120
     */
    fun parseIsoDuration(isoDuration: String): Int {
        val pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")
        val matcher = pattern.matcher(isoDuration)
        if (!matcher.matches()) return 0

        val hours = matcher.group(1)?.toIntOrNull() ?: 0
        val minutes = matcher.group(2)?.toIntOrNull() ?: 0
        val seconds = matcher.group(3)?.toIntOrNull() ?: 0

        return hours * 60 + minutes + if (seconds >= 30) 1 else 0
    }

    /**
     * Format minutes into human readable string like "2h 30m"
     */
    fun formatMinutes(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return when {
            hours > 0 && mins > 0 -> "${hours}h ${mins}m"
            hours > 0 -> "${hours}h"
            else -> "${mins}m"
        }
    }

    /**
     * Format minutes into timestamp like "01:30:00"
     */
    fun formatTimestamp(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return String.format("%02d:%02d:00", hours, mins)
    }
}
