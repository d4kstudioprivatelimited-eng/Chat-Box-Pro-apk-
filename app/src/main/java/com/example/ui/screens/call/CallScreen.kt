package com.example.ui.screens.call

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.CallGreen
import com.example.ui.theme.CallRed
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.viewmodel.ActiveCallState
import com.example.ui.viewmodel.ChatViewModel

@Composable
fun CallScreen(
    callState: ActiveCallState,
    chatViewModel: ChatViewModel
) {
    val durationFormatted = remember(callState.durationSeconds) {
        val minutes = callState.durationSeconds / 60
        val seconds = callState.durationSeconds % 60
        "%02d:%02d".format(minutes, seconds)
    }

    // 1. Picture-in-Picture (PiP) Overlay Mode
    if (callState.isPiPMode) {
        PiPOverlay(callState = callState, durationFormatted = durationFormatted, chatViewModel = chatViewModel)
        return
    }

    // 2. Incoming Call Screen Mode
    if (callState.direction == "INCOMING" && callState.status == "Incoming...") {
        IncomingCallView(callState = callState, chatViewModel = chatViewModel)
        return
    }

    // 3. Outgoing Call Screen Mode (Ringing / Calling)
    if (callState.direction == "OUTGOING" && (callState.status == "Calling..." || callState.status == "Ringing...")) {
        OutgoingCallView(callState = callState, chatViewModel = chatViewModel)
        return
    }

    // 4. Connected P2P Call Screen Mode (Voice / Video)
    ConnectedCallView(callState = callState, durationFormatted = durationFormatted, chatViewModel = chatViewModel)
}

/**
 * Incoming P2P Video/Voice Call Screen
 */
