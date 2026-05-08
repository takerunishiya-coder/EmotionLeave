package com.emotionleave.app.data.notification

import com.emotionleave.app.data.settings.AppSettings

object DailyReminderPolicy {
    fun shouldPost(settings: AppSettings, canPostNotifications: Boolean): Boolean =
        settings.notificationsEnabled && canPostNotifications
}
