package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.brevo.BrevoEmailService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val COUNTDOWN_SECONDS = 60

data class EmailAuthState(
    val email: String = "",
    val otpCode: String = "",
    val isOtpSent: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val resendCountdown: Int = 0,
    val isVerified: Boolean = false
)

class EmailAuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("chatbox_auth_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        EmailAuthState(
            email = prefs.getString("user_email", "") ?: "",
            isVerified = prefs.getBoolean("is_authenticated", false)
        )
    )
    val uiState: StateFlow<EmailAuthState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email.trim(), errorMessage = null)
    }

    fun updateOtpCode(code: String) {
        _uiState.value = _uiState.value.copy(otpCode = code.trim(), errorMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun sendVerificationEmail() {
        val email = _uiState.value.email.trim()
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter a valid email address."
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, infoMessage = null)

        viewModelScope.launch {
            val result = BrevoEmailService.sendOtpEmail(getApplication(), email)
            if (result.success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isOtpSent = true,
                    infoMessage = result.message,
                    errorMessage = null
                )
                startResendCountdown()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
            }
        }
    }

    fun resendVerificationEmail() {
        if (_uiState.value.resendCountdown > 0) return
        sendVerificationEmail()
    }

    fun verifyCode(enteredCode: String = _uiState.value.otpCode, onSuccess: () -> Unit = {}) {
        val trimmed = enteredCode.trim()
        if (trimmed.length != 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter the complete 6-digit passcode."
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val email = _uiState.value.email.trim()
            val (success, message) = BrevoEmailService.verifyOtp(email, trimmed)

            if (success) {
                prefs.edit()
                    .putBoolean("is_authenticated", true)
                    .putString("user_email", email)
                    .apply()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isVerified = true,
                    errorMessage = null
                )
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = message
                )
            }
        }
    }

    fun resetToEmailInput() {
        countdownJob?.cancel()
        BrevoEmailService.clearSession()
        _uiState.value = _uiState.value.copy(
            isOtpSent = false,
            otpCode = "",
            errorMessage = null,
            infoMessage = null,
            resendCountdown = 0
        )
    }

    private fun startResendCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (sec in COUNTDOWN_SECONDS downTo 0) {
                _uiState.value = _uiState.value.copy(resendCountdown = sec)
                if (sec > 0) delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
