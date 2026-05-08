package com.emotionleave.app.core

import android.content.Context
import com.emotionleave.app.data.local.EmotionLeaveDatabase
import com.emotionleave.app.data.repository.LocalDataRepository
import com.emotionleave.app.data.settings.SettingsDataSource

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val database: EmotionLeaveDatabase by lazy {
        EmotionLeaveDatabase.create(appContext)
    }

    val settingsDataSource: SettingsDataSource by lazy {
        SettingsDataSource(appContext)
    }

    val localDataRepository: LocalDataRepository by lazy {
        LocalDataRepository(
            database = database,
            settingsDataSource = settingsDataSource,
        )
    }
}
