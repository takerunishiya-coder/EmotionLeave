package com.emotionleave.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sos_sessions",
    indices = [Index(value = ["startedAt"])],
)
data class SosSessionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val startedAt: Long,
    val endedAt: Long? = null,
    val completedStep: String = "",
    val selectedActions: List<String> = emptyList(),
    val memo: String = "",
    val outcome: String = "",
    val linkedReviewDate: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)
