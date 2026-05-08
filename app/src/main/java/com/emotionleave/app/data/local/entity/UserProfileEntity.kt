package com.emotionleave.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String = DEFAULT_USER_PROFILE_ID,
    val createdAt: Long,
    val startDate: String = "",
    val locale: String,
    val recoveryGoal: String = "",
    val displayName: String = "",
    val reasonValues: List<String> = emptyList(),
    val selectedAvatarId: String = "",
)

const val DEFAULT_USER_PROFILE_ID = "local_user"
