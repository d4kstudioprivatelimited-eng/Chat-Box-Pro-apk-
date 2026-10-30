package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val authorId: String, // "me" or contactId
    val authorName: String,
    val authorAvatar: String = "",
    val type: String = "TEXT", // TEXT, IMAGE
    val textCaption: String = "",
    val backgroundColor: Long = 0xFF008069,
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 86400000L, // 24 hours
    val isViewed: Boolean = false,
    val viewsCount: Int = 12
)
