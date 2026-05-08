package com.emotionleave.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emotionleave.app.data.local.entity.DailyReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(review: DailyReviewEntity)

    @Query("SELECT * FROM daily_reviews WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<DailyReviewEntity?>

    @Query("SELECT * FROM daily_reviews ORDER BY date DESC")
    fun observeAll(): Flow<List<DailyReviewEntity>>

    @Query("SELECT * FROM daily_reviews ORDER BY date ASC")
    suspend fun getAllForExport(): List<DailyReviewEntity>

    @Query("DELETE FROM daily_reviews")
    suspend fun deleteAll()
}
