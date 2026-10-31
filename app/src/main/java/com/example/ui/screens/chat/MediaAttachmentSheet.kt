package com.example.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaAttachmentSheet(
    onDismiss: () -> Unit,
    onAttachPhoto: () -> Unit,
    onAttachDocument: () -> Unit,
    onAttachAudio: () -> Unit,
    onAttachLocation: () -> Unit,
    onAttachContact: () -> Unit,
    onAttachPoll: () -> Unit = {},
    onAttachAiImages: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("media_attachment_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AttachmentOption(
                    title = "Gallery",
                    icon = Icons.Default.PhotoLibrary,
                    color = Color(0xFF00A884),
                    onClick = {
                        onAttachPhoto()
                        onDismiss()
                    }
                )
                AttachmentOption(
                    title = "Camera",
                    icon = Icons.Default.CameraAlt,
                    color = Color(0xFFEC407A),
                    onClick = {
                        onAttachPhoto()
                        onDismiss()
                    }
                )
                AttachmentOption(
                    title = "Location",
                    icon = Icons.Default.LocationOn,
                    color = Color(0xFF00C853),
                    onClick = {
                        onAttachLocation()
                        onDismiss()
                    }
                )
                AttachmentOption(
                    title = "Contact",
                    icon = Icons.Default.Person,
                    color = Color(0xFF0288D1),
                    onClick = {
                        onAttachContact()
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AttachmentOption(
                    title = "Document",
                    icon = Icons.Default.Description,
                    color = Color(0xFF7E57C2),
                    onClick = {
                        onAttachDocument()
                        onDismiss()
                    }
                )
                AttachmentOption(
                    title = "Audio",
                    icon = Icons.Default.Headphones,
                    color = Color(0xFFFF7043),
                    onClick = {
                        onAttachAudio()
                        onDismiss()
                    }
                )
                AttachmentOption(
                    title = "Poll",
                    icon = Icons.Default.Poll,
                    color = Color(0xFFFFB300),
                    onClick = {
                        onAttachPoll()
                        onDismiss()
                    }
                )
                AttachmentOption(
                    title = "AI images",
                    icon = Icons.Default.AutoAwesome,
                    color = Color(0xFF29B6F6),
                    onClick = {
                        onAttachAiImages()
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AttachmentOption(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag("attachment_${title.lowercase()}")
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
