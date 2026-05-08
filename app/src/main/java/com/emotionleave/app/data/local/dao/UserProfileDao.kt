package com.emotionleave.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.emotionleave.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Upsert
    suspend fun upsert(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles ORDER BY createdAt ASC LIMIT 1")
    fun observeCurrent(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles ORDER BY createdAt ASC")
    suspend fun getAllForExport(): List<UserProfileEntity>

    @Query("DELETE FROM user_profiles")
    suspend fun deleteAll()
}
