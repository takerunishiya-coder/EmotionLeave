package com.emotionleave.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "daily_pledges",
    indices = [Index(value = ["date"], unique = true)],
)
data class DailyPledgeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: String,
    val pledgeText: String = "",
    val avoidancePlan: String = "",
    val mood: String = "",
    val note: String = "",
    val createdAt: Long,
    val updatedAt: Long,
)
