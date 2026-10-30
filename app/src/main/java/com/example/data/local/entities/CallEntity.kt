package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calls")
data class CallEntity(
    @PrimaryKey val id: String,
    val contactId: String,
    val contactName: String,
    val contactAvatar: String = "",
    val callType: String = "VOICE", // VOICE, VIDEO
    val direction: String = "INCOMING", // INCOMING, OUTGOING, MISSED
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0
)
