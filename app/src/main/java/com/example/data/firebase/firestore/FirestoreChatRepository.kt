package com.example.data.firebase.firestore

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.firebase.firestore.models.ChatRoomDocument
import com.example.data.firebase.firestore.models.MessageDocument
import com.example.data.firebase.firestore.models.UserProfileDocument
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TAG = "FirestoreChatRepo"

suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { result ->
        if (cont.isActive) cont.resume(result)
    }
    addOnFailureListener { exception ->
        if (cont.isActive) cont.resumeWithException(exception)
    }
    addOnCanceledListener {
        cont.cancel()
    }
}

/**
 * Repository responsible for real-time syncing of user profiles, chat rooms,
 * and message documents with Firebase Cloud Firestore.
 */
class FirestoreChatRepository(
    private val firestore: FirebaseFirestore
) {
    // Secondary constructor resolving database ID from string resource
    constructor(context: Context) : this(
        resolveFirestore(context.applicationContext)
    )

    companion object {
        private fun resolveFirestore(context: Context): FirebaseFirestore {
            return try {
                val dbId = context.getString(R.string.firestore_database_id)
                if (dbId.isBlank() || dbId == "(default)") {
                    FirebaseFirestore.getInstance()
                } else {
                    FirebaseFirestore.getInstance(dbId)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error resolving firestore with databaseId, falling back: ${e.message}")
                FirebaseFirestore.getInstance()
            }
        }
    }

    // =========================================================================
    // 1. User Profiles
    // =========================================================================

    /**
     * Real-time stream of a user's profile from Firestore.
     */
    fun observeUserProfile(userId: String): Flow<UserProfileDocument?> = callbackFlow {
        if (userId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val docRef = firestore.collection("users").document(userId)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observing user profile: ${error.message}")
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(UserProfileDocument::class.java)
                trySend(profile)
            } else {
                trySend(null)
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    /**
     * Creates or updates a user's profile document.
     */
    suspend fun syncUserProfile(profile: UserProfileDocument) {
        val uid = profile.id.ifBlank { return }
        try {
            kotlinx.coroutines.withTimeoutOrNull(2000L) {
                firestore.collection("users").document(uid)
                    .set(profile, SetOptions.merge())
                    .awaitTask()
            }
            Log.d(TAG, "UserProfile synced successfully for $uid")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync user profile: ${e.message}", e)
        }
    }

    /**
     * Submits a real verification request (Blue Tick) to Firestore verification_requests collection.
     */
    suspend fun requestVerification(userId: String, email: String, name: String) {
        if (userId.isBlank()) return
        try {
            val docData = mapOf(
                "userId" to userId,
                "email" to email,
                "name" to name,
                "requestedAt" to System.currentTimeMillis(),
                "status" to "PENDING"
            )
            firestore.collection("verification_requests")
                .document(userId)
                .set(docData, SetOptions.merge())
                .awaitTask()
            Log.d(TAG, "Verification request submitted to Firestore for $userId")
        } catch (e: Exception) {
            Log.w(TAG, "Error submitting verification request: ${e.message}")
        }
    }

    /**
     * Updates real-time presence (online/lastSeen) state in Firestore.
     */
    suspend fun updateUserPresence(userId: String, isOnline: Boolean) {
        if (userId.isBlank()) return
        try {
            firestore.collection("users").document(userId)
                .update(
                    mapOf(
                        "isOnline" to isOnline,
                        "lastSeen" to System.currentTimeMillis()
                    )
                ).awaitTask()
        } catch (e: Exception) {
            Log.w(TAG, "Presence update error: ${e.message}")
        }
    }

    /**
     * Fetches a one-time snapshot of a user profile.
     */
    suspend fun getUserProfile(userId: String): UserProfileDocument? {
        if (userId.isBlank()) return null
        return try {
            val snapshot = firestore.collection("users").document(userId).get().awaitTask()
            snapshot.toObject(UserProfileDocument::class.java)
        } catch (e: Exception) {
            Log.w(TAG, "Error getting user profile: ${e.message}")
            null
        }
    }

    /**
     * Retrieves all verified user profiles from Cloud Firestore for phonebook contact matching.
     */
    suspend fun getAllVerifiedUsers(): List<UserProfileDocument> {
        return try {
            val snapshot = firestore.collection("users").get().awaitTask()
            snapshot.toObjects(UserProfileDocument::class.java)
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching verified users: ${e.message}")
            emptyList()
        }
    }

    // =========================================================================
    // 2. Chat Rooms
    // =========================================================================

    /**
     * Real-time stream of all chat rooms where the given user is a participant.
     */
    fun observeChatRoomsForUser(userId: String): Flow<List<ChatRoomDocument>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val query = firestore.collection("chat_rooms")
            .whereArrayContains("participants", userId)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observing chat rooms: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val rooms = snapshot.toObjects(ChatRoomDocument::class.java)
                trySend(rooms.sortedByDescending { it.lastMessageTimestamp })
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    /**
     * Real-time stream of a single chat room.
     */
    fun observeChatRoom(roomId: String): Flow<ChatRoomDocument?> = callbackFlow {
        if (roomId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val docRef = firestore.collection("chat_rooms").document(roomId)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observing chat room $roomId: ${error.message}")
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(ChatRoomDocument::class.java))
            } else {
                trySend(null)
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    /**
     * Creates or updates a chat room document.
     */
    suspend fun createOrUpdateChatRoom(room: ChatRoomDocument) {
        val roomId = room.id.ifBlank { "room_${UUID.randomUUID()}" }
        val finalRoom = if (room.id.isBlank()) room.copy(id = roomId) else room
        try {
            firestore.collection("chat_rooms").document(roomId)
                .set(finalRoom, SetOptions.merge())
                .awaitTask()
            Log.d(TAG, "Chat room synced: $roomId")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing chat room: ${e.message}", e)
        }
    }

    /**
     * Updates the last message snippet, timestamp, and unread count in a chat room.
     */
    suspend fun updateLastMessage(
        roomId: String,
        lastMessageText: String,
        senderId: String,
        timestamp: Long = System.currentTimeMillis()
    ) {
        if (roomId.isBlank()) return
        try {
            firestore.collection("chat_rooms").document(roomId)
                .update(
                    mapOf(
                        "lastMessageText" to lastMessageText,
                        "lastMessageSenderId" to senderId,
                        "lastMessageTimestamp" to timestamp
                    )
                ).awaitTask()
        } catch (e: Exception) {
            Log.w(TAG, "Error updating last message for $roomId: ${e.message}")
        }
    }

    // =========================================================================
    // 3. Message Documents
    // =========================================================================

    /**
     * Real-time stream of all messages in a chat room, ordered chronologically.
     */
    fun observeMessages(roomId: String): Flow<List<MessageDocument>> = callbackFlow {
        if (roomId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val query = firestore.collection("chat_rooms")
            .document(roomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observing messages for room $roomId: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val messages = snapshot.toObjects(MessageDocument::class.java)
                trySend(messages)
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    /**
     * Sends a new message to a chat room document subcollection.
     * Also updates the parent chat room's last message text and timestamp.
     */
    suspend fun sendMessage(roomId: String, message: MessageDocument): String {
        require(roomId.isNotBlank()) { "roomId cannot be blank" }
        val msgId = message.id.ifBlank { "msg_${UUID.randomUUID()}" }
        val finalMsg = message.copy(id = msgId, roomId = roomId)

        try {
            firestore.collection("chat_rooms")
                .document(roomId)
                .collection("messages")
                .document(msgId)
                .set(finalMsg)
                .awaitTask()

            // Update parent room
            val previewText = when (finalMsg.mediaType) {
                "IMAGE" -> "📷 Photo"
                "AUDIO" -> "🎤 Voice note"
                "DOCUMENT" -> "📄 Document"
                "LOCATION" -> "📍 Live Location"
                else -> finalMsg.content
            }
            updateLastMessage(roomId, previewText, finalMsg.senderId, finalMsg.timestamp)
            Log.d(TAG, "Message $msgId sent to room $roomId")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message to Firestore: ${e.message}", e)
        }

        return msgId
    }

    /**
     * Updates delivery or read receipt status (SENT -> DELIVERED -> READ).
     */
    suspend fun updateMessageStatus(roomId: String, messageId: String, status: String) {
        if (roomId.isBlank() || messageId.isBlank()) return
        try {
            firestore.collection("chat_rooms")
                .document(roomId)
                .collection("messages")
                .document(messageId)
                .update("status", status)
                .awaitTask()
        } catch (e: Exception) {
            Log.w(TAG, "Error updating message status: ${e.message}")
        }
    }

    /**
     * Sets or clears an emoji reaction on a message.
     */
    suspend fun setMessageReaction(roomId: String, messageId: String, reaction: String) {
        if (roomId.isBlank() || messageId.isBlank()) return
        try {
            firestore.collection("chat_rooms")
                .document(roomId)
                .collection("messages")
                .document(messageId)
                .update("reaction", reaction)
                .awaitTask()
        } catch (e: Exception) {
            Log.w(TAG, "Error setting reaction on $messageId: ${e.message}")
        }
    }

    /**
     * Deletes a message document from Firestore.
     */
    suspend fun deleteMessage(roomId: String, messageId: String) {
        if (roomId.isBlank() || messageId.isBlank()) return
        try {
            firestore.collection("chat_rooms")
                .document(roomId)
                .collection("messages")
                .document(messageId)
                .delete()
                .awaitTask()
            Log.d(TAG, "Message $messageId deleted from room $roomId")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting message $messageId: ${e.message}", e)
        }
    }

    // =========================================================================
    // 4. Status / Story Documents (Real-Time Cloud Storage Sync)
    // =========================================================================

    /**
     * Real-time observation of active 24-hour stories posted across Cloud Firestore.
     */
    fun observeActiveStories(): Flow<List<com.example.data.firebase.firestore.models.StoryDocument>> = callbackFlow {
        val now = System.currentTimeMillis()
        val query = firestore.collection("stories")
            .whereGreaterThan("expiresAt", now)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observing stories: ${error.message}")
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val storyDocs = snapshot.toObjects(com.example.data.firebase.firestore.models.StoryDocument::class.java)
                trySend(storyDocs.sortedByDescending { it.timestamp })
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    /**
     * Posts a new 24-hour story document to Cloud Firestore.
     */
    suspend fun postStory(story: com.example.data.firebase.firestore.models.StoryDocument): String {
        val storyId = story.id.ifBlank { "story_${UUID.randomUUID()}" }
        val finalStory = story.copy(id = storyId)
        try {
            firestore.collection("stories")
                .document(storyId)
                .set(finalStory)
                .awaitTask()
            Log.d(TAG, "Story $storyId published to Cloud Firestore")
        } catch (e: Exception) {
            Log.e(TAG, "Error posting story: ${e.message}", e)
        }
        return storyId
    }
}
