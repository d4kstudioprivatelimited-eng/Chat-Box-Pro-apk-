package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.brevo.BrevoEmailService
import com.example.data.brevo.BrevoEmailTemplate
import com.example.ui.viewmodel.EmailAuthViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BrevoEmailAuthTest {

    @Test
    fun testBrevoEmailTemplateReplication() {
        val email = "alex@example.com"
        val otp = "963208"

        val html = BrevoEmailTemplate.generateVerificationEmailHtml(
            recipientEmail = email,
            otpCode = otp
        )

        assertNotNull(html)
        assertTrue("Must contain company name", html.contains("D 4K Studio Private Limited"))
        assertTrue("Must contain official verified security", html.contains("OFFICIAL VERIFIED SECURITY"))
        assertTrue("Must contain lead developer name", html.contains("Danish Raja Ji"))
        assertTrue("Must contain headquarters", html.contains("Naya Basti, Bokaro Thermal, Jharkhand, India"))
        assertTrue("Must contain official website", html.contains("https://mr4k09.000.pe"))
        assertTrue("Must contain phone number", html.contains("+91 9430320021"))
        assertTrue("Must contain ONE-TIME SECURITY PASSCODE header", html.contains("ONE-TIME SECURITY PASSCODE"))
        assertTrue("Must contain gold color style", html.contains("#fbbf24"))
        assertTrue("Must contain 10 minutes notice", html.contains("Valid for 10 Minutes"))
        assertTrue("Must contain TiDB Cloud Encrypted from screenshot", html.contains("TiDB Cloud Encrypted"))
        assertTrue("Must contain Developer Console Registration from screenshot", html.contains("Developer Console Registration"))
    }

    @Test
    fun testBrevoEmailServiceSendAndVerify() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val testEmail = "developer@chatboxpro.com"

        val result = BrevoEmailService.sendOtpEmail(app, testEmail)
        assertTrue("Send should succeed or provide active OTP", result.success)
        assertNotNull(result.activeOtp)

        val activeOtp = result.activeOtp!!
        assertEquals(6, activeOtp.length)

        // Invalid code should fail
        val (failResult, failMsg) = BrevoEmailService.verifyOtp(testEmail, "000000")
        assertFalse(failResult)

        // Mismatched email should fail
        val (emailMismatch, _) = BrevoEmailService.verifyOtp("other@example.com", activeOtp)
        assertFalse(emailMismatch)

        // Valid OTP should succeed
        val (successResult, successMsg) = BrevoEmailService.verifyOtp(testEmail, activeOtp)
        assertTrue(successResult)
        assertTrue(successMsg.contains("successfully verified"))
    }

    @Test
    fun testEmailAuthViewModel() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = EmailAuthViewModel(app)

        viewModel.updateEmail("testuser@chatboxpro.com")
        assertEquals("testuser@chatboxpro.com", viewModel.uiState.value.email)

        viewModel.updateOtpCode("123456")
        assertEquals("123456", viewModel.uiState.value.otpCode)

        viewModel.resetToEmailInput()
        assertEquals("", viewModel.uiState.value.otpCode)
        assertFalse(viewModel.uiState.value.isOtpSent)
    }

    @Test
    fun testWelcomeEmailTemplate() {
        val email = "alex@example.com"
        val name = "Alex Developer"

        val html = com.example.data.brevo.WelcomeEmailTemplate.generateWelcomeEmailHtml(
            recipientEmail = email,
            userName = name
        )

        assertNotNull(html)
        assertTrue("Must contain recipient name", html.contains("Alex Developer"))
        assertTrue("Must contain official verified account", html.contains("OFFICIAL VERIFIED ACCOUNT"))
        assertTrue("Must contain Chat Box Pro", html.contains("Chat Box Pro"))
        assertTrue("Must contain D 4K Studio Private Limited", html.contains("D 4K Studio Private Limited"))
        assertTrue("Must contain E2EE", html.contains("End-to-End Encryption"))
        assertTrue("Must contain 11-Language Switcher", html.contains("11-Language Switcher"))
    }

    @Test
    fun testLocalizationManager() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        com.example.ui.localization.LocalizationManager.init(app)

        assertEquals(com.example.ui.localization.AppLanguage.ENGLISH, com.example.ui.localization.LocalizationManager.currentLanguage.value)
        assertEquals("Chats", com.example.ui.localization.LocalizationManager.currentStrings.value.chats)

        // Switch to Hindi
        com.example.ui.localization.LocalizationManager.setLanguage(app, com.example.ui.localization.AppLanguage.HINDI)
        assertEquals(com.example.ui.localization.AppLanguage.HINDI, com.example.ui.localization.LocalizationManager.currentLanguage.value)
        assertEquals("चैट", com.example.ui.localization.LocalizationManager.currentStrings.value.chats)

        // Switch to Spanish
        com.example.ui.localization.LocalizationManager.setLanguage(app, com.example.ui.localization.AppLanguage.SPANISH)
        assertEquals("Chats", com.example.ui.localization.LocalizationManager.currentStrings.value.chats)
        assertEquals("Cerrar sesión", com.example.ui.localization.LocalizationManager.currentStrings.value.logout)

        // Switch to Arabic
        com.example.ui.localization.LocalizationManager.setLanguage(app, com.example.ui.localization.AppLanguage.ARABIC)
        assertEquals("الدردشات", com.example.ui.localization.LocalizationManager.currentStrings.value.chats)
    }

    @Test
    fun testSessionPersistenceAndLogout() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val authVm = com.example.ui.viewmodel.AuthViewModel(app)

        authVm.onEmailVerified("user@chatboxpro.com")
        assertTrue(authVm.uiState.value.isAuthenticated)
        assertEquals("user@chatboxpro.com", authVm.uiState.value.email)

        authVm.saveProfile("Test User", "Living life", "https://avatar.url")
        assertTrue(authVm.uiState.value.isProfileSetupDone)
        assertEquals("Test User", authVm.uiState.value.userName)

        // Verify that a newly instantiated ViewModel immediately reads the persistent session
        val coldStartVm = com.example.ui.viewmodel.AuthViewModel(app)
        assertTrue("Cold launch must be authenticated", coldStartVm.uiState.value.isAuthenticated)
        assertTrue("Cold launch must have profile done", coldStartVm.uiState.value.isProfileSetupDone)
        assertEquals("Test User", coldStartVm.uiState.value.userName)

        // Logout
        coldStartVm.logOut()
        assertFalse(coldStartVm.uiState.value.isAuthenticated)
        assertFalse(coldStartVm.uiState.value.isProfileSetupDone)

        // Verify session is wiped permanently
        val postLogoutVm = com.example.ui.viewmodel.AuthViewModel(app)
        assertFalse("Post-logout cold launch must require login", postLogoutVm.uiState.value.isAuthenticated)
    }
}
