package com.example.data.firebase.firestore.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class UserProfileDocument(
    @DocumentId val id: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
    val bio: String = "Available ⚡ Using Chat Box Pro",
    val isOnline: Boolean = true,
    val lastSeen: Long = System.currentTimeMillis(),
    val publicKey: String = "4F8A-9B21-EE3C-78A0-112D",
    val isVerified: Boolean = false,
    val aiAssistantName: String = "Sahiba AI",
    val aiTone: String = "Friendly",
    val translationLanguage: String = "English",
    @ServerTimestamp val updatedAt: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)
