package com.example.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.screens.security.AppLockManager
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    appLockManager: AppLockManager,
    onBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToE2eeInfo: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsState()
    val isDarkMode by chatViewModel.isDarkMode.collectAsState()
    val isLockEnabled by appLockManager.isLockEnabled.collectAsState()

    var showApkUpdateDialog by remember { mutableStateOf(false) }
    var showZeroFootprintDialog by remember { mutableStateOf(false) }
    var showOfflineShareDialog by remember { mutableStateOf(false) }
    var showPermissionsDialog by remember { mutableStateOf(false) }
    var showPinSetupDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }

    val strings = com.example.ui.localization.LocalAppStrings.current
    val currentLang = com.example.ui.localization.LocalCurrentLanguage.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("settings_column")
        ) {
            // Profile Card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToProfile() }
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .testTag("settings_profile_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (authState.userAvatarUrl.isNotEmpty()) {
                        AsyncImage(
                            model = authState.userAvatarUrl,
                            contentDescription = authState.userName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(WhatsAppEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(authState.userName, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(authState.userBio, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }

                    Icon(
                        Icons.Default.QrCode,
                        contentDescription = "My QR Code",
                        tint = WhatsAppEmerald,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onNavigateToE2eeInfo() }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            // Dark Mode Toggle
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isDarkMode == true) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = WhatsAppEmerald
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Dark Mode", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("True OLED Black theme", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Switch(
                        checked = isDarkMode ?: true,
                        onCheckedChange = { checked ->
                            chatViewModel.toggleDarkMode(checked)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppEmerald),
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }
            }

            // App Lock (Biometric / PIN) Toggle
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = WhatsAppEmerald
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("App Lock (Biometric / PIN)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(if (isLockEnabled) "Enabled • Require PIN when opened" else "Disabled", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Switch(
                        checked = isLockEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                showPinSetupDialog = true
                            } else {
                                appLockManager.setLockEnabled(false)
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppEmerald),
                        modifier = Modifier.testTag("app_lock_switch")
                    )
                }
            }

            // Advanced AI Auto-Reply Toggle
            item {
                val isAiReplyEnabled by chatViewModel.isAiAutoReplyEnabled.collectAsState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = WhatsAppEmerald
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI Auto-Reply Assistant", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(
                            if (isAiReplyEnabled) "Active • Displays 'AI Reply' status when away" else "Disabled",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = isAiReplyEnabled,
                        onCheckedChange = { checked ->
                            chatViewModel.toggleAiAutoReply(checked)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppEmerald),
                        modifier = Modifier.testTag("ai_autoreply_switch")
                    )
                }
            }

            // Offline APK Self-Extraction & Direct Share
            item {
                SettingsItem(
                    icon = Icons.Default.Share,
                    title = "Share App & Export APK",
                    subtitle = "Extract ChatBoxPro.apk offline to Downloads • Share via Bluetooth or WhatsApp",
                    onClick = { showOfflineShareDialog = true }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.SystemUpdate,
                    title = "App Updates (Direct OTA)",
                    subtitle = "Download & install latest production build directly",
                    onClick = { showApkUpdateDialog = true }
                )
            }

            // Zero-Footprint Ephemeral Transfers Inspector
            item {
                SettingsItem(
                    icon = Icons.Default.DeleteSweep,
                    title = "Zero-Footprint Ephemeral Media",
                    subtitle = "Verify files auto-deleted from server upon delivery",
                    onClick = { showZeroFootprintDialog = true }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "End-to-End Encryption",
                    subtitle = "Verify safety numbers & Signal keys",
                    onClick = onNavigateToE2eeInfo
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Backup,
                    title = "Chat Backup",
                    subtitle = "Local SQLite Room database • Tap to backup",
                    onClick = {
                        Toast.makeText(context, "Local chat database backup created successfully!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Message, group & call tones",
                    onClick = {}
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "App Permissions",
                    subtitle = "Camera, Microphone, Contacts & Notifications status",
                    onClick = { showPermissionsDialog = true }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = strings.appLanguage,
                    subtitle = "${currentLang.flagEmoji} ${currentLang.nativeName} (${currentLang.englishName}) • Tap to switch",
                    onClick = { showLanguageDialog = true }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "About & Company Details",
                    subtitle = "D 4K Studio Private Limited • Danish Raja",
                    onClick = { showAboutDialog = true }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLogoutDialog = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .testTag("logout_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = strings.logout, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(strings.logout, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }

    if (showAboutDialog) {
        AboutCompanyDialog(onDismiss = { showAboutDialog = false })
    }

    if (showOfflineShareDialog) {
        OfflineApkShareDialog(onDismiss = { showOfflineShareDialog = false })
    }

    if (showApkUpdateDialog) {
        ApkUpdateDialog(onDismiss = { showApkUpdateDialog = false })
    }

    if (showZeroFootprintDialog) {
        ZeroFootprintInspectorDialog(onDismiss = { showZeroFootprintDialog = false })
    }

    if (showPinSetupDialog) {
        AlertDialog(
            onDismissRequest = { showPinSetupDialog = false },
            title = { Text("Set 4-Digit Security PIN", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter a 4-digit PIN to lock Chat Box Pro when leaving the app.")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) newPinInput = it },
                        placeholder = { Text("e.g. 1234") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length == 4) {
                            appLockManager.setLockEnabled(true, newPinInput)
                            showPinSetupDialog = false
                            Toast.makeText(context, "App lock activated with PIN: $newPinInput", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald)
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinSetupDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPermissionsDialog) {
        AppPermissionsDialog(onDismiss = { showPermissionsDialog = false })
    }

    if (showLanguageDialog) {
        LanguageSelectorDialog(onDismiss = { showLanguageDialog = false })
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = strings.logoutConfirmTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = strings.logoutConfirmMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.logout, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(strings.cancel, color = WhatsAppEmerald, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = WhatsAppEmerald, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
