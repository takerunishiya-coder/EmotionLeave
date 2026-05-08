package com.emotionleave.app.data.repository

import com.emotionleave.app.data.local.EmotionLeaveDatabase
import com.emotionleave.app.data.local.entity.DailyPledgeEntity
import com.emotionleave.app.data.local.entity.DailyReviewEntity
import com.emotionleave.app.data.local.entity.HabitEntity
import com.emotionleave.app.data.local.entity.RelapseEventEntity
import com.emotionleave.app.data.local.entity.SosSessionEntity
import com.emotionleave.app.data.local.entity.UserProfileEntity
import com.emotionleave.app.data.settings.AppSettings
import com.emotionleave.app.data.settings.SettingsDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class LocalDataRepository(
    private val database: EmotionLeaveDatabase,
    private val settingsDataSource: SettingsDataSource,
) {
    fun observeUserProfile() = database.userProfileDao().observeCurrent()
    fun observeHabits() = database.habitDao().observeAll()
    fun observeDailyPledges() = database.dailyPledgeDao().observeAll()
    fun observeDailyReviews() = database.dailyReviewDao().observeAll()
    fun observeRelapseEvents() = database.relapseEventDao().observeAll()
    fun observeSosSessions() = database.sosSessionDao().observeAll()
    fun observeSettings(): Flow<AppSettings> = settingsDataSource.settings

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        database.userProfileDao().upsert(profile)
    }

    suspend fun saveHabit(habit: HabitEntity) {
        database.habitDao().upsert(habit)
    }

    suspend fun saveDailyPledge(pledge: DailyPledgeEntity) {
        database.dailyPledgeDao().save(pledge)
    }

    suspend fun saveDailyReview(review: DailyReviewEntity) {
        database.dailyReviewDao().save(review)
    }

    suspend fun saveRelapseEvent(event: RelapseEventEntity) {
        database.relapseEventDao().upsert(event)
    }

    suspend fun saveSosSession(session: SosSessionEntity) {
        database.sosSessionDao().upsert(session)
    }

    suspend fun createExportSnapshot(now: Long): ExportSnapshot =
        ExportSnapshot(
            exportedAt = now,
            userProfiles = database.userProfileDao().getAllForExport(),
            habits = database.habitDao().getAllForExport(),
            dailyPledges = database.dailyPledgeDao().getAllForExport(),
            dailyReviews = database.dailyReviewDao().getAllForExport(),
            relapseEvents = database.relapseEventDao().getAllForExport(),
            sosSessions = database.sosSessionDao().getAllForExport(),
            settings = settingsDataSource.settings.first(),
        )

    suspend fun deleteAllLocalData() {
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
        settingsDataSource.reset()
    }

    suspend fun markOnboardingComplete() {
        settingsDataSource.updateOnboardingComplete(true)
    }

    suspend fun updateSecureScreenEnabled(enabled: Boolean) {
        settingsDataSource.updateSecureScreenEnabled(enabled)
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        settingsDataSource.updateNotificationsEnabled(enabled)
    }

    suspend fun updateMorningReminderTime(time: String) {
        settingsDataSource.updateMorningReminderTime(time)
    }

    suspend fun updateEveningReminderTime(time: String) {
        settingsDataSource.updateEveningReminderTime(time)
    }

    suspend fun updateNeutralNotificationPreviewEnabled(enabled: Boolean) {
        settingsDataSource.updateNeutralNotificationPreviewEnabled(enabled)
    }
}
