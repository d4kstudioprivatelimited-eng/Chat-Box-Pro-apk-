package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entities.*

@Database(
    entities = [
        ChatEntity::class,
        MessageEntity::class,
        StoryEntity::class,
        CallEntity::class,
        ContactEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChatBoxDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun storyDao(): StoryDao
    abstract fun callDao(): CallDao
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: ChatBoxDatabase? = null

        fun getDatabase(context: Context): ChatBoxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatBoxDatabase::class.java,
                    "chatbox_pro_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
