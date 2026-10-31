package com.example.data.firebase.firestore.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class StoryDocument(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String = "",
    val caption: String = "",
    val mediaUrl: String = "",
    val bgColor: Long = 0xFF008069,
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000L)
)
