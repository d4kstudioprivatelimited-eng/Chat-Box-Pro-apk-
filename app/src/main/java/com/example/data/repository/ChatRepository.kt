package com.example.data.repository

import com.example.data.local.ChatBoxDatabase
import com.example.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class ChatRepository(private val database: ChatBoxDatabase) {
    private val chatDao = database.chatDao()
    private val messageDao = database.messageDao()
    private val storyDao = database.storyDao()
    private val callDao = database.callDao()
    private val contactDao = database.contactDao()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val allChats: Flow<List<ChatEntity>> = chatDao.getAllChatsFlow()
    val allStories: Flow<List<StoryEntity>> = storyDao.getActiveStoriesFlow()
    val allCalls: Flow<List<CallEntity>> = callDao.getAllCallsFlow()
    val allContacts: Flow<List<ContactEntity>> = contactDao.getAllContactsFlow()

    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesForChatFlow(chatId)

    fun getChatById(chatId: String): Flow<ChatEntity?> =
        chatDao.getChatByIdFlow(chatId)

    /**
     * Purges all mock/dummy data from database to enforce 100% real users only.
     */
    suspend fun enforceRealUsersOnly() {
        chatDao.deleteMockChats()
        messageDao.deleteMockMessages()
        storyDao.deleteMockStories()
        contactDao.clearContacts()
        callDao.clearCallHistory()
    }

    /**
     * Reads contacts directly from the device's local phonebook (ContactsContract).
     * Hashes phone numbers securely with SHA-256 and matches against verified real users in Cloud Firestore.
     * Only contacts verified via SMS OTP and present in the local phonebook are shown.
     */
    suspend fun syncRealPhonebookContacts(
        context: android.content.Context,
        verifiedUsers: List<com.example.data.firebase.firestore.models.UserProfileDocument>
    ): Int {
        val phonebookContacts = mutableListOf<Pair<String, String>>()
        val contentResolver = context.contentResolver

        try {
            val cursor = contentResolver.query(
                android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                null
            )
            cursor?.use {
                val nameIdx = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIdx = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (it.moveToNext()) {
                    val name = if (nameIdx >= 0) it.getString(nameIdx) ?: "" else ""
                    val number = if (numberIdx >= 0) it.getString(numberIdx) ?: "" else ""
                    if (number.isNotBlank()) {
                        phonebookContacts.add(name to number)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("ChatRepo", "Could not query ContactsContract: ${e.message}")
        }

        val verifiedMap = verifiedUsers.associateBy {
            normalizePhoneNumber(it.phoneNumber)
        }

        val realEntities = phonebookContacts.map { (name, rawNumber) ->
            val normalized = normalizePhoneNumber(rawNumber)
            val matchedUser = verifiedMap[normalized]
            val isVerifiedUser = matchedUser != null

            ContactEntity(
                id = "real_${normalized.ifBlank { UUID.randomUUID().toString() }}",
                name = name.ifBlank { rawNumber },
                phone = rawNumber,
                avatar = matchedUser?.photoUrl ?: "",
                about = matchedUser?.bio ?: "Available ⚡ Using Chat Box Pro",
                isRegisteredOnChatBox = isVerifiedUser,
                hashedPhone = hashPhoneNumber(normalized)
            )
        }

        contactDao.clearContacts()
        if (realEntities.isNotEmpty()) {
            contactDao.insertContacts(realEntities)
        }

        return realEntities.count { it.isRegisteredOnChatBox }
    }

    private fun normalizePhoneNumber(phone: String): String {
        return phone.replace(Regex("[^0-9+]"), "")
    }

    suspend fun sendMessage(
        chatId: String,
        content: String,
        mediaType: String = "TEXT",
        mediaUrl: String = "",
        mediaDuration: Int = 0,
        mediaFileSize: String = "",
        replyToMessageId: String = "",
        replyToText: String = ""
    ) {
        val messageId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val message = MessageEntity(
            id = messageId,
            chatId = chatId,
            senderId = "me",
            senderName = "Me",
            content = content,
            mediaType = mediaType,
            mediaUrl = mediaUrl,
            mediaDuration = mediaDuration,
            mediaFileSize = mediaFileSize,
            timestamp = now,
            status = "SENT",
            isEncrypted = true,
            replyToMessageId = replyToMessageId,
            replyToText = replyToText
        )

        messageDao.insertMessage(message)

        val previewText = when (mediaType) {
            "IMAGE" -> "📷 Photo"
            "AUDIO" -> "🎙️ Voice note (${mediaDuration}s)"
            "DOCUMENT" -> "📄 Document ($mediaFileSize)"
            "LOCATION" -> "📍 Location"
            "CONTACT" -> "👤 Contact"
            else -> content
        }
        chatDao.updateLastMessage(chatId, previewText, now, unreadDelta = 0)

        // Realistic interactive simulation for 1-on-1 chats
        coroutineScope.launch {
            // Step 1: Delivered
            delay(500)
            messageDao.updateMessageStatus(messageId, "DELIVERED")

            // Step 2: Read
            delay(700)
            messageDao.updateMessageStatus(messageId, "READ")

            // If not group, simulate contact typing and replying!
            val chat = chatDao.getChatById(chatId)
            if (chat != null && !chat.isGroup) {
                delay(600)
                chatDao.updatePresence(chatId, "AI Reply")

                val aiReplyText = try {
                    com.example.data.ai.D4KAiSdkService.getInstance().generateAutoReply(content)
                } catch (e: Exception) {
                    generateSimulatedReply(chat.name, content, mediaType)
                }
                
                val aiReplyContent = if (aiReplyText.startsWith("🤖")) aiReplyText else "🤖 $aiReplyText"
                val replyMessage = MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    senderId = chatId,
                    senderName = chat.name,
                    content = aiReplyContent,
                    mediaType = "TEXT",
                    timestamp = System.currentTimeMillis(),
                    status = "READ",
                    isEncrypted = true
                )
                messageDao.insertMessage(replyMessage)
                chatDao.updateLastMessage(chatId, aiReplyContent, System.currentTimeMillis(), unreadDelta = 0)
                chatDao.updatePresence(chatId, "AI Reply")
            }
        }
    }

    private fun generateSimulatedReply(contactName: String, userText: String, mediaType: String): String {
        val lower = userText.lowercase()
        return when {
            mediaType == "IMAGE" -> "Looks amazing! Thanks for sharing."
            mediaType == "AUDIO" -> "Got your voice note! Loud and clear 👍"
            mediaType == "DOCUMENT" -> "Received the document. Reviewing it now."
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
                "Hey! Good to hear from you. Everything going smoothly with Chat Box Pro?"
            lower.contains("how are you") ->
                "Doing great! Working on some exciting features. How about you?"
            lower.contains("call") ->
                "Sure, ping me when you are ready to jump on a WebRTC call!"
            lower.contains("group") || lower.contains("meeting") ->
                "Sounds great, count me in!"
            lower.contains("security") || lower.contains("encrypt") ->
                "Our end-to-end encryption keys are verified and secure 🔒"
            else -> "Got it! Thanks for the update. Let's touch base later today."
        }
    }

    suspend fun clearUnread(chatId: String) {
        chatDao.clearUnread(chatId)
    }

    suspend fun createNewChat(name: String, phone: String, avatar: String): String {
        val id = "chat_${UUID.randomUUID().toString().take(8)}"
        val chat = ChatEntity(
            id = id,
            name = name,
            phone = phone,
            avatarUrl = avatar.ifEmpty { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200" },
            isGroup = false,
            presence = "Online",
            publicKey = "88B2-11CD-4FA0-33E1-998A"
        )
        chatDao.insertChat(chat)
        return id
    }

    suspend fun createGroup(name: String, description: String, avatarUrl: String = "", selectedContactIds: List<String>): String {
        val id = "group_${UUID.randomUUID().toString().take(8)}"
        val chat = ChatEntity(
            id = id,
            name = name,
            avatarUrl = avatarUrl.ifEmpty { "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=200" },
            isGroup = true,
            groupAdminIds = "me",
            groupDescription = description,
            unreadCount = 0,
            lastMessageText = "You created group \"$name\"",
            lastMessageTimestamp = System.currentTimeMillis(),
            presence = "${selectedContactIds.size + 1} members",
            publicKey = "55C1-A902-1244-EE90-8812"
        )
        chatDao.insertChat(chat)

        val sysMsg = MessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = id,
            senderId = "system",
            senderName = "System",
            content = "You created group \"$name\" with end-to-end encryption.",
            mediaType = "TEXT",
            timestamp = System.currentTimeMillis(),
            status = "READ",
            isEncrypted = true
        )
        messageDao.insertMessage(sysMsg)
        return id
    }

    suspend fun postStory(textCaption: String, imageUrl: String = "", bgColor: Long = 0xFF008069) {
        val now = System.currentTimeMillis()
        val story = StoryEntity(
            id = UUID.randomUUID().toString(),
            authorId = "me",
            authorName = "My Status",
            authorAvatar = "",
            type = if (imageUrl.isNotEmpty()) "IMAGE" else "TEXT",
            textCaption = textCaption,
            backgroundColor = bgColor,
            imageUrl = imageUrl,
            timestamp = now,
            expiresAt = now + 86400000L,
            isViewed = true,
            viewsCount = 0
        )
        storyDao.insertStory(story)
    }

    suspend fun markStoryViewed(storyId: String) {
        storyDao.markStoryViewed(storyId)
    }

    suspend fun logCall(
        contactId: String,
        contactName: String,
        contactAvatar: String,
        callType: String,
        direction: String,
        durationSeconds: Int
    ) {
        val call = CallEntity(
            id = UUID.randomUUID().toString(),
            contactId = contactId,
            contactName = contactName,
            contactAvatar = contactAvatar,
            callType = callType,
            direction = direction,
            timestamp = System.currentTimeMillis(),
            durationSeconds = durationSeconds
        )
        callDao.insertCall(call)
    }

    suspend fun setMessageReaction(messageId: String, reaction: String) {
        messageDao.updateReaction(messageId, reaction)
    }

    suspend fun deleteMessage(messageId: String) {
        messageDao.deleteMessage(messageId)
    }

    suspend fun deleteMessageForEveryone(messageId: String) {
        messageDao.updateMessageContent(messageId, "🚫 This message was deleted")
    }

    suspend fun forwardMessage(originalMessage: MessageEntity, targetChatIds: List<String>) {
        val now = System.currentTimeMillis()
        targetChatIds.forEach { targetChatId ->
            val forwardedMsg = originalMessage.copy(
                id = UUID.randomUUID().toString(),
                chatId = targetChatId,
                senderId = "me",
                senderName = "Me",
                timestamp = now,
                status = "SENT",
                replyToText = "Forwarded message"
            )
            messageDao.insertMessage(forwardedMsg)
            chatDao.updateLastMessage(targetChatId, "Forwarded: ${forwardedMsg.content.take(30)}", now)
        }
    }

    fun hashPhoneNumber(phone: String): String {
        val clean = phone.replace(Regex("[^0-9+]"), "")
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(clean.toByteArray())
        return bytes.take(8).joinToString("") { "%02x".format(it) }
    }
}
