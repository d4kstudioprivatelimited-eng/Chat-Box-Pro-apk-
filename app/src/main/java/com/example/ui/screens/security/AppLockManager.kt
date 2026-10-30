package com.example.ui.screens.security

import android.app.Application
import android.content.Context
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

    fun unlockWithBiometrics() {
        _isLocked.value = false
    }

    fun lockNow() {
        if (_isLockEnabled.value) {
            _isLocked.value = true
        }
    }
}
