package com.example.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.WhatsAppEmerald

@Composable
fun InAppCameraDialog(
    onDismiss: () -> Unit,
    onPhotoCaptured: (String, String) -> Unit
) {
    var isFrontCamera by remember { mutableStateOf(false) }
    var flashMode by remember { mutableStateOf("AUTO") }
    var capturedPhotoUrl by remember { mutableStateOf<String?>(null) }
    var captionText by remember { mutableStateOf("") }

    val samplePhotos = listOf(
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800",
        "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800",
        "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=800"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("in_app_camera_screen")
    ) {
        // Viewfinder or Captured Preview
        if (capturedPhotoUrl != null) {
            AsyncImage(
                model = capturedPhotoUrl,
                contentDescription = "Captured photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Live camera viewfinder preview simulation
            AsyncImage(
                model = if (isFrontCamera)
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=800"
                else
                    samplePhotos[0],
                contentDescription = "Camera viewfinder",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Controls: Close, Flash, Camera grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Row {
                IconButton(
                    onClick = {
                        flashMode = when (flashMode) {
                            "AUTO" -> "ON"
                            "ON" -> "OFF"
                            else -> "AUTO"
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        when (flashMode) {
                            "ON" -> Icons.Default.FlashOn
                            "OFF" -> Icons.Default.FlashOff
                            else -> Icons.Default.FlashAuto
                        },
                        contentDescription = "Flash",
                        tint = if (flashMode == "ON") Color.Yellow else Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = { isFrontCamera = !isFrontCamera },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera", tint = Color.White)
                }
            }
        }

        // Bottom Controls: Shutter / Send Bar
        if (capturedPhotoUrl == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Shutter button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            capturedPhotoUrl = samplePhotos[1]
                        }
                        .testTag("camera_shutter_btn")
                )
            }
        } else {
            // Captured photo review bar
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .imePadding()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { capturedPhotoUrl = null }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retake", tint = Color.White)
                    }

                    TextField(
                        value = captionText,
                        onValueChange = { captionText = it },
                        placeholder = { Text("Add a caption...", color = Color.White.copy(alpha = 0.6f)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FloatingActionButton(
                        onClick = {
                            onPhotoCaptured(capturedPhotoUrl!!, captionText)
                            onDismiss()
                        },
                        containerColor = WhatsAppEmerald,
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("send_captured_photo_fab")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Photo")
                    }
                }
            }
        }
    }
}
