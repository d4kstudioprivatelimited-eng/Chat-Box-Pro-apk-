package com.example
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.platform.testTag
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.screens.auth.ProfileSetupScreen
import com.example.ui.screens.call.CallScreen
import com.example.ui.screens.chat.ChatDetailScreen
import com.example.ui.screens.chat.EncryptionInfoScreen
import com.example.ui.screens.contacts.ContactSyncScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.NewChatScreen
import com.example.ui.screens.home.NewGroupScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.status.CreateStatusScreen
import com.example.ui.screens.status.StoryViewerScreen
import com.example.ui.theme.ChatBoxProTheme
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.ChatViewModel

class MainActivity :FragmentActivity () {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.example.ui.localization.LocalizationManager.init(this)

        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val emailAuthViewModel: com.example.ui.viewmodel.EmailAuthViewModel = viewModel()
            val chatViewModel: ChatViewModel = viewModel()
            val appLockManager = remember { com.example.ui.screens.security.AppLockManager(this@MainActivity) }

            val authState by authViewModel.uiState.collectAsState()
            val isDarkPref by chatViewModel.isDarkMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val useDarkMode = isDarkPref ?: systemDark
            val isAppLocked by appLockManager.isLocked.collectAsState()

            val activeCall by chatViewModel.activeCall.collectAsState()
            val viewingStories by chatViewModel.viewingStories.collectAsState()
            val storyIndex by chatViewModel.storyIndex.collectAsState()
            val inAppNotification by chatViewModel.inAppNotification.collectAsState()

            val currentLang by com.example.ui.localization.LocalizationManager.currentLanguage.collectAsState()
            val currentStrings by com.example.ui.localization.LocalizationManager.currentStrings.collectAsState()

