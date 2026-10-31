package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatBoxDatabase
import com.example.data.local.entities.*
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ActiveCallState(
    val contactId: String,
    val contactName: String,
    val contactAvatar: String,
    val callType: String, // VOICE, VIDEO
    val status: String = "Connecting...", // Calling..., Ringing..., Connected, Incoming...
    val direction: String = "OUTGOING", // OUTGOING, INCOMING
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeaker: Boolean = false,
    val isVideoDisabled: Boolean = false,
    val isFrontCamera: Boolean = true,
    val isGroupCall: Boolean = false,
    val participants: List<com.example.data.signaling.CallParticipant> = emptyList(),
    val isPiPMode: Boolean = false
)

data class InAppNotificationAlert(
    val id: String = java.util.UUID.randomUUID().toString(),
    val chatId: String,
    val senderName: String,
    val messageText: String,
    val avatarUrl: String = ""
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val database = ChatBoxDatabase.getDatabase(application)
    val repository = ChatRepository(database)
    val firestoreRepository = com.example.data.firebase.firestore.FirestoreChatRepository(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(null) // null = system
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val prefs = application.getSharedPreferences("chatbox_settings", android.content.Context.MODE_PRIVATE)
    private val _isAiAutoReplyEnabled = MutableStateFlow(
        prefs.getBoolean("is_ai_autoreply_enabled", true)
    )
    val isAiAutoReplyEnabled: StateFlow<Boolean> = _isAiAutoReplyEnabled.asStateFlow()

    private val defaultQuickReplies = listOf("Sounds good! 👍", "On my way! 🚀", "Call you in 5 📞", "Thanks! 🙏", "Send details 📄")
    private val _customQuickReplies = MutableStateFlow<List<String>>(
        prefs.getStringSet("custom_quick_replies", defaultQuickReplies.toSet())?.toList() ?: defaultQuickReplies
    )
    val customQuickReplies: StateFlow<List<String>> = _customQuickReplies.asStateFlow()

    fun addCustomQuickReply(reply: String) {
        if (reply.isNotBlank() && !_customQuickReplies.value.contains(reply)) {
            val updated = _customQuickReplies.value + reply.trim()
            _customQuickReplies.value = updated
            prefs.edit().putStringSet("custom_quick_replies", updated.toSet()).apply()
        }
    }

    fun removeCustomQuickReply(reply: String) {
        val updated = _customQuickReplies.value.filter { it != reply }
        _customQuickReplies.value = updated
        prefs.edit().putStringSet("custom_quick_replies", updated.toSet()).apply()
    }

    fun toggleAiAutoReply(enabled: Boolean) {
        prefs.edit().putBoolean("is_ai_autoreply_enabled", enabled).apply()
        _isAiAutoReplyEnabled.value = enabled
    }


    // Chats filtered by search query
    val chats: StateFlow<List<ChatEntity>> = combine(
        repository.allChats,
        _searchQuery
    ) { chatList, query ->
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            chatList
        } else {
            chatList.filter {
                it.name.contains(trimmed, ignoreCase = true) ||
                it.phone.contains(trimmed, ignoreCase = true) ||
                it.lastMessageText.contains(trimmed, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stories: StateFlow<List<StoryEntity>> = repository.allStories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calls: StateFlow<List<CallEntity>> = repository.allCalls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<ContactEntity>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active VoIP Call State
    private val _activeCall = MutableStateFlow<ActiveCallState?>(null)
    val activeCall: StateFlow<ActiveCallState?> = _activeCall.asStateFlow()
    private var callTimerJob: Job? = null

    // Status Story Viewer State
    private val _viewingStories = MutableStateFlow<List<StoryEntity>>(emptyList())
    val viewingStories: StateFlow<List<StoryEntity>> = _viewingStories.asStateFlow()
    private val _storyIndex = MutableStateFlow(0)
    val storyIndex: StateFlow<Int> = _storyIndex.asStateFlow()

    // In-App Push Notification Alert State
    private val _inAppNotification = MutableStateFlow<InAppNotificationAlert?>(null)
    val inAppNotification: StateFlow<InAppNotificationAlert?> = _inAppNotification.asStateFlow()

    fun dismissNotification() {
        _inAppNotification.value = null
    }

    fun triggerNotificationAlert(chatId: String, senderName: String, messageText: String, avatarUrl: String = "") {
        _inAppNotification.value = InAppNotificationAlert(
            chatId = chatId,
            senderName = senderName,
            messageText = messageText,
            avatarUrl = avatarUrl
        )
        viewModelScope.launch {
            delay(4000)
            if (_inAppNotification.value?.chatId == chatId) {
                _inAppNotification.value = null
            }
        }
    }

    init {
        viewModelScope.launch {
            repository.enforceRealUsersOnly()
        }
    }

    fun syncContacts(context: android.content.Context, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch {
            val verifiedUsers = firestoreRepository.getAllVerifiedUsers()
            val matchedCount = repository.syncRealPhonebookContacts(context, verifiedUsers)
            onComplete(matchedCount)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleDarkMode(enableDark: Boolean?) {
        _isDarkMode.value = enableDark
    }

    fun getChatMessages(chatId: String): Flow<List<MessageEntity>> {
        return repository.getMessagesForChat(chatId)
    }

    fun getChat(chatId: String): Flow<ChatEntity?> {
        return repository.getChatById(chatId)
    }

    fun markChatRead(chatId: String) {
        viewModelScope.launch {
            repository.clearUnread(chatId)
        }
    }

    fun sendMessage(
        chatId: String,
        content: String,
        mediaType: String = "TEXT",
        mediaUrl: String = "",
        mediaDuration: Int = 0,
        mediaFileSize: String = "",
        replyToMessageId: String = "",
        replyToText: String = ""
    ) {
        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                content = content,
                mediaType = mediaType,
                mediaUrl = mediaUrl,
                mediaDuration = mediaDuration,
                mediaFileSize = mediaFileSize,
                replyToMessageId = replyToMessageId,
                replyToText = replyToText
            )
            // Asynchronously sync to Cloud Firestore
            try {
                firestoreRepository.sendMessage(
                    roomId = chatId,
                    message = com.example.data.firebase.firestore.models.MessageDocument(
                        roomId = chatId,
                        senderId = "me",
                        senderName = "Alex Developer",
                        content = content,
                        mediaType = mediaType,
                        mediaUrl = mediaUrl,
                        mediaDuration = mediaDuration,
                        mediaFileSize = mediaFileSize,
                        status = "SENT",
                        replyToMessageId = replyToMessageId,
                        replyToText = replyToText,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (_: Exception) {}
        }
    }

    fun reactToMessage(messageId: String, emoji: String) {
        viewModelScope.launch {
            repository.setMessageReaction(messageId, emoji)
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
        }
    }

    fun createChat(name: String, phone: String, avatar: String, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val chatId = repository.createNewChat(name, phone, avatar)
            try {
                firestoreRepository.createOrUpdateChatRoom(
                    com.example.data.firebase.firestore.models.ChatRoomDocument(
                        id = chatId,
                        name = name,
                        isGroup = false,
                        participants = listOf("me", phone),
                        participantNames = mapOf("me" to "Alex Developer", phone to name),
                        participantAvatars = mapOf("me" to "", phone to avatar)
                    )
                )
            } catch (_: Exception) {}
            onCreated(chatId)
        }
    }

    fun createGroup(name: String, desc: String, avatarUrl: String = "", members: List<String>, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val chatId = repository.createGroup(name, desc, avatarUrl, members)
            try {
                firestoreRepository.createOrUpdateChatRoom(
                    com.example.data.firebase.firestore.models.ChatRoomDocument(
                        id = chatId,
                        name = name,
                        isGroup = true,
                        participants = members + "me",
                        groupDescription = desc
                    )
                )
            } catch (_: Exception) {}
            onCreated(chatId)
        }
    }

    // Call Actions
    fun startCall(contactId: String, contactName: String, contactAvatar: String, callType: String) {
        _activeCall.value = ActiveCallState(
            contactId = contactId,
            contactName = contactName,
            contactAvatar = contactAvatar,
            callType = callType,
            status = "Calling...",
            direction = "OUTGOING"
        )

        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            delay(1200)
            _activeCall.value = _activeCall.value?.copy(status = "Ringing...")
            delay(1800)
            _activeCall.value = _activeCall.value?.copy(status = "Connected")

            // Count call duration seconds
            while (_activeCall.value != null) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let {
                    it.copy(durationSeconds = it.durationSeconds + 1)
                }
            }
        }
    }

    fun receiveIncomingCall(contactId: String, contactName: String, contactAvatar: String, callType: String) {
        callTimerJob?.cancel()
        _activeCall.value = ActiveCallState(
            contactId = contactId,
            contactName = contactName,
            contactAvatar = contactAvatar,
            callType = callType,
            status = "Incoming...",
            direction = "INCOMING"
        )
    }

    fun acceptCall() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(status = "Connected")

        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (_activeCall.value != null) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let {
                    it.copy(durationSeconds = it.durationSeconds + 1)
                }
            }
        }
    }

    fun declineCall() {
        val currentCall = _activeCall.value
        callTimerJob?.cancel()
        callTimerJob = null
        if (currentCall != null) {
            viewModelScope.launch {
                repository.logCall(
                    contactId = currentCall.contactId,
                    contactName = currentCall.contactName,
                    contactAvatar = currentCall.contactAvatar,
                    callType = currentCall.callType,
                    direction = "MISSED",
                    durationSeconds = 0
                )
            }
        }
        _activeCall.value = null
    }

    fun toggleMute() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleSpeaker() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeaker = !it.isSpeaker) }
    }

    fun toggleVideoDisabled() {
        _activeCall.value = _activeCall.value?.let { it.copy(isVideoDisabled = !it.isVideoDisabled) }
    }

    fun flipCamera() {
        _activeCall.value = _activeCall.value?.let { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun endCall() {
        val currentCall = _activeCall.value
        callTimerJob?.cancel()
        callTimerJob = null
        if (currentCall != null) {
            viewModelScope.launch {
                repository.logCall(
                    contactId = currentCall.contactId,
                    contactName = currentCall.contactName,
                    contactAvatar = currentCall.contactAvatar,
                    callType = currentCall.callType,
                    direction = currentCall.direction,
                    durationSeconds = currentCall.durationSeconds
                )
            }
        }
        _activeCall.value = null
    }

    private val _statusPrivacyOption = MutableStateFlow(com.example.ui.screens.status.StatusPrivacyOption.MY_CONTACTS)
    val statusPrivacyOption: StateFlow<com.example.ui.screens.status.StatusPrivacyOption> = _statusPrivacyOption.asStateFlow()

    fun updateStatusPrivacy(option: com.example.ui.screens.status.StatusPrivacyOption) {
        _statusPrivacyOption.value = option
    }

    fun togglePiPMode() {
        _activeCall.value = _activeCall.value?.let { it.copy(isPiPMode = !it.isPiPMode) }
    }

    fun deleteMessageForEveryone(messageId: String) {
        viewModelScope.launch {
            repository.deleteMessageForEveryone(messageId)
        }
    }

    fun deleteMessageForMe(messageId: String) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
        }
    }

    fun forwardMessage(message: MessageEntity, targetChatIds: List<String>) {
        viewModelScope.launch {
            repository.forwardMessage(message, targetChatIds)
        }
    }

    // Story / Status Actions
    fun openStories(storyList: List<StoryEntity>, initialIndex: Int = 0) {
        _viewingStories.value = storyList
        _storyIndex.value = initialIndex
    }

    fun closeStories() {
        _viewingStories.value = emptyList()
        _storyIndex.value = 0
    }

    fun nextStory() {
        val list = _viewingStories.value
        if (_storyIndex.value < list.size - 1) {
            _storyIndex.value += 1
            viewModelScope.launch {
                repository.markStoryViewed(list[_storyIndex.value].id)
            }
        } else {
            closeStories()
        }
    }

    fun previousStory() {
        if (_storyIndex.value > 0) {
            _storyIndex.value -= 1
        }
    }

    fun postStory(caption: String, imageUrl: String = "", bgColor: Long = 0xFF008069) {
        viewModelScope.launch {
            repository.postStory(caption, imageUrl, bgColor)
        }
    }
}
