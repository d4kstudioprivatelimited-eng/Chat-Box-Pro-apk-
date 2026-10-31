package com.example.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SahibaAiAutonomousAgentOverlay(
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel,
    onNavigateToChat: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsState()
    val assistantName = authState.aiAssistantName.ifBlank { "Sahiba AI" }

    var agentState by remember { mutableStateOf("LISTENING") } // "LISTENING", "PROCESSING", "EXECUTING", "SPEAKING"
    var userVoiceInput by remember { mutableStateOf("") }
    var aiOutputText by remember { mutableStateOf("Sahiba AI is listening for your command...") }

    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var activeJob by remember { mutableStateOf<Job?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    // Init TextToSpeech
    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initial TTS greeting
            }
        }
        ttsEngine = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    fun speakText(text: String) {
        ttsEngine?.language = Locale.US
        ttsEngine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sahiba_agent_tts")
    }

    fun abortPipeline() {
        activeJob?.cancel()
        activeJob = null
        ttsEngine?.stop()
        onClose()
    }

    fun processAutonomousCommand(command: String) {
        agentState = "PROCESSING"
        aiOutputText = "Analyzing intent with D4K AI SDK..."

        activeJob = coroutineScope.launch {
            val lower = command.lowercase().trim()
            delay(1200)

            when {
                lower.contains("send message") || lower.contains("text") || lower.contains("dictate") -> {
                    agentState = "EXECUTING"
                    val targetName = if (lower.contains("to")) lower.substringAfter("to").trim().split(" ").firstOrNull()?.capitalize(Locale.ROOT) ?: "Contact" else "Rahul"
                    val msgContent = if (lower.contains("saying")) lower.substringAfter("saying").trim() else "Hey $targetName, this message was sent autonomously by $assistantName!"
                    
                    aiOutputText = "Sending message to $targetName: \"$msgContent\""
                    speakText("Sending message to $targetName")

                    val createdChatId = chatViewModel.repository.createNewChat(targetName, "+1 555-0192", "")
                    chatViewModel.sendMessage(createdChatId, msgContent)
                    delay(1500)
                    onNavigateToChat(createdChatId)
                    onClose()
                }

                lower.contains("read unread") || lower.contains("read message") || lower.contains("unread") -> {
                    agentState = "SPEAKING"
                    val textToRead = "You have 1 unread message from David: 'Project requirements updated'."
                    aiOutputText = textToRead
                    speakText(textToRead)
                    delay(4000)
                    onClose()
                }

                lower.contains("call") || lower.contains("video call") || lower.contains("voice call") -> {
                    agentState = "EXECUTING"
                    val isVideo = lower.contains("video")
                    val targetName = if (lower.contains("call")) lower.substringAfter("call").trim().split(" ").firstOrNull()?.capitalize(Locale.ROOT) ?: "Sarah" else "Sarah"
                    val callType = if (isVideo) "VIDEO" else "VOICE"

                    aiOutputText = "Initiating $callType call with $targetName..."
                    speakText("Calling $targetName")
                    delay(1000)
                    chatViewModel.startCall("c_$targetName", targetName, "", callType)
                    onClose()
                }

                lower.contains("settings") -> {
                    agentState = "EXECUTING"
                    aiOutputText = "Opening Settings..."
                    speakText("Opening Settings")
                    delay(800)
                    onNavigateToSettings()
                    onClose()
                }

                else -> {
                    agentState = "SPEAKING"
                    val aiReply = try {
                        com.example.data.ai.D4KAiSdkService.getInstance().generateAutoReply(command)
                    } catch (e: Exception) {
                        "Executing task: $command"
                    }
                    aiOutputText = "$assistantName: $aiReply"
                    speakText(aiReply)
                    delay(3000)
                    onClose()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .testTag("sahiba_agent_overlay"),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row with Abort Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(10.dp),
                            shape = CircleShape,
                            color = WhatsAppEmerald
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "$assistantName Autonomous Agent",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = WhatsAppEmerald
                        )
                    }

                    // INSTANT ABORT / CANCEL BUTTON
                    IconButton(
                        onClick = { abortPipeline() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                            .testTag("sahiba_agent_abort_btn")
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Abort AI Agent",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pulsating Waveform Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    WhatsAppEmerald.copy(alpha = 0.1f),
                                    Color(0xFF00E5FF).copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val midY = size.height / 2
                        val barCount = 20
                        val barWidth = size.width / (barCount * 2)

                        for (i in 0 until barCount) {
                            val amplitude = (Math.sin(Math.toRadians((waveOffset + i * 20).toDouble())) * 25 + 30).toFloat()
                            val startX = i * barWidth * 2 + barWidth
                            drawLine(
                                color = if (i % 2 == 0) WhatsAppEmerald else Color(0xFF00E5FF),
                                start = Offset(startX, midY - amplitude),
                                end = Offset(startX, midY + amplitude),
                                strokeWidth = barWidth * 0.8f
                            )
                        }
                    }

                    Text(
                        text = agentState,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhatsAppEmerald,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Output / Command Text
                Text(
                    text = aiOutputText,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Preset Chips
                Text("Tap or say a command:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SuggestionChip(
                        onClick = {
                            userVoiceInput = "Send message to Rahul saying Let's meet at 5 PM"
                            processAutonomousCommand(userVoiceInput)
                        },
                        label = { Text("💬 Send Msg", fontSize = 11.sp) }
                    )

                    SuggestionChip(
                        onClick = {
                            userVoiceInput = "Read unread messages out loud"
                            processAutonomousCommand(userVoiceInput)
                        },
                        label = { Text("🔊 Read Msgs", fontSize = 11.sp) }
                    )

                    SuggestionChip(
                        onClick = {
                            userVoiceInput = "Call Sarah"
                            processAutonomousCommand(userVoiceInput)
                        },
                        label = { Text("📞 Call", fontSize = 11.sp) }
                    )
                }
            }
        }
    }
}
