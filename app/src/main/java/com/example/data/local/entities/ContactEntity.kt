package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val avatar: String = "",
    val about: String = "Hey there! I am using Chat Box Pro.",
    val isRegisteredOnChatBox: Boolean = true,
    val hashedPhone: String = ""
)
