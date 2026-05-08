package com.emotionleave.app.data.settings

data class AppSettings(
    val onboardingComplete: Boolean = false,
    val privacyLockEnabled: Boolean = false,
    val secureScreenEnabled: Boolean = true,
    val notificationsEnabled: Boolean = false,
    val morningReminderTime: String = "",
    val eveningReminderTime: String = "",
    val neutralNotificationPreviewEnabled: Boolean = true,
    val aiAnalysisEnabled: Boolean = false,
    val includeNotesInAiAnalysis: Boolean = false,
    val includeRelapseRecordsInAiAnalysis: Boolean = false,
    val preferredExportFormat: String = "json",
)
