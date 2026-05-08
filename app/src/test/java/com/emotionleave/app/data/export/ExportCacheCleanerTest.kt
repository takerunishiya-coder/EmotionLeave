package com.emotionleave.app.data.export

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ExportCacheCleanerTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun clearFromCacheDir_removesExportsDirectoryOnly() {
        val cacheDir = temporaryFolder.newFolder("cache")
        val exportDir = File(cacheDir, "exports").apply { mkdirs() }
        val exportFile = File(exportDir, "emotionleave-export.json").apply {
            writeText("""{"memo":"sensitive"}""")
        }
        val unrelatedFile = File(cacheDir, "other.tmp").apply {
            writeText("keep")
        }

        assertTrue(exportFile.exists())

        ExportCacheCleaner.clearFromCacheDir(cacheDir)

        assertFalse(exportDir.exists())
        assertTrue(unrelatedFile.exists())
    }
}
