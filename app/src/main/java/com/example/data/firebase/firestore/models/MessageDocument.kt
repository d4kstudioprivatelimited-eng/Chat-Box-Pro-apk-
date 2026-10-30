package com.example.data.firebase.firestore.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class MessageDocument(
    @DocumentId val id: String = "",
    val roomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val mediaType: String = "TEXT", // TEXT, IMAGE, AUDIO, DOCUMENT, LOCATION, CONTACT
    val mediaUrl: String = "",
    val mediaDuration: Int = 0,
    val mediaFileSize: String = "",
    val status: String = "SENT", // SENT, DELIVERED, READ
    val isEncrypted: Boolean = true,
    val reaction: String = "",
    val replyToMessageId: String = "",
    val replyToText: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @ServerTimestamp val createdAt: Timestamp? = null
)
