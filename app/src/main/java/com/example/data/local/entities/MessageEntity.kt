package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String, // "me" or contact phone/id
    val senderName: String = "",
    val content: String = "",
    val mediaType: String = "TEXT", // TEXT, IMAGE, AUDIO, DOCUMENT, LOCATION, CONTACT
    val mediaUrl: String = "",
    val mediaDuration: Int = 0, // audio duration in seconds
    val mediaFileSize: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "READ", // SENT, DELIVERED, READ
    val isEncrypted: Boolean = true,
    val reaction: String = "",
    val replyToMessageId: String = "",
    val replyToText: String = ""
)