@Composable
private fun IncomingCallView(
    callState: ActiveCallState,
    chatViewModel: ChatViewModel
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_incoming")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "incoming_ring"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF071B19), Color(0xFF0F2D2A), Color(0xFF0B1426))
                )
            )
            .testTag("incoming_call_screen")
    ) {
        // Top Security & Call Type Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = WhatsAppEmerald.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppEmerald.copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = WhatsAppEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "P2P WebRTC E2EE Encrypted",
                        color = WhatsAppEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = callState.contactName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (callState.callType == "VIDEO") "Incoming P2P Video Call..." else "Incoming P2P Voice Call...",
                fontSize = 15.sp,
                color = WhatsAppEmerald,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Pulsing Caller Avatar
            Box(
                modifier = Modifier
                    .scale(ringScale)
                    .size(170.dp)
                    .clip(CircleShape)
                    .border(3.5.dp, WhatsAppEmerald, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (callState.contactAvatar.isNotEmpty()) {
                    AsyncImage(
                        model = callState.contactAvatar,
                        contentDescription = callState.contactName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF005C4B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(72.dp))
                    }
                }
            }
        }

        // Bottom Accept / Decline Buttons Bar
        Surface(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color(0xFF1F2C34).copy(alpha = 0.96f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decline Call Button (Red)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { chatViewModel.declineCall() },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(CallRed)
                                .testTag("incoming_decline_btn")
                        ) {
                            Icon(
                                Icons.Default.CallEnd,
                                contentDescription = "Decline Call",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Decline", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    // Accept Call Button (Green)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { chatViewModel.acceptCall() },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(CallGreen)
                                .testTag("incoming_accept_btn")
                        ) {
                            Icon(
                                if (callState.callType == "VIDEO") Icons.Default.Videocam else Icons.Default.Call,
                                contentDescription = "Accept Call",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Accept", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Outgoing P2P Video/Voice Call Screen (Calling / Ringing)
 */
@Composable
private fun OutgoingCallView(
    callState: ActiveCallState,
    chatViewModel: ChatViewModel
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_outgoing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "outgoing_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F1E1C), Color(0xFF152A26), Color(0xFF0B1426))
                )
            )
            .testTag("outgoing_call_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = WhatsAppEmerald.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "🔒 End-to-End Encrypted",
                    color = WhatsAppEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = callState.contactName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${callState.status} (${if (callState.callType == "VIDEO") "P2P Video" else "P2P Voice"})",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Pulsing Avatar
            Box(
                modifier = Modifier
                    .scale(pulseScale)
                    .size(160.dp)
                    .clip(CircleShape)
                    .border(3.dp, WhatsAppEmerald, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (callState.contactAvatar.isNotEmpty()) {
                    AsyncImage(
                        model = callState.contactAvatar,
                        contentDescription = callState.contactName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF005C4B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(72.dp))
                    }
                }
            }
        }

        // Bottom Controls (Mute, Camera Toggle, Speaker, End)
        Surface(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color(0xFF1F2C34).copy(alpha = 0.95f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speaker Button
                IconButton(
                    onClick = { chatViewModel.toggleSpeaker() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (callState.isSpeaker) Color.White else Color.White.copy(alpha = 0.15f))
                        .testTag("outgoing_speaker_btn")
                ) {
                    Icon(
                        imageVector = if (callState.isSpeaker) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeDown,
                        contentDescription = "Speaker",
                        tint = if (callState.isSpeaker) Color.Black else Color.White
                    )
                }

                // Camera Toggle / Video toggle (if Video call)
                if (callState.callType == "VIDEO") {
                    IconButton(
                        onClick = { chatViewModel.toggleVideoDisabled() },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (callState.isVideoDisabled) CallRed else Color.White.copy(alpha = 0.15f))
                            .testTag("outgoing_camera_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (callState.isVideoDisabled) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            contentDescription = "Camera Toggle",
                            tint = Color.White
                        )
                    }
                }

                // Mute Mic Button
                IconButton(
                    onClick = { chatViewModel.toggleMute() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (callState.isMuted) CallRed else Color.White.copy(alpha = 0.15f))
                        .testTag("outgoing_mute_btn")
                ) {
                    Icon(
                        imageVector = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = Color.White
                    )
                }

                // End Call Red Button
                IconButton(
                    onClick = { chatViewModel.endCall() },
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(CallRed)
                        .testTag("outgoing_end_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

/**
 * Connected P2P Video/Voice Call Screen
 */
@Composable
private fun ConnectedCallView(
    callState: ActiveCallState,
    durationFormatted: String,
    chatViewModel: ChatViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1E1C))
            .testTag("active_call_screen")
    ) {
        // Video Call Feed or Group Grid
        if (callState.callType == "VIDEO" && !callState.isVideoDisabled) {
            AsyncImage(
                model = callState.contactAvatar.ifEmpty { "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=800" },
                contentDescription = "Video Feed",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Local Camera Preview (Self View)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 16.dp, end = 16.dp)
                    .size(width = 100.dp, height = 140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E2A30)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300",
                    contentDescription = "Self View",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    "You",
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                )
            }
        }

        // Top Toolbar: Minimize to PiP & Lock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { chatViewModel.togglePiPMode() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .testTag("call_pip_btn")
            ) {
                Icon(
                    Icons.Default.PictureInPictureAlt,
                    contentDescription = "Picture in Picture",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = WhatsAppEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "P2P WebRTC E2EE",
                    fontSize = 12.sp,
                    color = WhatsAppEmerald,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.size(40.dp))
        }

        // Header: Contact Info & Status
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = callState.contactName,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = durationFormatted,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold
            )

            // Voice call large avatar
            if (callState.callType == "VOICE" || callState.isVideoDisabled) {
                Spacer(modifier = Modifier.height(60.dp))

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(3.dp, WhatsAppEmerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (callState.contactAvatar.isNotEmpty()) {
                        AsyncImage(
                            model = callState.contactAvatar,
                            contentDescription = callState.contactName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF005C4B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(72.dp))
                        }
                    }
                }
            }
        }

        // Bottom Call Controls Bar
        Surface(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color(0xFF1F2C34).copy(alpha = 0.95f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speaker Button
                IconButton(
                    onClick = { chatViewModel.toggleSpeaker() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (callState.isSpeaker) Color.White else Color.White.copy(alpha = 0.15f))
                        .testTag("call_speaker_btn")
                ) {
                    Icon(
                        imageVector = if (callState.isSpeaker) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeDown,
                        contentDescription = "Speaker",
                        tint = if (callState.isSpeaker) Color.Black else Color.White
                    )
                }

                // Camera Toggle / Video toggle (for video calls)
                if (callState.callType == "VIDEO") {
                    IconButton(
                        onClick = { chatViewModel.flipCamera() },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("call_flip_camera_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Flip Camera",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { chatViewModel.toggleVideoDisabled() },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (callState.isVideoDisabled) CallRed else Color.White.copy(alpha = 0.15f))
                            .testTag("call_video_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (callState.isVideoDisabled) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            contentDescription = "Toggle Video",
                            tint = Color.White
                        )
                    }
                }

                // Mute Mic Button
                IconButton(
                    onClick = { chatViewModel.toggleMute() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (callState.isMuted) CallRed else Color.White.copy(alpha = 0.15f))
                        .testTag("call_mute_btn")
                ) {
                    Icon(
                        imageVector = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = Color.White
                    )
                }

                // End Call Red Button
                IconButton(
                    onClick = { chatViewModel.endCall() },
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(CallRed)
                        .testTag("call_end_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

/**
 * Floating Picture-in-Picture Call Overlay
 */
@Composable
private fun PiPOverlay(
    callState: ActiveCallState,
    durationFormatted: String,
    chatViewModel: ChatViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2C34)),
            modifier = Modifier
                .size(width = 170.dp, height = 230.dp)
                .clickable { chatViewModel.togglePiPMode() }
                .border(1.5.dp, WhatsAppEmerald, RoundedCornerShape(16.dp))
                .testTag("pip_floating_card")
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (callState.callType == "VIDEO" && !callState.isVideoDisabled) {
                    AsyncImage(
                        model = callState.contactAvatar.ifEmpty { "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400" },
                        contentDescription = "PiP Video",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0F1E1C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                    }
                }

                // Top expand & timer bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (callState.status == "Connected") durationFormatted else callState.status,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { chatViewModel.togglePiPMode() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Fullscreen, contentDescription = "Expand", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                // Bottom mini end button
                IconButton(
                    onClick = { chatViewModel.endCall() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CallRed)
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
