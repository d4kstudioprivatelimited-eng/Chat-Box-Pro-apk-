package com.example.data.firebase.firestore.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class ChatRoomDocument(
    @DocumentId val id: String = "",
    val name: String = "",
    val isGroup: Boolean = false,
    val participants: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val participantAvatars: Map<String, String> = emptyMap(),
    val lastMessageText: String = "",
    val lastMessageSenderId: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val groupAdminIds: List<String> = emptyList(),
    val groupDescription: String = "",
    val isOnlyAdminCanPost: Boolean = false,
    val unreadCounts: Map<String, Int> = emptyMap(),
    @ServerTimestamp val updatedAt: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)
