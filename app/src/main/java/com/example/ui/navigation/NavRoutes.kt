package com.example.ui.navigation

sealed class Screen(val route: String) {
    object EmailAuth : Screen("email_auth")
    object ProfileSetup : Screen("profile_setup")
    object Home : Screen("home")
    object ChatDetail : Screen("chat_detail/{chatId}") {
        fun createRoute(chatId: String) = "chat_detail/$chatId"
    }
    object NewChat : Screen("new_chat")
    object NewGroup : Screen("new_group")
    object ContactSync : Screen("contact_sync")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object EncryptionInfo : Screen("encryption_info/{chatId}") {
        fun createRoute(chatId: String) = "encryption_info/$chatId"
    }
    object CreateStatus : Screen("create_status")
}
