package com.emotionleave.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startAt: Long,
    val relapseCount: Int = 0,
    val longestStreakDays: Int = 0,
    val createdAt: Long,
    val updatedAt: Long,
)
