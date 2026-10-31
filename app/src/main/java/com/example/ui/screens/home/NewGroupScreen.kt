package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.theme.WhatsAppGreenDark
import com.example.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGroupScreen(
    chatViewModel: ChatViewModel,
    onBack: () -> Unit,
    onGroupCreated: (String) -> Unit
) {
    val contacts by chatViewModel.contacts.collectAsState()
    var groupName by remember { mutableStateOf("") }
    var groupDesc by remember { mutableStateOf("") }
    var groupAvatarUrl by remember { mutableStateOf("") }
    var onlyAdminCanPost by remember { mutableStateOf(false) }
    val selectedContactIds = remember { mutableStateListOf<String>() }

    val groupPhotoPicker = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            groupAvatarUrl = uri.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("New group", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = if (selectedContactIds.isEmpty()) "Add participants" else "${selectedContactIds.size} selected",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (groupName.isNotBlank() && selectedContactIds.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        chatViewModel.createGroup(
                            name = groupName,
                            desc = groupDesc,
                            avatarUrl = groupAvatarUrl,
                            members = selectedContactIds.toList(),
                            onCreated = onGroupCreated
                        )
                    },
                    containerColor = WhatsAppGreenDark,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("confirm_create_group_fab")
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Create Group")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("new_group_column"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Group Subject Header
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppEmerald.copy(alpha = 0.2f))
                                    .clickable {
                                        groupPhotoPicker.launch(
                                            androidx.activity.result.PickVisualMediaRequest(
                                                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (groupAvatarUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = groupAvatarUrl,
                                        contentDescription = "Group Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Group Icon", tint = WhatsAppEmerald)
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            OutlinedTextField(
                                value = groupName,
                                onValueChange = { if (it.length <= 25) groupName = it },
                                placeholder = { Text("Group subject") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("group_name_input"),
                                trailingIcon = {
                                    Text(
                                        "${25 - groupName.length}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = groupDesc,
                            onValueChange = { groupDesc = it },
                            placeholder = { Text("Group description (optional)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("group_desc_input"),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Admin Permissions Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Only admins can post", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Restrict messaging permissions to group admins", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = onlyAdminCanPost,
                                onCheckedChange = { onlyAdminCanPost = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppEmerald)
                            )
                        }
                    }
                }
            }

            // Selected participants horizontal chips
            if (selectedContactIds.isNotEmpty()) {
                item {
                    Text(
                        "Participants",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedContactIds) { contactId ->
                            val contact = contacts.firstOrNull { it.id == contactId }
                            InputChip(
                                selected = true,
                                onClick = { selectedContactIds.remove(contactId) },
                                label = { Text(contact?.name ?: contactId, fontSize = 12.sp) },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                                }
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                }
            }

            item {
                Text(
                    "Select Contacts",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(contacts, key = { it.id }) { contact ->
                val isSelected = selectedContactIds.contains(contact.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) {
                                selectedContactIds.remove(contact.id)
                            } else {
                                selectedContactIds.add(contact.id)
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        if (contact.avatar.isNotEmpty()) {
                            AsyncImage(
                                model = contact.avatar,
                                contentDescription = contact.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(contact.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(contact.about, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }

                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            if (checked) selectedContactIds.add(contact.id) else selectedContactIds.remove(contact.id)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = WhatsAppEmerald)
                    )
                }
            }
        }
    }
}
