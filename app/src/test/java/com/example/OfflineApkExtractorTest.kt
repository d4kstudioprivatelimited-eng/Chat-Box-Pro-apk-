package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.offline.EXTRACTED_APK_NAME
import com.example.data.offline.OfflineApkExtractor
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OfflineApkExtractorTest {

    @Test
    fun `test extracted apk naming matches ChatBoxPro apk`() {
        assertEquals("ChatBoxPro.apk", EXTRACTED_APK_NAME)
    }

    @Test
    fun `test offline apk extraction completes successfully`() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        var progressRecorded = 0f

        val result = OfflineApkExtractor.extractApkToDownloads(app) { progress ->
            progressRecorded = progress
        }

        assertTrue(result.success)
        assertNotNull(result.savedFile)
        assertEquals("ChatBoxPro.apk", result.savedFile?.name)
        assertTrue(progressRecorded > 0f)
    }

    @Test
    fun `test unknown sources permission check does not throw`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        // In Robolectric environment, this should evaluate gracefully without throwing
        val canInstall = OfflineApkExtractor.canInstallUnknownApps(app)
        assertNotNull(canInstall)
    }
}
