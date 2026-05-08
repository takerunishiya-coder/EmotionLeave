package com.emotionleave.app

import android.app.Application
import com.emotionleave.app.core.AppContainer

class EmotionLeaveApplication : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(this)
    }
}
