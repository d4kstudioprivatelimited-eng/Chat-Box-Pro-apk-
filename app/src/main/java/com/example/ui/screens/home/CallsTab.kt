package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.data.local.entities.CallEntity
import com.example.ui.theme.CallGreen
import com.example.ui.theme.CallRed
import com.example.ui.theme.WhatsAppEmerald
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CallsTab(
    calls: List<CallEntity>,
    onStartCall: (String, String, String, String) -> Unit,
    onNewCallClick: () -> Unit
) {
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("calls_list_column"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Create Call Link Item
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNewCallClick() }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("create_call_link_row"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(WhatsAppEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Link,
                        contentDescription = "Create call link",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Create a call link",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Share a link for your WebRTC call",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Recent",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (calls.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "To start calling contacts who have Chat Box Pro, tap the call icon at the bottom.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        items(calls, key = { it.id }) { call ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("call_row_${call.id}"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (call.contactAvatar.isNotEmpty()) {
                    AsyncImage(
                        model = call.contactAvatar,
                        contentDescription = call.contactName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = call.contactName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (call.direction == "MISSED") CallRed else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val (icon, tint) = when (call.direction) {
                            "MISSED" -> Pair(Icons.AutoMirrored.Filled.CallMissed, CallRed)
                            "INCOMING" -> Pair(Icons.AutoMirrored.Filled.CallReceived, CallGreen)
                            else -> Pair(Icons.AutoMirrored.Filled.CallMade, CallGreen)
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = call.direction,
                            tint = tint,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sdf.format(Date(call.timestamp)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onStartCall(
                            call.contactId,
                            call.contactName,
                            call.contactAvatar,
                            call.callType
                        )
                    },
                    modifier = Modifier.testTag("call_action_${call.id}")
                ) {
                    Icon(
                        imageVector = if (call.callType == "VIDEO") Icons.Default.Videocam else Icons.Default.Call,
                        contentDescription = "Call ${call.contactName}",
                        tint = WhatsAppEmerald
                    )
                }
            }
        }
    }
}
