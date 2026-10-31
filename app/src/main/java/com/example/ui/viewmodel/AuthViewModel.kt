package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.brevo.BrevoEmailService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "AuthViewModel"

data class AuthUiState(
    val email: String = "",
    val phoneNumber: String = "",
    val isAuthenticated: Boolean = false,
    val isProfileSetupDone: Boolean = false,
    val userName: String = "",
    val userBio: String = "Available ⚡ Using Chat Box Pro",
    val userAvatarUrl: String = "",
    val sessionToken: String = ""
)

/**
 * WhatsApp-Like Persistent Login Session Management & User Profile State.
 * 
 * Uses Android SharedPreferences to persist session permanently across cold restarts.
 * Once verified, user never sees login or OTP screens on app restart, opening
 * straight to the Main Chat List until explicit logout.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("chatbox_auth_prefs", Context.MODE_PRIVATE)
    private val firestoreRepository = com.example.data.firebase.firestore.FirestoreChatRepository(application)

    private val _uiState = MutableStateFlow(
        AuthUiState(
            email = prefs.getString("user_email", "") ?: "",
            phoneNumber = prefs.getString("user_phone", "") ?: "",
            userName = prefs.getString("user_name", "") ?: "",
            userBio = prefs.getString("user_bio", "Available ⚡ Using Chat Box Pro") ?: "Available ⚡ Using Chat Box Pro",
            userAvatarUrl = prefs.getString("user_avatar", "") ?: "",
            isAuthenticated = prefs.getBoolean("is_authenticated", false),
            isProfileSetupDone = prefs.getBoolean("is_profile_done", false),
            sessionToken = prefs.getString("session_token", "") ?: ""
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailVerified(email: String) {
        val trimmedEmail = email.trim()
        val token = prefs.getString("session_token", null) ?: UUID.randomUUID().toString()

        _uiState.value = _uiState.value.copy(
            email = trimmedEmail,
            phoneNumber = trimmedEmail,
            isAuthenticated = true,
            sessionToken = token
        )

        prefs.edit()
            .putString("user_email", trimmedEmail)
            .putString("user_phone", trimmedEmail)
            .putString("session_token", token)
            .putBoolean("is_authenticated", true)
            .apply()

        Log.d(TAG, "Persistent session established for: $trimmedEmail")
    }

    fun saveProfile(name: String, bio: String, avatarUrl: String, phone: String = "") {
        val trimmedName = name.trim().ifEmpty { "Alex Developer" }
        val userEmail = _uiState.value.email
        val userPhone = phone.trim().ifEmpty { _uiState.value.phoneNumber }

        // Step 5: Immediate local session update for responsive UI
        _uiState.value = _uiState.value.copy(
            userName = trimmedName,
            userBio = bio,
            userAvatarUrl = avatarUrl,
            phoneNumber = userPhone,
            isProfileSetupDone = true,
            isAuthenticated = true
        )

        prefs.edit()
            .putString("user_name", trimmedName)
            .putString("user_bio", bio)
            .putString("user_avatar", avatarUrl)
            .putString("user_phone", userPhone)
            .putBoolean("is_profile_done", true)
            .putBoolean("is_authenticated", true)
            .apply()

        Log.d(TAG, "Profile saved locally for $trimmedName ($userEmail, phone: $userPhone)")

        viewModelScope.launch {
            // Step 3: User uploads Profile DP -> App saves this DP securely using Supabase storage
            val supabaseAvatarUrl = try {
                if (avatarUrl.isNotBlank()) {
                    com.example.data.supabase.SupabaseStorageService.getInstance()
                        .uploadProfileAvatar(
                            context = getApplication(),
                            imageUriOrUrl = avatarUrl,
                            userEmailOrId = userEmail.ifBlank { userPhone }
                        )
                } else {
                    avatarUrl
                }
            } catch (e: Exception) {
                Log.w(TAG, "Supabase Profile DP upload notice: ${e.message}")
                avatarUrl
            }

            if (supabaseAvatarUrl != avatarUrl && supabaseAvatarUrl.isNotBlank()) {
                _uiState.value = _uiState.value.copy(userAvatarUrl = supabaseAvatarUrl)
                prefs.edit().putString("user_avatar", supabaseAvatarUrl).apply()
                Log.d(TAG, "Profile DP saved to Supabase: $supabaseAvatarUrl")
            }

            // Step 4: Firebase handles the core backend session within the app
            try {
                if (com.google.firebase.FirebaseApp.getApps(getApplication()).isNotEmpty()) {
                    firestoreRepository.syncUserProfile(
                        com.example.data.firebase.firestore.models.UserProfileDocument(
                            id = userEmail.ifBlank { userPhone.ifBlank { "me" } },
                            displayName = trimmedName,
                            phoneNumber = userPhone,
                            photoUrl = supabaseAvatarUrl.ifBlank { avatarUrl },
                            bio = bio,
                            isOnline = true
                        )
                    )
                    Log.d(TAG, "Firebase user profile document synchronized.")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase sync notice: ${e.message}")
            }

            // Step 4: Once the profile is saved, the app triggers the Node.js server to send the Welcome Email
            if (userEmail.contains("@")) {
                try {
                    val result = BrevoEmailService.sendWelcomeEmail(
                        context = getApplication(),
                        recipientEmail = userEmail,
                        userName = trimmedName,
                        avatarUrl = supabaseAvatarUrl.ifBlank { avatarUrl }
                    )
                    Log.d(TAG, "Welcome email result via Node.js Proxy: ${result.message}")
                } catch (e: Exception) {
                    Log.w(TAG, "Welcome email dispatch notice: ${e.message}")
                }
            }
        }
    }

    /**
     * Completely wipes the local session and signs out.
     */
    fun logOut() {
        _uiState.value = AuthUiState(
            email = "",
            phoneNumber = "",
            isAuthenticated = false,
            isProfileSetupDone = false,
            userName = "",
            userBio = "Available ⚡ Using Chat Box Pro",
            userAvatarUrl = "",
            sessionToken = ""
        )

        prefs.edit().clear().apply()
        BrevoEmailService.clearSession()
        Log.d(TAG, "Session wiped successfully. Logged out.")
    }
}
