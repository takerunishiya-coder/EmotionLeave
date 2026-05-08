package com.emotionleave.app.data.export

import java.io.File

object ExportCacheCleaner {
    fun clearFromCacheDir(cacheDir: File): Boolean =
        File(cacheDir, "exports").deleteRecursively()
}
