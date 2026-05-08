package com.emotionleave.app.data.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.emotionleave.app.data.settings.AppSettings
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class NotificationScheduler(context: Context) {
    private val workManager = WorkManager.getInstance(context.applicationContext)

    fun scheduleDailyReminder() {
        scheduleDailyReminders(AppSettings(notificationsEnabled = true))
    }

    fun scheduleDailyReminders(settings: AppSettings) {
        if (!settings.notificationsEnabled) {
            cancelDailyReminder()
            return
        }
        enqueueReminder(
            workName = MORNING_WORK_NAME,
            type = DailyReminderWorker.TYPE_MORNING,
            time = settings.morningReminderTime.ifBlank { DEFAULT_MORNING_TIME },
        )
        enqueueReminder(
            workName = EVENING_WORK_NAME,
            type = DailyReminderWorker.TYPE_EVENING,
            time = settings.eveningReminderTime.ifBlank { DEFAULT_EVENING_TIME },
        )
    }

    private fun enqueueReminder(workName: String, type: String, time: String) {
        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        )
            .setInitialDelay(initialDelayMinutes(time), TimeUnit.MINUTES)
            .setInputData(
                Data.Builder()
                    .putString(DailyReminderWorker.KEY_TYPE, type)
                    .build(),
            )
            .addTag(workName)
            .build()

        workManager.enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    fun cancelDailyReminder() {
        workManager.cancelUniqueWork(MORNING_WORK_NAME)
        workManager.cancelUniqueWork(EVENING_WORK_NAME)
        workManager.cancelUniqueWork(LEGACY_WORK_NAME)
    }

    private fun initialDelayMinutes(time: String): Long {
        val targetTime = runCatching { LocalTime.parse(time) }.getOrDefault(LocalTime.parse(DEFAULT_MORNING_TIME))
        val now = LocalDateTime.now()
        var target = now.toLocalDate().atTime(targetTime)
        if (!target.isAfter(now)) {
            target = target.plusDays(1)
        }
        return Duration.between(now, target).toMinutes().coerceAtLeast(15)
    }

    private companion object {
        const val DEFAULT_MORNING_TIME = "08:00"
        const val DEFAULT_EVENING_TIME = "21:00"
        const val MORNING_WORK_NAME = "morning_pledge_reminder"
        const val EVENING_WORK_NAME = "evening_review_reminder"
        const val LEGACY_WORK_NAME = "daily_neutral_reminder"
    }
}
