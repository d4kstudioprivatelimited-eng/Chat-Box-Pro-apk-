package com.example

import com.example.data.firebase.firestore.models.ChatRoomDocument
import com.example.data.firebase.firestore.models.MessageDocument
import com.example.data.firebase.firestore.models.UserProfileDocument
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FirestoreChatRepositoryTest {

    @Test
    fun `test user profile document creation and default fields`() {
        val profile = UserProfileDocument(
            id = "user_123",
            displayName = "Danish Raja",
            phoneNumber = "+919430320021",
            photoUrl = "https://example.com/avatar.jpg",
            bio = "Lead Architect at D 4K Studio Private Limited",
            isOnline = true
        )

        assertEquals("user_123", profile.id)
        assertEquals("Danish Raja", profile.displayName)
        assertEquals("+919430320021", profile.phoneNumber)
        assertTrue(profile.isOnline)
        assertEquals("4F8A-9B21-EE3C-78A0-112D", profile.publicKey)
    }

    @Test
    fun `test chat room document structure`() {
        val room = ChatRoomDocument(
            id = "room_abc",
            name = "Project Group",
            isGroup = true,
            participants = listOf("user_123", "user_456"),
            lastMessageText = "Hello from Chat Box Pro",
            lastMessageSenderId = "user_123"
        )

        assertEquals("room_abc", room.id)
        assertTrue(room.isGroup)
        assertEquals(2, room.participants.size)
        assertEquals("Hello from Chat Box Pro", room.lastMessageText)
    }

    @Test
    fun `test message document fields`() {
        val message = MessageDocument(
            id = "msg_001",
            roomId = "room_abc",
            senderId = "user_123",
            senderName = "Danish Raja",
            content = "Encrypted message payload",
            mediaType = "TEXT",
            status = "DELIVERED",
            isEncrypted = true
        )

        assertEquals("msg_001", message.id)
        assertEquals("room_abc", message.roomId)
        assertEquals("DELIVERED", message.status)
        assertTrue(message.isEncrypted)
    }
}
