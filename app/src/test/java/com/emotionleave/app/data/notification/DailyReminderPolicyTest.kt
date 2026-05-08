package com.emotionleave.app.data.notification

import com.emotionleave.app.data.settings.AppSettings
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyReminderPolicyTest {
    @Test
    fun shouldPost_returnsTrueOnlyWhenEnabledAndPermissionAvailable() {
        assertTrue(
            DailyReminderPolicy.shouldPost(
                settings = AppSettings(notificationsEnabled = true),
                canPostNotifications = true,
            ),
        )
    }

    @Test
    fun shouldPost_returnsFalseWhenUserDisabledNotifications() {
        assertFalse(
            DailyReminderPolicy.shouldPost(
                settings = AppSettings(notificationsEnabled = false),
                canPostNotifications = true,
            ),
        )
    }

    @Test
    fun shouldPost_returnsFalseWhenPermissionIsUnavailable() {
        assertFalse(
            DailyReminderPolicy.shouldPost(
                settings = AppSettings(notificationsEnabled = true),
                canPostNotifications = false,
            ),
        )
    }
}
