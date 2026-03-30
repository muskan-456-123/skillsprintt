package com.example.skilltracker.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skilltracker.domain.usecase.GetLatestCourseUseCase
import com.example.skilltracker.domain.usecase.GetLatestSkillUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getLatestSkillUseCase: GetLatestSkillUseCase,
    private val getLatestCourseUseCase: GetLatestCourseUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val skill = getLatestSkillUseCase()
            val course = getLatestCourseUseCase()

            if (skill != null && course != null) {
                val dailySessions = if (course.dailyMinutes > 0) {
                    (course.dailyMinutes + 59) / 60 // Ceiling division
                } else 1

                NotificationHelper.showReminderNotification(
                    context = context,
                    sessionsCount = dailySessions,
                    skillName = skill.name
                )
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
