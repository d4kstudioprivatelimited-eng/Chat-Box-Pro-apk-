package com.example.ui.screens.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppEmerald
import kotlinx.coroutines.delay

@Composable
fun LiveVoiceRecordingBar(
    onCancel: () -> Unit,
    onSend: (Int) -> Unit
) {
    var recordingSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            recordingSeconds++
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mic_blink")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_voice_recording_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cancel bin
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.15f))
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Cancel recording", tint = Color.Red, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Red blinking dot + timer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = alphaAnim))
                )

                Spacer(modifier = Modifier.width(8.dp))

                val formattedTime = "%02d:%02d".format(recordingSeconds / 60, recordingSeconds % 60)
                Text(
                    text = formattedTime,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Animated fluctuating waveform bars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val barHeights = listOf(14, 28, 8, 22, 34, 12, 26, 18, 30, 10, 24, 16)
                    barHeights.forEachIndexed { index, height ->
                        val barScale = (index % 3 + 1) * 0.3f
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height((height * (0.6f + alphaAnim * barScale)).dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(WhatsAppEmerald)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Send voice note button
            IconButton(
                onClick = { onSend(recordingSeconds.coerceAtLeast(1)) },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(WhatsAppEmerald)
                    .testTag("send_voice_note_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send voice note", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}
