package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.AuthViewModel
import com.example.util.AppPermission
import com.example.util.PermissionManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OnboardingAndPermissionsTest {

    @Test
    fun testAllRequiredAppPermissionsConfigured() {
        val permissions = AppPermission.values()
        assertTrue(permissions.isNotEmpty())

        val camera = AppPermission.CAMERA
        assertEquals(android.Manifest.permission.CAMERA, camera.manifestPermission)
        assertFalse(camera.rationale.isBlank())

        val mic = AppPermission.MICROPHONE
        assertEquals(android.Manifest.permission.RECORD_AUDIO, mic.manifestPermission)

        val contacts = AppPermission.CONTACTS
        assertEquals(android.Manifest.permission.READ_CONTACTS, contacts.manifestPermission)
    }

    @Test
    fun testPermissionManagerDoesNotThrow() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        // In Robolectric, testing permission checking evaluates gracefully
        val hasCamera = PermissionManager.hasPermission(app, AppPermission.CAMERA)
        assertNotNull(hasCamera)
    }

    @Test
    fun testFirstTimeOnboardingAuthFlow() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        // Reset shared preferences to simulate a fresh install
        val prefs = app.getSharedPreferences("chatbox_auth_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        val authViewModel = AuthViewModel(app)

        // 1. Fresh app launch must start unauthenticated
        val initial = authViewModel.uiState.value
        assertFalse("New user should be unauthenticated initially", initial.isAuthenticated)
        assertFalse("New user should not have profile marked as done initially", initial.isProfileSetupDone)

        // 2. Email verified via Secure Auth Service
        authViewModel.onEmailVerified("alex@chatboxpro.com")
        val postAuth = authViewModel.uiState.value
        assertTrue("User should be authenticated after email OTP", postAuth.isAuthenticated)
        assertFalse("Profile setup should still be pending before user enters name and DP", postAuth.isProfileSetupDone)

        // 3. User finishes Profile Setup (Name, Bio, DP)
        authViewModel.saveProfile("Danish Raja", "Lead Developer @ D 4K Studio", "https://example.com/avatar.jpg")
        val postProfile = authViewModel.uiState.value
        assertTrue("User should remain authenticated", postProfile.isAuthenticated)
        assertTrue("Profile setup should be completed", postProfile.isProfileSetupDone)
        assertEquals("Danish Raja", postProfile.userName)

        // 4. Log out returns to registration screen
        authViewModel.logOut()
        val postLogout = authViewModel.uiState.value
        assertFalse("Logged out user should be unauthenticated", postLogout.isAuthenticated)
        assertFalse("Profile setup flag should be reset", postLogout.isProfileSetupDone)
    }
}
