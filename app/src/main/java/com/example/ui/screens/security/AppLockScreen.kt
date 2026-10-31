package com.example.ui.screens.security

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.WhatsAppEmerald

private fun Context.findFragmentActivity(): FragmentActivity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is FragmentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun AppLockScreen(
    appLockManager: AppLockManager
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val shakeOffset = remember { Animatable(0f) }
    val context = LocalContext.current
    val fragmentActivity = remember(context) { context.findFragmentActivity() }

    LaunchedEffect(Unit) {
        if (fragmentActivity != null && appLockManager.isBiometricEnabled.value && appLockManager.isBiometricHardwareAvailable()) {
            appLockManager.triggerBiometricPrompt(
                activity = fragmentActivity,
                onSuccess = {
                    appLockManager.unlockWithBiometrics()
                },
                onError = { _ -> }
            )
        }
    }




    LaunchedEffect(isError) {
        if (isError) {
            shakeOffset.animateTo(
                targetValue = 20f,
                animationSpec = repeatable(
                    iterations = 4,
                    animation = tween(50, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            shakeOffset.snapTo(0f)
            enteredPin = ""
            isError = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("app_lock_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(WhatsAppEmerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "App Locked",
                    tint = WhatsAppEmerald,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Chat Box Pro Locked",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "Enter your 4-digit security PIN or scan fingerprint",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 4 Pin Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.offset(x = shakeOffset.value.dp)
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) WhatsAppEmerald else Color.Transparent)
                            .border(2.dp, if (isFilled) WhatsAppEmerald else Color.White.copy(alpha = 0.4f), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Numeric Keypad
            val keypad = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("BIO", "0", "DEL")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                keypad.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { key ->
                            when (key) {
                                "BIO" -> {
                                    IconButton(
                                        onClick = {
                                            if (fragmentActivity != null && appLockManager.isBiometricHardwareAvailable()) {
                                                appLockManager.triggerBiometricPrompt(
                                                    activity = fragmentActivity,
                                                    onSuccess = {
                                                        appLockManager.unlockWithBiometrics()
                                                    },
                                                    onError = { err ->
                                                        android.widget.Toast.makeText(context, err, android.widget.Toast.LENGTH_SHORT).show()
                                                    }
                                                )
                                            } else {
                                                android.widget.Toast.makeText(context, "Biometric sensor unavailable. Enter your 4-digit PIN.", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        },

                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .testTag("app_lock_biometric_btn")
                                    ) {
                                        Icon(
                                            Icons.Default.Fingerprint,
                                            contentDescription = "Biometric Unlock",
                                            tint = WhatsAppEmerald,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                                "DEL" -> {
                                    IconButton(
                                        onClick = {
                                            if (enteredPin.isNotEmpty()) {
                                                enteredPin = enteredPin.dropLast(1)
                                            }
                                        },
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .testTag("app_lock_delete_btn")
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.Backspace,
                                            contentDescription = "Delete",
                                            tint = Color.White.copy(alpha = 0.7f),
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.08f))
                                            .clickable {
                                                if (enteredPin.length < 4) {
                                                    val newPin = enteredPin + key
                                                    enteredPin = newPin
                                                    if (newPin.length == 4) {
                                                        if (!appLockManager.verifyPin(newPin)) {
                                                            isError = true
                                                        }
                                                    }
                                                }
                                            }
                                            .testTag("keypad_$key"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = key,
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
