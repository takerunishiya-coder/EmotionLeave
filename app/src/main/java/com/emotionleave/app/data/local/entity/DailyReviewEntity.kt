package com.emotionleave.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "daily_reviews",
    indices = [Index(value = ["date"], unique = true)],
)
data class DailyReviewEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: String,
    val quickStatus: String = "",
    val urgeOccurred: Boolean = false,
    val urgeLevel: Int? = null,
    val triggerTags: List<String> = emptyList(),
    val copingActions: List<String> = emptyList(),
    val tomorrowAction: String = "",
    val note: String = "",
    val createdAt: Long,
    val updatedAt: Long,
)
