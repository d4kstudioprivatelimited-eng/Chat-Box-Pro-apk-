package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreenDark
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.EmailAuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailAuthScreen(
    emailAuthViewModel: EmailAuthViewModel = viewModel(),
    authViewModel: AuthViewModel? = null,
    onAuthSuccess: () -> Unit
) {
    val state by emailAuthViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chat Box Pro Verification",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    if (state.isOtpSent) {
                        IconButton(onClick = { emailAuthViewModel.resetToEmailInput() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Change email")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Official Chat Box Pro Logo Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F141C)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chatbox_logo),
                    contentDescription = "Chat Box Pro Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (!state.isOtpSent) "Enter your Email Address" else "Check your Email",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (!state.isOtpSent)
                    "Chat Box Pro will send a secure 6-digit verification passcode to your email using the Secure Auth Service."
                else
                    "We've sent an official verification passcode to:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            if (state.isOtpSent) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.email,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(Edit)",
                        color = WhatsAppEmerald,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { emailAuthViewModel.resetToEmailInput() }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!state.isOtpSent) {
                // Single Email Address Input Field (Replaces Phone & Country Selector)
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { emailAuthViewModel.updateEmail(it) },
                    placeholder = { Text("e.g. alex@example.com") },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email", tint = WhatsAppEmerald)
                    },
                    trailingIcon = {
                        if (state.email.isNotEmpty()) {
                            IconButton(onClick = { emailAuthViewModel.updateEmail("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            emailAuthViewModel.sendVerificationEmail()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input_field"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                state.errorMessage?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Official Verified Security Badge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = WhatsAppEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Official Verified Security",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Secure Auth Service • D 4K Studio Private Limited",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        emailAuthViewModel.sendVerificationEmail()
                    },
                    enabled = !state.isLoading && state.email.contains("@"),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_email_otp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenDark),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Send Verification Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else {
                // OTP Verification Screen
                AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // Info message banner from Brevo
                        state.infoMessage?.let { msg ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFEFF6FF)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.MarkEmailRead,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = msg,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E40AF),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                        }

                        // Passcode Card matching the design in screenshot
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "ONE-TIME SECURITY PASSCODE (ENTER 6 DIGITS)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // 6 Individual Passcode Boxes with Gold text style
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 0 until 6) {
                                        val digit = state.otpCode.getOrNull(i)?.toString() ?: ""
                                        val isCurrent = state.otpCode.length == i
                                        Box(
                                            modifier = Modifier
                                                .size(width = 44.dp, height = 54.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF0B1426))
                                                .border(
                                                    width = if (isCurrent) 2.dp else 1.dp,
                                                    color = if (isCurrent) Color(0xFFFBBF24) else Color(0xFF1E293B),
                                                    shape = RoundedCornerShape(10.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = digit.ifEmpty { "•" },
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = if (digit.isNotEmpty()) Color(0xFFFBBF24) else Color(0xFF475569)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "⏱ Valid for 10 Minutes • Single-Use Only",
                                    fontSize = 12.sp,
                                    color = Color(0xFF2563EB),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Hidden real input field synchronized with the 6 passcode boxes
                        OutlinedTextField(
                            value = state.otpCode,
                            onValueChange = { emailAuthViewModel.updateOtpCode(it.filter { ch -> ch.isDigit() }.take(6)) },
                            placeholder = { Text("Tap here to type 6-digit OTP") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_code_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        state.errorMessage?.let {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, textAlign = TextAlign.Center)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Resend OTP Countdown
                        Row(
                            modifier = Modifier
                                .clickable(enabled = state.resendCountdown == 0 && !state.isLoading) {
                                    emailAuthViewModel.resendVerificationEmail()
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Resend",
                                tint = if (state.resendCountdown == 0) WhatsAppEmerald else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (state.resendCountdown > 0)
                                    "Resend Code in ${state.resendCountdown}s"
                                else
                                    "Resend Code via Email",
                                color = if (state.resendCountdown == 0) WhatsAppEmerald else Color.Gray,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = {
                                emailAuthViewModel.verifyCode(state.otpCode) {
                                    authViewModel?.onEmailVerified(state.email)
                                    onAuthSuccess()
                                }
                            },
                            enabled = !state.isLoading && state.otpCode.length == 6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("verify_otp_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenDark),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Verify & Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
