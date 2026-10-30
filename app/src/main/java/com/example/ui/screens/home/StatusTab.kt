package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.data.local.entities.StoryEntity
import com.example.ui.theme.StoryRingBorder
import com.example.ui.theme.WhatsAppEmerald
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatusTab(
    stories: List<StoryEntity>,
    onStoryClick: (List<StoryEntity>, Int) -> Unit,
    onAddStatusClick: () -> Unit,
    onStatusPrivacyClick: () -> Unit = {}
) {
    val myStory = stories.firstOrNull { it.authorId == "me" }
    val contactStories = stories.filter { it.authorId != "me" }
    val recentStories = contactStories.filter { !it.isViewed }
    val viewedStories = contactStories.filter { it.isViewed }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("status_list_column"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // My Status Item
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (myStory != null) {
                            onStoryClick(listOf(myStory), 0)
                        } else {
                            onAddStatusClick()
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("my_status_row"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (myStory != null) 2.5.dp else 0.dp,
                                color = if (myStory != null) StoryRingBorder else Color.Transparent,
                                shape = CircleShape
                            )
                            .padding(if (myStory != null) 3.dp else 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
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

                    if (myStory == null) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(WhatsAppEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add status",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "My status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (myStory != null) "Tap to view status updates" else "Tap to add status update",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onStatusPrivacyClick,
                    modifier = Modifier.testTag("status_privacy_btn")
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Status Privacy",
                        tint = WhatsAppEmerald
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                thickness = 0.5.dp
            )
        }

        // Section: Recent Updates
        if (recentStories.isNotEmpty()) {
            item {
                Text(
                    text = "Recent updates",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(recentStories, key = { it.id }) { story ->
                StoryItemRow(
                    story = story,
                    isViewed = false,
                    onClick = {
                        val index = stories.indexOf(story)
                        onStoryClick(stories, if (index >= 0) index else 0)
                    }
                )
            }
        }

        // Section: Viewed Updates
        if (viewedStories.isNotEmpty()) {
            item {
                Text(
                    text = "Viewed updates",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(viewedStories, key = { it.id }) { story ->
                StoryItemRow(
                    story = story,
                    isViewed = true,
                    onClick = {
                        val index = stories.indexOf(story)
                        onStoryClick(stories, if (index >= 0) index else 0)
                    }
                )
            }
        }

        // Privacy Footer
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Your status updates are end-to-end encrypted",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StoryItemRow(
    story: StoryEntity,
    isViewed: Boolean,
    onClick: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val formattedTime = remember(story.timestamp) { sdf.format(Date(story.timestamp)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("story_row_${story.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Story ring circle
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .border(
                    width = 2.5.dp,
                    color = if (isViewed) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else StoryRingBorder,
                    shape = CircleShape
                )
                .padding(3.dp)
        ) {
            if (story.authorAvatar.isNotEmpty()) {
                AsyncImage(
                    model = story.authorAvatar,
                    contentDescription = story.authorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(story.backgroundColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        story.textCaption.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = story.authorName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Today, $formattedTime",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
