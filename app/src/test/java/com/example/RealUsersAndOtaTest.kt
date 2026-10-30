package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ChatBoxDatabase
import com.example.data.ota.OtaUpdateManager
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RealUsersAndOtaTest {

    private lateinit var app: Application
    private lateinit var database: ChatBoxDatabase
    private lateinit var repository: ChatRepository

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        database = ChatBoxDatabase.getDatabase(app)
        repository = ChatRepository(database)
    }

    @Test
    fun `test purge all mock data ensures zero dummy contacts`() = runBlocking {
        repository.enforceRealUsersOnly()

        val contactCount = database.contactDao().getContactCount()
        assertEquals(0, contactCount)
    }

    @Test
    fun `test ota update manager release info`() = runBlocking {
        val otaManager = OtaUpdateManager.getInstance()
        val release = otaManager.checkLatestRelease()

        assertNotNull(release)
        assertEquals("1.2.5", release.versionName)
        assertTrue(release.apkDownloadUrl.endsWith(".apk"))
        assertTrue(release.releaseNotes.contains("Real Users"))
    }
}
