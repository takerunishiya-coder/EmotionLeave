package com.emotionleave.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val SETTINGS_DATASTORE_NAME = "emotionleave_settings"

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_DATASTORE_NAME,
)

class SettingsDataSource(context: Context) {
    private val dataStore = context.applicationContext.settingsDataStore

    val settings: Flow<AppSettings> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences ->
            AppSettings(
                onboardingComplete = preferences[Keys.OnboardingComplete] ?: false,
                privacyLockEnabled = preferences[Keys.PrivacyLockEnabled] ?: false,
                secureScreenEnabled = preferences[Keys.SecureScreenEnabled] ?: true,
                notificationsEnabled = preferences[Keys.NotificationsEnabled] ?: false,
                morningReminderTime = preferences[Keys.MorningReminderTime].orEmpty(),
                eveningReminderTime = preferences[Keys.EveningReminderTime].orEmpty(),
                neutralNotificationPreviewEnabled =
                    preferences[Keys.NeutralNotificationPreviewEnabled] ?: true,
                aiAnalysisEnabled = preferences[Keys.AiAnalysisEnabled] ?: false,
                includeNotesInAiAnalysis = preferences[Keys.IncludeNotesInAiAnalysis] ?: false,
                includeRelapseRecordsInAiAnalysis =
                    preferences[Keys.IncludeRelapseRecordsInAiAnalysis] ?: false,
                preferredExportFormat = preferences[Keys.PreferredExportFormat] ?: "json",
            )
        }

    suspend fun updateOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[Keys.OnboardingComplete] = complete }
    }

    suspend fun updatePrivacyLockEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.PrivacyLockEnabled] = enabled }
    }

    suspend fun updateSecureScreenEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SecureScreenEnabled] = enabled }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.NotificationsEnabled] = enabled }
    }

    suspend fun updateMorningReminderTime(time: String) {
        dataStore.edit { it[Keys.MorningReminderTime] = time }
    }

    suspend fun updateEveningReminderTime(time: String) {
        dataStore.edit { it[Keys.EveningReminderTime] = time }
    }

    suspend fun updateNeutralNotificationPreviewEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.NeutralNotificationPreviewEnabled] = enabled }
    }

    suspend fun updateAiAnalysisEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.AiAnalysisEnabled] = enabled }
    }

    suspend fun updateIncludeNotesInAiAnalysis(enabled: Boolean) {
        dataStore.edit { it[Keys.IncludeNotesInAiAnalysis] = enabled }
    }

    suspend fun updateIncludeRelapseRecordsInAiAnalysis(enabled: Boolean) {
        dataStore.edit { it[Keys.IncludeRelapseRecordsInAiAnalysis] = enabled }
    }

    suspend fun updatePreferredExportFormat(format: String) {
        dataStore.edit { it[Keys.PreferredExportFormat] = format }
    }

    suspend fun reset() {
        dataStore.edit { it.clear() }
    }

    private object Keys {
        val OnboardingComplete = booleanPreferencesKey("onboarding_complete")
        val PrivacyLockEnabled = booleanPreferencesKey("privacy_lock_enabled")
        val SecureScreenEnabled = booleanPreferencesKey("secure_screen_enabled")
        val NotificationsEnabled = booleanPreferencesKey("notifications_enabled")
        val MorningReminderTime = stringPreferencesKey("morning_reminder_time")
        val EveningReminderTime = stringPreferencesKey("evening_reminder_time")
        val NeutralNotificationPreviewEnabled =
            booleanPreferencesKey("neutral_notification_preview_enabled")
        val AiAnalysisEnabled = booleanPreferencesKey("ai_analysis_enabled")
        val IncludeNotesInAiAnalysis = booleanPreferencesKey("include_notes_in_ai_analysis")
        val IncludeRelapseRecordsInAiAnalysis =
            booleanPreferencesKey("include_relapse_records_in_ai_analysis")
        val PreferredExportFormat = stringPreferencesKey("preferred_export_format")
    }
}
