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
    onAttachContact: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("media_attachment_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                "Share Content",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AttachmentOption(
                    title = "Document",
                    icon = Icons.Default.Description,
                    color = Color(0xFF7F66FF),
                    onClick = {
                        onAttachDocument()
                        onDismiss()
                    }
                )
                AttachmentOption(
                    title = "Camera",
                    icon = Icons.Default.CameraAlt,
                    color = Color(0xFFD3396D),
                    onClick = {
                        onAttachPhoto()
                        onDismiss()
                    }
                )
                AttachmentOption(
                    title = "Gallery",
                    icon = Icons.Default.PhotoLibrary,
                    color = Color(0xFFAC44CF),
                    onClick = {
                        onAttachPhoto()
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
                    title = "Audio",
                    icon = Icons.Default.Headphones,
                    color = Color(0xFFFF8A00),
                    onClick = {
                        onAttachAudio()
                        onDismiss()
                    }
                )
                AttachmentOption(
                    title = "Location",
                    icon = Icons.Default.LocationOn,
                    color = Color(0xFF1FA855),
                    onClick = {
                        onAttachLocation()
                        onDismiss()
                    }
                )
                AttachmentOption(
                    title = "Contact",
                    icon = Icons.Default.Person,
                    color = Color(0xFF009DE2),
                    onClick = {
                        onAttachContact()
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
