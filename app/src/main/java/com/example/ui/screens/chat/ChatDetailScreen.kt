package com.example.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entities.MessageEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    chatViewModel: ChatViewModel,
    onBack: () -> Unit,
    onStartCall: (String, String, String, String) -> Unit
) {
    val chatState by chatViewModel.getChat(chatId).collectAsState(initial = null)
    val messages: List<MessageEntity> by chatViewModel.getChatMessages(chatId).collectAsState(initial = emptyList())
    val allChats by chatViewModel.chats.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showEncryptionInfo by remember { mutableStateOf(false) }
    var showInAppCamera by remember { mutableStateOf(false) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var forwardingMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var showDisappearingDialog by remember { mutableStateOf(false) }
    var disappearingSetting by remember { mutableStateOf("Off") }
    var menuExpanded by remember { mutableStateOf(false) }
    var showUserProfileDialog by remember { mutableStateOf(false) }
    var replyToMessage by remember { mutableStateOf<MessageEntity?>(null) }

    val cameraRequester = com.example.util.rememberPermissionRequester(com.example.util.AppPermission.CAMERA) {
        showInAppCamera = true
    }
    val micRequester = com.example.util.rememberPermissionRequester(com.example.util.AppPermission.MICROPHONE) {
        isRecordingVoice = true
    }

    LaunchedEffect(chatId) {
        chatViewModel.markChatRead(chatId)
        chatViewModel.logProfileVisit(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showUserProfileDialog = true }
                            .padding(vertical = 4.dp)
                    ) {
                        if (chatState?.avatarUrl?.isNotEmpty() == true) {
                            AsyncImage(
                                model = chatState?.avatarUrl,
                                contentDescription = chatState?.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (chatState?.isGroup == true) Icons.Default.Groups else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = chatState?.name ?: "Chat",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (chatState?.isGroup == true) {
                                    chatState?.presence ?: "Tap for group info"
                                } else {
                                    chatState?.presence ?: "Online"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (chatState?.presence == "Online" || chatState?.presence == "Typing..." || chatState?.presence == "AI Reply") WhatsAppEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val chat = chatState ?: return@IconButton
                            onStartCall(chat.id, chat.name, chat.avatarUrl, "VIDEO")
                        },
                        modifier = Modifier.testTag("chat_video_call_btn")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call")
                    }

                    IconButton(
                        onClick = {
                            val chat = chatState ?: return@IconButton
                            onStartCall(chat.id, chat.name, chat.avatarUrl, "VOICE")
                        },
                        modifier = Modifier.testTag("chat_voice_call_btn")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Voice Call")
                    }

                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Encryption Info") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    showEncryptionInfo = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Search messages") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                onClick = { menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Disappearing messages ($disappearingSetting)") },
                                leadingIcon = { Icon(Icons.Default.Timelapse, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    showDisappearingDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mute notifications") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.VolumeOff, contentDescription = null) },
                                onClick = { menuExpanded = false }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
        ) {
            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("messages_lazy_column"),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                // Centered E2EE banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shadowElevation = 1.dp,
                            modifier = Modifier.clickable { showEncryptionInfo = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = WhatsAppEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Messages and calls are end-to-end encrypted. Tap to verify.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == "me"
                    MessageBubble(
                        message = msg,
                        isMe = isMe,
                        onReaction = { emoji ->
                            chatViewModel.reactToMessage(msg.id, emoji)
                        },
                        onDeleteForMe = {
                            chatViewModel.deleteMessageForMe(msg.id)
                        },
                        onDeleteForEveryone = {
                            chatViewModel.deleteMessageForEveryone(msg.id)
                        },
                        onReply = { target ->
                            replyToMessage = target
                        },
                        onForward = { target ->
                            forwardingMessage = target
                        }
                    )
                }
            }

            // Reply banner preview
            replyToMessage?.let { targetMsg ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(32.dp)
                                .background(WhatsAppEmerald, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Replying to ${if (targetMsg.senderId == "me") "You" else targetMsg.senderName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = WhatsAppEmerald
                            )
                            Text(
                                targetMsg.content.ifEmpty { "Media message" },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        IconButton(onClick = { replyToMessage = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel reply", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            val quickReplies by chatViewModel.customQuickReplies.collectAsState()
            // Smart & Customizable Quick Replies
            if (quickReplies.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = 0,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    divider = {},
                    indicator = {}
                ) {
                    quickReplies.forEach { suggest ->
                        SuggestionChip(
                            onClick = {
                                chatViewModel.sendMessage(
                                    chatId = chatId,
                                    content = suggest,
                                    mediaType = "TEXT"
                                )
                            },
                            label = { Text(suggest, fontSize = 12.sp) },
                            modifier = Modifier
                                .padding(end = 6.dp, top = 2.dp, bottom = 2.dp)
                                .testTag("suggest_chip_$suggest")
                        )
                    }
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                if (isRecordingVoice) {
                    LiveVoiceRecordingBar(
                        onCancel = { isRecordingVoice = false },
                        onSend = { sec ->
                            chatViewModel.sendMessage(
                                chatId = chatId,
                                content = "",
                                mediaType = "AUDIO",
                                mediaDuration = sec
                            )
                            isRecordingVoice = false
                        }
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Chat Input Container
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { /* Emoji picker toggle */ }) {
                                    Icon(
                                        Icons.Default.EmojiEmotions,
                                        contentDescription = "Emojis",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                TextField(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    placeholder = { Text("Message", fontSize = 15.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("chat_message_input"),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    maxLines = 4
                                )

                                IconButton(
                                    onClick = { showAttachmentSheet = true },
                                    modifier = Modifier.testTag("chat_attach_btn")
                                ) {
                                    Icon(
                                        Icons.Default.AttachFile,
                                        contentDescription = "Attach file",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (inputText.isBlank()) {
                                    IconButton(
                                        onClick = { cameraRequester.request() },
                                        modifier = Modifier.testTag("chat_camera_btn")
                                    ) {
                                        Icon(
                                            Icons.Default.CameraAlt,
                                            contentDescription = "Camera",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Voice Note Mic or Send Button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(WhatsAppEmerald)
                                .clickable {
                                    if (inputText.isNotBlank()) {
                                        chatViewModel.sendMessage(
                                            chatId = chatId,
                                            content = inputText.trim(),
                                            mediaType = "TEXT",
                                            replyToMessageId = replyToMessage?.id ?: "",
                                            replyToText = replyToMessage?.content ?: ""
                                        )
                                        inputText = ""
                                        replyToMessage = null
                                    } else {
                                        micRequester.request()
                                    }
                                }
                                .testTag("chat_send_or_mic_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (inputText.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
                                contentDescription = if (inputText.isNotBlank()) "Send" else "Record voice message",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showInAppCamera) {
        InAppCameraDialog(
            onDismiss = { showInAppCamera = false },
            onPhotoCaptured = { photoUrl, caption ->
                chatViewModel.sendMessage(
                    chatId = chatId,
                    content = caption.ifEmpty { "Camera snapshot 📷" },
                    mediaType = "IMAGE",
                    mediaUrl = photoUrl
                )
            }
        )
    }

    forwardingMessage?.let { msg ->
        ForwardMessageDialog(
            message = msg,
            chats = allChats.filter { it.id != chatId },
            onDismiss = { forwardingMessage = null },
            onForwardToChats = { targets ->
                chatViewModel.forwardMessage(msg, targets)
                forwardingMessage = null
            }
        )
    }

    if (showDisappearingDialog) {
        AlertDialog(
            onDismissRequest = { showDisappearingDialog = false },
            title = { Text("Disappearing Messages", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("For more privacy, new messages will disappear from this chat after the selected duration.")
                    Spacer(modifier = Modifier.height(12.dp))
                    listOf("24 hours", "7 days", "90 days", "Off").forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    disappearingSetting = option
                                    showDisappearingDialog = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = disappearingSetting == option,
                                onClick = {
                                    disappearingSetting = option
                                    showDisappearingDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = WhatsAppEmerald)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDisappearingDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    if (showUserProfileDialog) {
        val fetchedProfile by chatViewModel.firestoreRepository.observeUserProfile(chatId).collectAsState(initial = null)
        AlertDialog(
            onDismissRequest = { showUserProfileDialog = false },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(WhatsAppEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (fetchedProfile?.photoUrl?.isNotEmpty() == true || chatState?.avatarUrl?.isNotEmpty() == true) {
                            AsyncImage(
                                model = fetchedProfile?.photoUrl?.ifEmpty { chatState?.avatarUrl } ?: chatState?.avatarUrl,
                                contentDescription = chatState?.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = WhatsAppEmerald, modifier = Modifier.size(48.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            fetchedProfile?.displayName?.ifEmpty { chatState?.name } ?: chatState?.name ?: "User Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (fetchedProfile?.isVerified == true) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF1DA1F2), modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        fetchedProfile?.phoneNumber?.ifEmpty { chatState?.phone } ?: chatState?.phone ?: "",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("About / Status Bio:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WhatsAppEmerald)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                fetchedProfile?.bio?.ifEmpty { "Available ⚡ Using Chat Box Pro" } ?: "Available ⚡ Using Chat Box Pro",
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconButton(onClick = {
                            showUserProfileDialog = false
                            onStartCall(chatId, chatState?.name ?: "Contact", chatState?.avatarUrl ?: "", "VOICE")
                        }) {
                            Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = WhatsAppEmerald)
                        }
                        IconButton(onClick = {
                            showUserProfileDialog = false
                            onStartCall(chatId, chatState?.name ?: "Contact", chatState?.avatarUrl ?: "", "VIDEO")
                        }) {
                            Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = WhatsAppEmerald)
                        }
                        IconButton(onClick = {
                            showUserProfileDialog = false
                            showEncryptionInfo = true
                        }) {
                            Icon(Icons.Default.Lock, contentDescription = "E2EE Security", tint = WhatsAppEmerald)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUserProfileDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showAttachmentSheet) {
        MediaAttachmentSheet(
            onDismiss = { showAttachmentSheet = false },
            onAttachPhoto = {
                chatViewModel.sendMessage(
                    chatId = chatId,
                    content = "Shared from gallery 🖼️",
                    mediaType = "IMAGE",
                    mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600"
                )
            },
            onAttachDocument = {
                chatViewModel.sendMessage(
                    chatId = chatId,
                    content = "Project_Requirements_v2.pdf",
                    mediaType = "DOCUMENT",
                    mediaFileSize = "2.4 MB"
                )
            },
            onAttachAudio = {
                chatViewModel.sendMessage(
                    chatId = chatId,
                    content = "",
                    mediaType = "AUDIO",
                    mediaDuration = 24
                )
            },
            onAttachLocation = {
                chatViewModel.sendMessage(
                    chatId = chatId,
                    content = "📍 Live Location: San Francisco, CA",
                    mediaType = "LOCATION"
                )
            },
            onAttachContact = {
                chatViewModel.sendMessage(
                    chatId = chatId,
                    content = "👤 Contact Card: David Chen (+1 555-456-7890)",
                    mediaType = "CONTACT"
                )
            },
            onAttachPoll = {
                chatViewModel.sendMessage(
                    chatId = chatId,
                    content = "📊 Poll: Which feature should we launch first?\n1. AI Image Generator\n2. Voice Note Transcribe",
                    mediaType = "POLL"
                )
            },
            onAttachAiImages = {
                chatViewModel.sendMessage(
                    chatId = chatId,
                    content = "✨ Generated AI Avatar 🖼️",
                    mediaType = "IMAGE",
                    mediaUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600"
                )
            }
        )
    }

    if (showEncryptionInfo) {
        EncryptionInfoDialog(
            contactName = chatState?.name ?: "Contact",
            publicKey = chatState?.publicKey ?: "7B9E-43F1-AA02-98C3-B471",
            onDismiss = { showEncryptionInfo = false }
        )
    }

    if (cameraRequester.showRationale()) {
        com.example.util.PermissionRationaleDialog(
            permission = com.example.util.AppPermission.CAMERA,
            onDismiss = { cameraRequester.dismissRationale() },
            onOpenSettings = { com.example.util.PermissionManager.openAppSettings(context) }
        )
    }

    if (micRequester.showRationale()) {
        com.example.util.PermissionRationaleDialog(
            permission = com.example.util.AppPermission.MICROPHONE,
            onDismiss = { micRequester.dismissRationale() },
            onOpenSettings = { com.example.util.PermissionManager.openAppSettings(context) }
        )
    }
}
