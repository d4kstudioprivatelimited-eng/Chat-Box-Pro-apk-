package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String = "",
    val avatarUrl: String = "",
    val isGroup: Boolean = false,
    val groupAdminIds: String = "",
    val groupDescription: String = "",
    val isOnlyAdminCanPost: Boolean = false,
    val unreadCount: Int = 0,
    val lastMessageText: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val presence: String = "Online",
    val publicKey: String = "4F8A-9B21-EE3C-78A0-112D"
)
