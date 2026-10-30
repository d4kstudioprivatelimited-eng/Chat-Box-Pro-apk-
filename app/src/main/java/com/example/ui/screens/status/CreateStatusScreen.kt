package com.example.ui.screens.status

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStatusScreen(
    chatViewModel: ChatViewModel,
    onBack: () -> Unit
) {
    var statusText by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    var statusMode by remember { mutableStateOf("TEXT") } // "TEXT" or "IMAGE"

    val backgroundColors = listOf(
        0xFF008069, // Classic WhatsApp Green
        0xFF075E54, // Deep Teal
        0xFF5B5EA6, // Indigo
        0xFF9C27B0, // Purple
        0xFFD81B60, // Rose
        0xFF005C4B  // OLED Emerald
    )

    val sampleImages = listOf(
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
        "https://images.unsplash.com/photo-1470240731273-7821a6eeb6bd?w=800",
        "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800"
    )
    var selectedImageUrl by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (statusMode == "TEXT") Color(backgroundColors[selectedColorIndex]) else Color(0xFF1E2A30))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("create_status_screen")
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = statusMode == "TEXT",
                    onClick = {
                        statusMode = "TEXT"
                        selectedImageUrl = ""
                    },
                    label = { Text("Text", color = Color.White) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.White.copy(alpha = 0.25f),
                        containerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = statusMode == "IMAGE",
                    onClick = {
                        statusMode = "IMAGE"
                        selectedImageUrl = sampleImages[0]
                    },
                    label = { Text("Photo", color = Color.White) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.White.copy(alpha = 0.25f),
                        containerColor = Color.Transparent
                    )
                )
            }
        }

        // Center Content Input
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp, bottom = 140.dp),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = statusText,
                onValueChange = { statusText = it },
                placeholder = {
                    Text(
                        if (statusMode == "TEXT") "Type a status..." else "Add a caption...",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("status_text_input")
            )
        }

        // Bottom Controls: Color palette & Send FAB
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (statusMode == "TEXT") {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(backgroundColors.indices.toList()) { index ->
                        val color = Color(backgroundColors[index])
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColorIndex == index) 3.dp else 1.dp,
                                    color = if (selectedColorIndex == index) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorIndex = index }
                        )
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(sampleImages) { imgUrl ->
                        val isSelected = selectedImageUrl == imgUrl
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 2.5.dp else 0.dp,
                                    color = if (isSelected) WhatsAppEmerald else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedImageUrl = imgUrl }
                        ) {
                            coil.compose.AsyncImage(
                                model = imgUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Disappears in 24 hours", fontSize = 12.sp, color = Color.White)
                    }
                }

                FloatingActionButton(
                    onClick = {
                        if (statusText.isNotBlank() || selectedImageUrl.isNotBlank()) {
                            chatViewModel.postStory(
                                caption = statusText,
                                imageUrl = selectedImageUrl,
                                bgColor = backgroundColors[selectedColorIndex]
                            )
                            onBack()
                        }
                    },
                    containerColor = WhatsAppEmerald,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("publish_status_fab")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Publish Status")
                }
            }
        }
    }
}
