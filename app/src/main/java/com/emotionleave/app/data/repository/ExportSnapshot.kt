package com.emotionleave.app.data.repository

import com.emotionleave.app.data.local.entity.DailyPledgeEntity
import com.emotionleave.app.data.local.entity.DailyReviewEntity
import com.emotionleave.app.data.local.entity.HabitEntity
import com.emotionleave.app.data.local.entity.RelapseEventEntity
import com.emotionleave.app.data.local.entity.SosSessionEntity
import com.emotionleave.app.data.local.entity.UserProfileEntity
import com.emotionleave.app.data.settings.AppSettings

data class ExportSnapshot(
    val exportedAt: Long,
    val userProfiles: List<UserProfileEntity>,
    val habits: List<HabitEntity>,
    val dailyPledges: List<DailyPledgeEntity>,
    val dailyReviews: List<DailyReviewEntity>,
    val relapseEvents: List<RelapseEventEntity>,
    val sosSessions: List<SosSessionEntity>,
    val settings: AppSettings,
)
