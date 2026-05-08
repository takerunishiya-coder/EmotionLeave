package com.emotionleave.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.emotionleave.app.data.local.entity.SosSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SosSessionDao {
    @Upsert
    suspend fun upsert(session: SosSessionEntity)

    @Query("SELECT * FROM sos_sessions ORDER BY startedAt DESC")
    fun observeAll(): Flow<List<SosSessionEntity>>

    @Query("SELECT * FROM sos_sessions ORDER BY startedAt ASC")
    suspend fun getAllForExport(): List<SosSessionEntity>

    @Query("DELETE FROM sos_sessions")
    suspend fun deleteAll()
}
