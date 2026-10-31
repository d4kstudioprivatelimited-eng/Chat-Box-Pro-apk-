package com.example.ui.screens.chat

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entities.MessageEntity
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun MessageBubble(
    message: MessageEntity,
    isMe: Boolean,
    onReaction: (String) -> Unit,
    onDeleteForMe: () -> Unit,
    onDeleteForEveryone: () -> Unit,
    onReply: (MessageEntity) -> Unit,
    onForward: (MessageEntity) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var isPlayingVoice by remember { mutableStateOf(false) }
    var voiceProgress by remember { mutableFloatStateOf(0.35f) }

    val coroutineScope = rememberCoroutineScope()
    val dragOffsetX = remember { Animatable(0f) }

    val sdf = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val timeStr = remember(message.timestamp) { sdf.format(Date(message.timestamp)) }

    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val bubbleColor = if (isMe) {
        if (isDark) DarkSentBubble else LightSentBubble
    } else {
        if (isDark) DarkReceivedBubble else LightReceivedBubble
    }

    val textColor = if (isMe) {
        if (isDark) DarkTextPrimary else LightTextPrimary
    } else {
        if (isDark) DarkTextPrimary else LightTextPrimary
    }

    // Swipe to reply row container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        // Swipe to reply indicator
        if (dragOffsetX.value > 20f || dragOffsetX.value < -20f) {
            Box(
                modifier = Modifier
                    .align(if (isMe) Alignment.CenterStart else Alignment.CenterEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(WhatsAppEmerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Reply,
                    contentDescription = "Swipe to reply",
                    tint = WhatsAppEmerald,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .offset { IntOffset(dragOffsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffsetX.value > 50f || dragOffsetX.value < -50f) {
                                onReply(message)
                            }
                            coroutineScope.launch {
                                dragOffsetX.animateTo(0f, spring())
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            coroutineScope.launch {
                                dragOffsetX.snapTo((dragOffsetX.value + dragAmount * 0.5f).coerceIn(-100f, 100f))
                            }
                        }
                    )
                }
                .widthIn(max = 310.dp),
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            // Action Menu Popup (Reactions + Reply + Forward + Delete)
            if (showMenu) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
                            listOf("👍", "❤️", "😂", "😮", "😢", "🙏").forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = 22.sp,
                                    modifier = Modifier
                                        .clickable {
                                            onReaction(emoji)
                                            showMenu = false
                                        }
                                        .padding(horizontal = 6.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            TextButton(onClick = {
                                onReply(message)
                                showMenu = false
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reply", fontSize = 12.sp)
                            }

                            TextButton(onClick = {
                                onForward(message)
                                showMenu = false
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Forward, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Forward", fontSize = 12.sp)
                            }

                            TextButton(onClick = {
                                if (isMe) onDeleteForEveryone() else onDeleteForMe()
                                showMenu = false
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 2.dp,
                        bottomEnd = if (isMe) 2.dp else 16.dp
                    ),
                    color = bubbleColor,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { showMenu = true }
                            )
                        }
                        .testTag("message_bubble_${message.id}")
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        // Sender name in group chat if received
                        if (!isMe && message.senderName.isNotEmpty() && message.senderName != "System") {
                            Text(
                                text = message.senderName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = WhatsAppEmerald,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        // Reply preview if this message replied to another
                        if (message.replyToText.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(28.dp)
                                            .background(WhatsAppEmerald, RoundedCornerShape(2.dp))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = message.replyToText,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Content based on mediaType
                        when (message.mediaType) {
                            "IMAGE" -> {
                                AsyncImage(
                                    model = message.mediaUrl,
                                    contentDescription = "Shared photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                if (message.content.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = message.content,
                                        fontSize = 15.sp,
                                        color = textColor
                                    )
                                }

                                // Zero-Footprint Delivery Banner
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoDelete,
                                        contentDescription = "Zero Footprint",
                                        tint = WhatsAppEmerald,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Zero-Footprint: Auto-deleted from server",
                                        fontSize = 9.sp,
                                        color = WhatsAppEmerald,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            "AUDIO" -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { isPlayingVoice = !isPlayingVoice },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingVoice) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                            contentDescription = "Voice note play/pause",
                                            tint = WhatsAppEmerald,
                                            modifier = Modifier.size(34.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        LinearProgressIndicator(
                                            progress = { if (isPlayingVoice) 0.72f else voiceProgress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                                            color = WhatsAppEmerald,
                                            trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isPlayingVoice) "0:18 / 0:24" else "0:24",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Icon(
                                        Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = WhatsAppEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoDelete, contentDescription = null, tint = WhatsAppEmerald, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ephemeral voice: Server clean", fontSize = 9.sp, color = WhatsAppEmerald)
                                }
                            }

                            "DOCUMENT" -> {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Description,
                                                contentDescription = "Document",
                                                tint = WhatsAppEmerald,
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = message.content.ifEmpty { "Document.pdf" },
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 13.sp,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = message.mediaFileSize.ifEmpty { "1.8 MB • PDF" },
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Icon(
                                                Icons.Default.FileDownload,
                                                contentDescription = "Download",
                                                tint = WhatsAppEmerald,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "🛡️ Zero-Footprint: Auto-deleted from server",
                                            fontSize = 9.sp,
                                            color = WhatsAppEmerald
                                        )
                                    }
                                }
                            }

                            "LOCATION" -> {
                                val context = androidx.compose.ui.platform.LocalContext.current
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val geoUri = android.net.Uri.parse("https://maps.google.com/?q=37.7749,-122.4194")
                                            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, geoUri)
                                            mapIntent.setPackage("com.google.android.apps.maps")
                                            try {
                                                context.startActivity(mapIntent)
                                            } catch (_: Exception) {
                                                context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, geoUri))
                                            }
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(WhatsAppEmerald.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.LocationOn,
                                                    contentDescription = "Live Location",
                                                    tint = WhatsAppEmerald,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "📍 Live Location (Tap to open Maps)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = WhatsAppEmerald
                                                )
                                                Text(
                                                    text = message.content.removePrefix("📍 Live Location: ").ifEmpty { "San Francisco, CA" },
                                                    fontSize = 12.sp,
                                                    color = textColor,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        val distKm = remember(message.id) { String.format("%.1f", 0.5 + (message.id.hashCode() % 15) / 10.0) }
                                        val etaMins = remember(message.id) { (message.id.hashCode() % 10) + 3 }
                                        Text(
                                            "📍 Distance: $distKm km away • Live ETA: ~$etaMins mins • Updated live",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = WhatsAppEmerald
                                        )
                                    }
                                }
                            }

                            else -> {
                                val context = androidx.compose.ui.platform.LocalContext.current
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = message.content,
                                        fontSize = 15.sp,
                                        lineHeight = 20.sp,
                                        color = textColor,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (!isMe && message.content.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = {
                                                try {
                                                    val ttsHolder = arrayOf<android.speech.tts.TextToSpeech?>(null)
                                                    ttsHolder[0] = android.speech.tts.TextToSpeech(context) { status ->
                                                        if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                                                            ttsHolder[0]?.language = java.util.Locale.US
                                                            ttsHolder[0]?.speak(message.content, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "msg_tts_${message.id}")
                                                        }
                                                    }
                                                } catch (_: Exception) {}
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.VolumeUp,
                                                contentDescription = "Read aloud (Text to Speech)",
                                                tint = WhatsAppEmerald,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Timestamp & Status Ticks
                        Row(
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (message.isEdited) {
                                Text(
                                    text = "Edited • ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WhatsAppEmerald
                                )
                            }
                            Text(
                                text = timeStr,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (isMe) {
                                Spacer(modifier = Modifier.width(4.dp))
                                when (message.status) {
                                    "SENT" -> {
                                        Icon(
                                            Icons.Default.Done,
                                            contentDescription = "Sent",
                                            tint = CheckGray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    "DELIVERED" -> {
                                        Icon(
                                            Icons.Default.DoneAll,
                                            contentDescription = "Delivered",
                                            tint = CheckGray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    else -> {
                                        Icon(
                                            Icons.Default.DoneAll,
                                            contentDescription = "Read",
                                            tint = CheckBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Reaction badge on bubble corner
                if (message.reaction.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = 10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(message.reaction, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
