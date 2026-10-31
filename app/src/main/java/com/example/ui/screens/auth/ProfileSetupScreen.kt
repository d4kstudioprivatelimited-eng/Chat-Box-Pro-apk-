package com.example.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import com.example.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    authViewModel: AuthViewModel,
    onComplete: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    var name by remember { mutableStateOf(state.userName.ifBlank { "Alex Developer" }) }
    var bio by remember { mutableStateOf(state.userBio) }
    var selectedAvatar by remember { mutableStateOf(state.userAvatarUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200" }) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedAvatar = uri.toString()
        }
    }

    val presetAvatars = listOf(
        "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
        "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=200",
        "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200"
    )

    val quickBios = listOf(
        "Available",
        "Busy 🛑",
        "At work 💼",
        "Battery about to die 🪫",
        "In a meeting 🤝",
        "Urgent calls only 📞"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Info", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Please review your info and choose a profile picture",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar Preview
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(100.dp)
            ) {
                if (selectedAvatar.isNotEmpty()) {
                    AsyncImage(
                        model = selectedAvatar,
                        contentDescription = "Avatar Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, WhatsAppEmerald, CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Avatar Placeholder",
                            modifier = Modifier.size(50.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(WhatsAppEmerald)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Preset Avatars & AI Avatar Generation Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Choose or Generate Avatar", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                var isGenerating by remember { mutableStateOf(false) }
                val context = androidx.compose.ui.platform.LocalContext.current
                TextButton(
                    onClick = {
                        isGenerating = true
                        authViewModel.generateAiAvatar("Professional futuristic digital avatar 3d") { generated ->
                            selectedAvatar = generated
                            isGenerating = false
                            android.widget.Toast.makeText(context, "AI Avatar generated!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isGenerating
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = WhatsAppEmerald, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(if (isGenerating) "Generating..." else "✨ Generate AI Avatar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WhatsAppEmerald)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presetAvatars) { avatarUrl ->
                    val isSelected = selectedAvatar == avatarUrl
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { selectedAvatar = avatarUrl }
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) WhatsAppEmerald else Color.Transparent,
                                shape = CircleShape
                            )
                    ) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Preset avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Display Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                placeholder = { Text("Type your name here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("display_name_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = WhatsAppEmerald)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Immutable Verified Email ID Field
            val verifiedEmail = state.email.ifBlank { state.phoneNumber }
            OutlinedTextField(
                value = verifiedEmail,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Verified Email ID (Locked)") },
                supportingText = {
                    Text("Email ID is verified & permanently locked", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verified_email_input_locked"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                trailingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Locked Email", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Lock-on-Save Mobile Number Field
            val isPhoneAlreadySaved = state.isProfileSetupDone && state.phoneNumber.isNotBlank()
            var phoneNumber by remember { mutableStateOf(state.phoneNumber) }
            val cleanDigits = remember(phoneNumber) { phoneNumber.trim().replace(Regex("[^0-9+]"), "") }
            val isPhoneValid = if (isPhoneAlreadySaved) true else cleanDigits.length >= 10
            var showError by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = if (isPhoneAlreadySaved) state.phoneNumber else phoneNumber,
                onValueChange = { 
                    if (!isPhoneAlreadySaved) {
                        phoneNumber = it 
                        if (showError && it.trim().replace(Regex("[^0-9+]"), "").length >= 10) {
                            showError = false
                        }
                    }
                },
                readOnly = isPhoneAlreadySaved,
                enabled = !isPhoneAlreadySaved,
                label = { Text(if (isPhoneAlreadySaved) "Mobile Number (Locked)" else "Mobile Number (Strictly Required)") },
                placeholder = { Text("e.g. +91 9430320021") },
                isError = !isPhoneAlreadySaved && (showError || (phoneNumber.isNotEmpty() && !isPhoneValid)),
                supportingText = {
                    if (isPhoneAlreadySaved) {
                        Text("Mobile Number is saved & permanently locked", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else if (showError || (phoneNumber.isNotEmpty() && !isPhoneValid)) {
                        Text("A valid mobile number (min 10 digits) is strictly required.", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    } else {
                        Text("Required for user search & contact verification • Locked on save", color = WhatsAppEmerald, fontSize = 11.sp)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("phone_number_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = if (isPhoneAlreadySaved) MaterialTheme.colorScheme.onSurfaceVariant else WhatsAppEmerald)
                },
                trailingIcon = {
                    if (isPhoneAlreadySaved) {
                        Icon(Icons.Default.Lock, contentDescription = "Locked Mobile Number", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = if (isPhoneAlreadySaved) OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) else OutlinedTextFieldDefaults.colors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Status Bio Field
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("About") },
                placeholder = { Text("Hey there! I am using Chat Box Pro.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("about_bio_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick bio chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickBios) { chipText ->
                    SuggestionChip(
                        onClick = { bio = chipText },
                        label = { Text(chipText, fontSize = 12.sp) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (!isPhoneAlreadySaved && !isPhoneValid) {
                        showError = true
                        return@Button
                    }
                    authViewModel.saveProfile(
                        name = name.ifEmpty { "Alex Developer" },
                        bio = bio,
                        avatarUrl = selectedAvatar,
                        phone = if (isPhoneAlreadySaved) state.phoneNumber else phoneNumber
                    )
                    onComplete()
                },
                enabled = (isPhoneAlreadySaved || isPhoneValid) && name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_profile_button"),
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenDark),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Finish Setup", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
