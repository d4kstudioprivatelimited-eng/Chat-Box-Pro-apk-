package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatScreen(
    chatViewModel: ChatViewModel,
    onBack: () -> Unit,
    onNavigateToNewGroup: () -> Unit,
    onChatSelected: (String) -> Unit
) {
    val contacts by chatViewModel.contacts.collectAsState()
    var showNewContactDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }

    val registeredContacts = contacts.filter { it.isRegisteredOnChatBox }
    val otherContacts = contacts.filter { !it.isRegisteredOnChatBox }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Select contact", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${contacts.size} contacts", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showNewContactDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Contact")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("select_contact_column")
        ) {
            // New Group Action
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToNewGroup() }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("new_group_action_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(WhatsAppEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("New group", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showNewContactDialog = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("add_contact_action_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(WhatsAppEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("New contact", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    "Contacts on Chat Box Pro",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(registeredContacts, key = { it.id }) { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            chatViewModel.createChat(
                                name = contact.name,
                                phone = contact.phone,
                                avatar = contact.avatar,
                                onCreated = onChatSelected
                            )
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("contact_item_${contact.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (contact.avatar.isNotEmpty()) {
                        AsyncImage(
                            model = contact.avatar,
                            contentDescription = contact.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(WhatsAppEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(contact.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(contact.about, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                }
            }

            if (otherContacts.isNotEmpty()) {
                item {
                    Text(
                        "Invite to Chat Box Pro",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(otherContacts, key = { it.id }) { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(contact.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(contact.phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = { /* Invite link simulation */ },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald)
                        ) {
                            Text("Invite", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showNewContactDialog) {
        AlertDialog(
            onDismissRequest = { showNewContactDialog = false },
            title = { Text("New Contact", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("Contact Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newContactPhone,
                        onValueChange = { newContactPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newContactName.isNotBlank() && newContactPhone.isNotBlank()) {
                            chatViewModel.createChat(
                                name = newContactName,
                                phone = newContactPhone,
                                avatar = "",
                                onCreated = { id ->
                                    showNewContactDialog = false
                                    onChatSelected(id)
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald)
                ) {
                    Text("Save & Chat")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewContactDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
