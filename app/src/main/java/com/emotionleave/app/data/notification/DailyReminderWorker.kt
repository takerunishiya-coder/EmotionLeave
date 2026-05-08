package com.emotionleave.app.data.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.emotionleave.app.MainActivity
import com.emotionleave.app.R
import com.emotionleave.app.data.settings.SettingsDataSource
import kotlinx.coroutines.flow.first

class DailyReminderWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val settings = SettingsDataSource(context).settings.first()
        if (!DailyReminderPolicy.shouldPost(settings, canPostNotifications())) return Result.success()
        val type = inputData.getString(KEY_TYPE) ?: TYPE_MORNING
        val title = when {
            !settings.neutralNotificationPreviewEnabled -> "EmotionLeave"
            type == TYPE_EVENING -> "今日の振り返りを残しませんか"
            else -> "今日の誓いを残しませんか"
        }
        val text = when {
            !settings.neutralNotificationPreviewEnabled -> "今日の記録を残せます"
            type == TYPE_EVENING -> "次の自分のヒントを短く残せます"
            else -> "今日の行動を1つだけ決められます"
        }

        ensureChannel()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        NotificationManagerCompat.from(context).safeNotify(notification)
        return Result.success()
    }

    private fun canPostNotifications(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Daily check-in",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Neutral daily reminders."
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    private fun NotificationManagerCompat.safeNotify(notification: android.app.Notification) {
        notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "daily_check_in"
        const val KEY_TYPE = "reminder_type"
        const val TYPE_MORNING = "morning"
        const val TYPE_EVENING = "evening"
        const val NOTIFICATION_ID = 1001
    }
}
