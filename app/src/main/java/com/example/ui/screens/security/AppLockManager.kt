package com.example.ui.screens.security

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppLockManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("chatbox_app_lock", Context.MODE_PRIVATE)

    private val _isLocked = MutableStateFlow(
        prefs.getBoolean("is_lock_enabled", false)
    )
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _isLockEnabled = MutableStateFlow(
        prefs.getBoolean("is_lock_enabled", false)
    )
    val isLockEnabled: StateFlow<Boolean> = _isLockEnabled.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(
        prefs.getBoolean("is_biometric_enabled", true)
    )
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    fun isBiometricHardwareAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        val canAuth = biometricManager.canAuthenticate(authenticators)
        return canAuth == BiometricManager.BIOMETRIC_SUCCESS || canAuth == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
    }

    fun getStoredPin(): String = prefs.getString("stored_pin", "1234") ?: "1234"

    fun setLockEnabled(enabled: Boolean, pin: String = "1234") {
        prefs.edit()
            .putBoolean("is_lock_enabled", enabled)
            .putString("stored_pin", pin)
            .apply()
        _isLockEnabled.value = enabled
        if (!enabled) {
            _isLocked.value = false
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_biometric_enabled", enabled).apply()
        _isBiometricEnabled.value = enabled
    }

    fun verifyPin(pin: String): Boolean {
        return if (pin == getStoredPin()) {
            _isLocked.value = false
            true
        } else {
            false
        }
    }

    fun triggerBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Chat Box Pro Security")
            .setSubtitle("Authenticate using fingerprint or face unlock")
            .setNegativeButtonText("Use PIN")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    _isLocked.value = false
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.w("AppLockManager", "Biometric authentication error $errorCode: $errString")
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Fingerprint/Face not recognized. Please try again.")
                }
            })

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Log.e("AppLockManager", "Biometric prompt exception: ${e.message}")
            onError(e.localizedMessage ?: "Biometric hardware error")
        }
    }

    fun unlockWithBiometrics() {
        _isLocked.value = false
    }

    fun lockNow() {
        if (_isLockEnabled.value) {
            _isLocked.value = true
        }
    }
}
