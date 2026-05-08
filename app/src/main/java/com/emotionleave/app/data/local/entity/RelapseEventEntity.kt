package com.emotionleave.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "relapse_events",
    indices = [Index(value = ["occurredAt"])],
)
data class RelapseEventEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val occurredAt: Long,
    val feeling: String = "",
    val triggerTags: List<String> = emptyList(),
    val reflection: String = "",
    val nextAction: String = "",
    val rePledgedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
)
