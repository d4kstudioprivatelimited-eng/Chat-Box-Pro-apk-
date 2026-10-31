package com.example.ui.screens.whatsapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppEmerald
import kotlinx.coroutines.delay
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppLinkedDeviceDialog(
    onDismiss: () -> Unit,
    onStartWhatsAppChat: (name: String, phone: String) -> Unit
) {
    var pairingState by remember { mutableStateOf("QR_READY") } // "QR_READY", "PAIRING", "CONNECTED"
    var qrToken by remember { mutableStateOf(UUID.randomUUID().toString().take(12)) }
    var targetPhone by remember { mutableStateOf("") }
    var linkedAccountPhone by remember { mutableStateOf("+1 (415) 890-3412") }

    LaunchedEffect(pairingState) {
        if (pairingState == "PAIRING") {
            delay(2000)
            pairingState = "CONNECTED"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(WhatsAppEmerald.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = WhatsAppEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Web Companion Protocol (Free)",
                        color = WhatsAppEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Linked Device Web Sync",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Connect natively using secure web companion protocol bridge.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (pairingState) {
                    "QR_READY" -> {
                        // Render QR Code Box
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(2.dp, WhatsAppEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .testTag("whatsapp_qr_code_box"),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val gridCount = 15
                                val cellSize = size.width / gridCount

                                val darkColor = Color.Black
                                val lightColor = Color.White

                                drawRect(lightColor)

                                for (r in 0 until gridCount) {
                                    for (c in 0 until gridCount) {
                                        val isTopLeftFinder = r < 4 && c < 4
                                        val isTopRightFinder = r < 4 && c >= gridCount - 4
                                        val isBottomLeftFinder = r >= gridCount - 4 && c < 4

                                        if (isTopLeftFinder || isTopRightFinder || isBottomLeftFinder) {
                                            val isBoundary = r == 0 || r == 3 || c == 0 || c == 3 ||
                                                    r == gridCount - 1 || r == gridCount - 4 ||
                                                    c == gridCount - 1 || c == gridCount - 4
                                            val isCenterSquare = (r in 1..2 && c in 1..2) ||
                                                    (r in 1..2 && c in (gridCount - 3)..(gridCount - 2)) ||
                                                    (r in (gridCount - 3)..(gridCount - 2) && c in 1..2)

                                            if (isBoundary || isCenterSquare) {
                                                drawRect(
                                                    color = darkColor,
                                                    topLeft = Offset(c * cellSize, r * cellSize),
                                                    size = Size(cellSize, cellSize)
                                                )
                                            }
                                        } else {
                                            val hash = (r * 31 + c * 17 + qrToken.hashCode()).hashCode()
                                            if (hash % 3 == 0) {
                                                drawRect(
                                                    color = darkColor,
                                                    topLeft = Offset(c * cellSize, r * cellSize),
                                                    size = Size(cellSize, cellSize)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Center overlay badge
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Devices, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Scan with Companion App -> Linked Devices",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { qrToken = UUID.randomUUID().toString().take(12) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh QR", tint = WhatsAppEmerald)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { pairingState = "PAIRING" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("simulate_whatsapp_scan_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simulate QR Scan & Pair Session")
                        }
                    }

                    "PAIRING" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 24.dp)
                        ) {
                            CircularProgressIndicator(color = WhatsAppEmerald, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Authenticating Web Session key...", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Connecting via secure protocol bridge...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    "CONNECTED" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(WhatsAppEmerald.copy(alpha = 0.08f))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = WhatsAppEmerald)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Web Protocol Connected", fontWeight = FontWeight.Bold, color = WhatsAppEmerald, fontSize = 14.sp)
                                    Text("Active Account: $linkedAccountPhone", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Start Native Chat with Phone Number:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = targetPhone,
                            onValueChange = { targetPhone = it },
                            placeholder = { Text("e.g. +14155552671") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = WhatsAppEmerald) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("whatsapp_number_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (targetPhone.isNotBlank()) {
                                    onStartWhatsAppChat("Direct Contact", targetPhone.trim())
                                    onDismiss()
                                }
                            },
                            enabled = targetPhone.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_whatsapp_chat_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Native Chat")
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
