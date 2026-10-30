package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY isPinned DESC, lastMessageTimestamp DESC")
    fun getAllChatsFlow(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
    suspend fun getChatById(chatId: String): ChatEntity?

    @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
    fun getChatByIdFlow(chatId: String): Flow<ChatEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)

    @Update
    suspend fun updateChat(chat: ChatEntity)

    @Query("UPDATE chats SET lastMessageText = :text, lastMessageTimestamp = :time, unreadCount = unreadCount + :unreadDelta WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: String, text: String, time: Long, unreadDelta: Int = 0)

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun clearUnread(chatId: String)

    @Query("UPDATE chats SET presence = :presence WHERE id = :chatId")
    suspend fun updatePresence(chatId: String, presence: String)

    @Delete
    suspend fun deleteChat(chat: ChatEntity)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChatById(chatId: String)

    @Query("DELETE FROM chats WHERE id IN ('chat_sarah', 'chat_android_devs', 'chat_alex', 'chat_mom')")
    suspend fun deleteMockChats()

    @Query("DELETE FROM chats")
    suspend fun clearChats()
}
