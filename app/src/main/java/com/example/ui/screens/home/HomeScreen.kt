package com.example.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreenDark
import com.example.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    chatViewModel: ChatViewModel,
    onNavigateToChat: (String) -> Unit,
    onNavigateToNewChat: () -> Unit,
    onNavigateToNewGroup: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCreateStatus: () -> Unit,
    onNavigateToContactSync: () -> Unit,
    onNavigateToE2eeInfo: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isSearchActive by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    val chats by chatViewModel.chats.collectAsState()
    val stories by chatViewModel.stories.collectAsState()
    val calls by chatViewModel.calls.collectAsState()
    val searchQuery by chatViewModel.searchQuery.collectAsState()

    val totalUnread = remember(chats) {
        chats.sumOf { it.unreadCount }
    }

    val focusManager = LocalFocusManager.current
    val strings = com.example.ui.localization.LocalAppStrings.current

    var showStatusPrivacyDialog by remember { mutableStateOf(false) }
    var showWhatsAppWebDialog by remember { mutableStateOf(false) }
    val currentPrivacyOption by chatViewModel.statusPrivacyOption.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (isSearchActive) {
                    TopAppBar(
                        title = {
                            TextField(
                                value = searchQuery,
                                onValueChange = { chatViewModel.setSearchQuery(it) },
                                placeholder = { Text("Search chats and messages...") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("home_search_input")
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                isSearchActive = false
                                chatViewModel.setSearchQuery("")
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close search")
                            }
                        },
                        actions = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { chatViewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                } else {
                    TopAppBar(
                        title = {
                            Text(
                                "Chat Box Pro",
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp,
                                color = WhatsAppEmerald
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = onNavigateToContactSync,
                                modifier = Modifier.testTag("contact_sync_icon_btn")
                            ) {
                                Icon(
                                    Icons.Default.PersonSearch,
                                    contentDescription = "Sync Contacts",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = { isSearchActive = true },
                                modifier = Modifier.testTag("search_icon_btn")
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Box {
                                IconButton(
                                    onClick = { menuExpanded = true },
                                    modifier = Modifier.testTag("more_menu_btn")
                                ) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "More Options",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("New group") },
                                        leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null) },
                                        onClick = {
                                            menuExpanded = false
                                            onNavigateToNewGroup()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("New broadcast") },
                                        leadingIcon = { Icon(Icons.Default.Podcasts, contentDescription = null) },
                                        onClick = {
                                            menuExpanded = false
                                            onNavigateToNewChat()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Sync contacts") },
                                        leadingIcon = { Icon(Icons.Default.Sync, contentDescription = null) },
                                        onClick = {
                                            menuExpanded = false
                                            onNavigateToContactSync()
                                        }
                                    )
                                     DropdownMenuItem(
                                        text = { Text("Linked devices (WhatsApp Web)") },
                                        leadingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = WhatsAppEmerald) },
                                        onClick = {
                                            menuExpanded = false
                                            showWhatsAppWebDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Security & E2EE") },
                                        leadingIcon = { Icon(Icons.Default.Security, contentDescription = null) },
                                        onClick = {
                                            menuExpanded = false
                                            onNavigateToE2eeInfo()
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Settings") },
                                        leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                        onClick = {
                                            menuExpanded = false
                                            onNavigateToSettings()
                                        }
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                // WhatsApp Top Tabs (Chats, Updates/Status, Calls)
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = WhatsAppEmerald,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = WhatsAppEmerald,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        modifier = Modifier.testTag("tab_chats"),
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    strings.chats,
                                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == 0) WhatsAppEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (totalUnread > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(WhatsAppEmerald),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = totalUnread.toString(),
                                            color = Color.Black,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    )

                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        modifier = Modifier.testTag("tab_updates"),
                        text = {
                            Text(
                                strings.status,
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == 1) WhatsAppEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )

                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        modifier = Modifier.testTag("tab_calls"),
                        text = {
                            Text(
                                strings.calls,
                                fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == 2) WhatsAppEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("whatsapp_bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (totalUnread > 0) {
                                    Badge(containerColor = WhatsAppEmerald, contentColor = Color.Black) {
                                        Text(totalUnread.toString(), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                if (selectedTabIndex == 0) Icons.AutoMirrored.Filled.Chat else Icons.Outlined.ChatBubbleOutline,
                                contentDescription = strings.chats
                            )
                        }
                    },
                    label = { Text(strings.chats, fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppEmerald,
                        selectedTextColor = WhatsAppEmerald,
                        indicatorColor = WhatsAppEmerald.copy(alpha = 0.18f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = {
                        Icon(
                            if (selectedTabIndex == 1) Icons.Default.DonutLarge else Icons.Outlined.DonutLarge,
                            contentDescription = strings.status
                        )
                    },
                    label = { Text(strings.status, fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppEmerald,
                        selectedTextColor = WhatsAppEmerald,
                        indicatorColor = WhatsAppEmerald.copy(alpha = 0.18f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    icon = {
                        Icon(
                            if (selectedTabIndex == 2) Icons.Default.Call else Icons.Outlined.Call,
                            contentDescription = strings.calls
                        )
                    },
                    label = { Text(strings.calls, fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppEmerald,
                        selectedTextColor = WhatsAppEmerald,
                        indicatorColor = WhatsAppEmerald.copy(alpha = 0.18f)
                    )
                )
            }
        },
        floatingActionButton = {
            when (selectedTabIndex) {
                0 -> {
                    FloatingActionButton(
                        onClick = onNavigateToNewChat,
                        containerColor = WhatsAppGreenDark,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("new_chat_fab")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "New chat")
                    }
                }
                1 -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        SmallFloatingActionButton(
                            onClick = onNavigateToCreateStatus,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("edit_status_fab")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Write text status", modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        FloatingActionButton(
                            onClick = onNavigateToCreateStatus,
                            containerColor = WhatsAppGreenDark,
                            contentColor = Color.White,
                            modifier = Modifier.testTag("camera_status_fab")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Take photo status")
                        }
                    }
                }
                2 -> {
                    FloatingActionButton(
                        onClick = onNavigateToNewChat,
                        containerColor = WhatsAppGreenDark,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("new_call_fab")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "New Call")
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTabIndex) {
                0 -> ChatListTab(
                    chats = chats,
                    onChatClick = onNavigateToChat,
                    onE2eeBannerClick = onNavigateToE2eeInfo
                )
                1 -> StatusTab(
                    stories = stories,
                    onStoryClick = { list, index ->
                        chatViewModel.openStories(list, index)
                    },
                    onAddStatusClick = onNavigateToCreateStatus,
                    onStatusPrivacyClick = { showStatusPrivacyDialog = true }
                )
                2 -> CallsTab(
                    calls = calls,
                    onStartCall = { id, name, avatar, type ->
                        chatViewModel.startCall(id, name, avatar, type)
                    },
                    onNewCallClick = onNavigateToNewChat
                )
            }
        }
    }

    if (showStatusPrivacyDialog) {
        com.example.ui.screens.status.StatusPrivacyDialog(
            currentOption = currentPrivacyOption,
            onDismiss = { showStatusPrivacyDialog = false },
            onSaveOption = { newOption ->
                chatViewModel.updateStatusPrivacy(newOption)
            }
        )
    }

    if (showWhatsAppWebDialog) {
        com.example.ui.screens.whatsapp.WhatsAppLinkedDeviceDialog(
            onDismiss = { showWhatsAppWebDialog = false },
            onStartWhatsAppChat = { name, phone ->
                chatViewModel.createChat(
                    name = name,
                    phone = phone,
                    avatar = "",
                    onCreated = onNavigateToChat
                )
            }
        )
    }
}
