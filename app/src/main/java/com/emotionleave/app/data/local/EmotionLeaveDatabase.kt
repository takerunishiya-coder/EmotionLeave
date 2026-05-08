package com.emotionleave.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emotionleave.app.data.local.dao.DailyPledgeDao
import com.emotionleave.app.data.local.dao.DailyReviewDao
import com.emotionleave.app.data.local.dao.HabitDao
import com.emotionleave.app.data.local.dao.RelapseEventDao
import com.emotionleave.app.data.local.dao.SosSessionDao
import com.emotionleave.app.data.local.dao.UserProfileDao
import com.emotionleave.app.data.local.entity.DailyPledgeEntity
import com.emotionleave.app.data.local.entity.DailyReviewEntity
import com.emotionleave.app.data.local.entity.HabitEntity
import com.emotionleave.app.data.local.entity.RelapseEventEntity
import com.emotionleave.app.data.local.entity.SosSessionEntity
import com.emotionleave.app.data.local.entity.UserProfileEntity
import com.emotionleave.app.data.security.DatabaseKeyManager
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        UserProfileEntity::class,
        HabitEntity::class,
        DailyPledgeEntity::class,
        DailyReviewEntity::class,
        RelapseEventEntity::class,
        SosSessionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class EmotionLeaveDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun habitDao(): HabitDao
    abstract fun dailyPledgeDao(): DailyPledgeDao
    abstract fun dailyReviewDao(): DailyReviewDao
    abstract fun relapseEventDao(): RelapseEventDao
    abstract fun sosSessionDao(): SosSessionDao

    companion object {
        private const val DATABASE_NAME = "emotionleave.db"

        fun create(context: Context): EmotionLeaveDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                EmotionLeaveDatabase::class.java,
                DATABASE_NAME,
            )
                .openHelperFactory(
                    SupportFactory(
                        DatabaseKeyManager(context).getOrCreatePassphrase(),
                    ),
                )
                .build()
    }
}