            var hasStartupPermissions by remember {
                mutableStateOf(
                    androidx.core.content.ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.READ_CONTACTS) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                    androidx.core.content.ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                    androidx.core.content.ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED
                )
            }

            ChatBoxProTheme(darkTheme = useDarkMode) {
                com.example.ui.localization.ProvideLocalizedStrings(currentStrings, currentLang) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        if (!hasStartupPermissions) {
                            com.example.ui.screens.auth.StartupPermissionsSplashScreen(
                                onPermissionsGranted = {
                                    hasStartupPermissions = true
                                }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize()) {
                                val navController = rememberNavController()

                                val startDestination = if (authState.isAuthenticated) {
                                    if (authState.isProfileSetupDone) Screen.Home.route else Screen.ProfileSetup.route
                                } else {
                                    Screen.EmailAuth.route
                                }

                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {
                            composable(Screen.EmailAuth.route) {
                                com.example.ui.screens.auth.EmailAuthScreen(
                                    emailAuthViewModel = emailAuthViewModel,
                                    authViewModel = authViewModel,
                                    onAuthSuccess = {
                                        if (authViewModel.uiState.value.isProfileSetupDone) {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.EmailAuth.route) { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate(Screen.ProfileSetup.route) {
                                                popUpTo(Screen.EmailAuth.route) { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }

                            composable(Screen.ProfileSetup.route) {
                                ProfileSetupScreen(
                                    authViewModel = authViewModel,
                                    onComplete = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.Home.route) {
                                HomeScreen(
                                    chatViewModel = chatViewModel,
                                    onNavigateToChat = { chatId ->
                                        navController.navigate(Screen.ChatDetail.createRoute(chatId))
                                    },
                                    onNavigateToNewChat = {
                                        navController.navigate(Screen.NewChat.route)
                                    },
                                    onNavigateToNewGroup = {
                                        navController.navigate(Screen.NewGroup.route)
                                    },
                                    onNavigateToSettings = {
                                        navController.navigate(Screen.Settings.route)
                                    },
                                    onNavigateToCreateStatus = {
                                        navController.navigate(Screen.CreateStatus.route)
                                    },
                                    onNavigateToContactSync = {
                                        navController.navigate(Screen.ContactSync.route)
                                    },
                                    onNavigateToE2eeInfo = {
                                        navController.navigate(Screen.EncryptionInfo.createRoute("general"))
                                    }
                                )
                            }

                            composable(
                                route = Screen.ChatDetail.route,
                                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                                ChatDetailScreen(
                                    chatId = chatId,
                                    chatViewModel = chatViewModel,
                                    onBack = { navController.popBackStack() },
                                    onStartCall = { contactId, name, avatar, type ->
                                        chatViewModel.startCall(contactId, name, avatar, type)
                                    }
                                )
                            }

                            composable(Screen.NewChat.route) {
                                NewChatScreen(
                                    chatViewModel = chatViewModel,
                                    onBack = { navController.popBackStack() },
                                    onNavigateToNewGroup = {
                                        navController.navigate(Screen.NewGroup.route)
                                    },
                                    onChatSelected = { chatId ->
                                        navController.navigate(Screen.ChatDetail.createRoute(chatId)) {
                                            popUpTo(Screen.NewChat.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.NewGroup.route) {
                                NewGroupScreen(
                                    chatViewModel = chatViewModel,
                                    onBack = { navController.popBackStack() },
                                    onGroupCreated = { groupId ->
                                        navController.navigate(Screen.ChatDetail.createRoute(groupId)) {
                                            popUpTo(Screen.NewGroup.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.ContactSync.route) {
                                ContactSyncScreen(
                                    chatViewModel = chatViewModel,
                                    onBack = { navController.popBackStack() },
                                    onContactSelected = { chatId ->
                                        navController.navigate(Screen.ChatDetail.createRoute(chatId))
                                    }
                                )
                            }

                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    authViewModel = authViewModel,
                                    chatViewModel = chatViewModel,
                                    appLockManager = appLockManager,
                                    onBack = { navController.popBackStack() },
                                    onNavigateToProfile = {
                                        navController.navigate(Screen.ProfileSetup.route)
                                    },
                                    onNavigateToE2eeInfo = {
                                        navController.navigate(Screen.EncryptionInfo.createRoute("general"))
                                    },
                                    onLogout = {
                                        authViewModel.logOut()
                                        navController.navigate(Screen.EmailAuth.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(
                                route = Screen.EncryptionInfo.route,
                                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
                            ) {
                                EncryptionInfoScreen(
                                    contactName = "Your Contact",
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.CreateStatus.route) {
                                CreateStatusScreen(
                                    chatViewModel = chatViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                        // Full-screen overlay for active VoIP / WebRTC Call
                        activeCall?.let { call ->
                            CallScreen(
                                callState = call,
                                chatViewModel = chatViewModel
                            )
                        }

                        // Full-screen overlay for 24-Hour Story / Status Viewer
                        if (viewingStories.isNotEmpty()) {
                            StoryViewerScreen(
                                stories = viewingStories,
                                initialIndex = storyIndex,
                                chatViewModel = chatViewModel,
                                onClose = { chatViewModel.closeStories() }
                            )
                        }

                        // Full-screen overlay for App Lock (Biometric / PIN)
                        if (isAppLocked) {
                            com.example.ui.screens.security.AppLockScreen(
                                appLockManager = appLockManager
                            )
                        }

                        // Sahiba AI Autonomous Agent Floating Trigger FAB
                        if (authState.isAuthenticated && authState.isProfileSetupDone && !isAppLocked) {
                            var showSahibaAiOverlay by remember { mutableStateOf(false) }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 76.dp, end = 16.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                FloatingActionButton(
                                    onClick = { showSahibaAiOverlay = true },
                                    containerColor = WhatsAppEmerald,
                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .testTag("sahiba_ai_fab_btn")
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = "Activate Sahiba AI Agent",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            if (showSahibaAiOverlay) {
                                com.example.ui.components.SahibaAiAutonomousAgentOverlay(
                                    chatViewModel = chatViewModel,
                                    authViewModel = authViewModel,
                                    onNavigateToChat = { chatId ->
                                        navController.navigate(Screen.ChatDetail.createRoute(chatId))
                                    },
                                    onNavigateToSettings = {
                                        navController.navigate(Screen.Settings.route)
                                    },
                                    onClose = { showSahibaAiOverlay = false }
                                )
                            }
                        }

                        // Heads-Up In-App Push Notification Banner
                        AnimatedVisibility(
                            visible = inAppNotification != null,
                            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            inAppNotification?.let { alert ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate(Screen.ChatDetail.createRoute(alert.chatId))
                                            chatViewModel.dismissNotification()
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(42.dp),
                                            shape = CircleShape,
                                            color = WhatsAppEmerald
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Notifications,
                                                    contentDescription = "Notification",
                                                    tint = androidx.compose.ui.graphics.Color.White,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = alert.senderName,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = alert.messageText,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        IconButton(
                                            onClick = { chatViewModel.dismissNotification() },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Dismiss",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
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
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Authentication failed. Please try again.")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("App Lock")
            .setSubtitle("Unlock using fingerprint or device screen lock")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
}




