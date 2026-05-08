package com.emotionleave.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emotionleave.app.data.local.entity.DailyPledgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPledgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(pledge: DailyPledgeEntity)

    @Query("SELECT * FROM daily_pledges WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<DailyPledgeEntity?>

    @Query("SELECT * FROM daily_pledges ORDER BY date DESC")
    fun observeAll(): Flow<List<DailyPledgeEntity>>

    @Query("SELECT * FROM daily_pledges ORDER BY date ASC")
    suspend fun getAllForExport(): List<DailyPledgeEntity>

    @Query("DELETE FROM daily_pledges")
    suspend fun deleteAll()
}
