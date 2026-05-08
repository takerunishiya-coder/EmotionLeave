package com.emotionleave.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.emotionleave.app.data.local.entity.RelapseEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelapseEventDao {
    @Upsert
    suspend fun upsert(event: RelapseEventEntity)

    @Query("SELECT * FROM relapse_events ORDER BY occurredAt DESC")
    fun observeAll(): Flow<List<RelapseEventEntity>>

    @Query("SELECT * FROM relapse_events ORDER BY occurredAt ASC")
    suspend fun getAllForExport(): List<RelapseEventEntity>

    @Query("DELETE FROM relapse_events")
    suspend fun deleteAll()
}
