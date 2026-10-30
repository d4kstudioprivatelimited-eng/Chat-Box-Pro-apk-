package com.example.ui.screens.status

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppEmerald

enum class StatusPrivacyOption {
    MY_CONTACTS,
    MY_CONTACTS_EXCEPT,
    ONLY_SHARE_WITH
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusPrivacyDialog(
    currentOption: StatusPrivacyOption = StatusPrivacyOption.MY_CONTACTS,
    onDismiss: () -> Unit,
    onSaveOption: (StatusPrivacyOption) -> Unit
) {
    var selectedOption by remember { mutableStateOf(currentOption) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("status_privacy_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                "Status Privacy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Who can see my status updates? Changes to privacy settings won't affect status updates that you've sent already.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Option 1: My contacts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedOption == StatusPrivacyOption.MY_CONTACTS,
                    onClick = { selectedOption = StatusPrivacyOption.MY_CONTACTS },
                    colors = RadioButtonDefaults.colors(selectedColor = WhatsAppEmerald)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("My contacts", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("All your saved contacts on Chat Box Pro", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Option 2: My contacts except...
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedOption == StatusPrivacyOption.MY_CONTACTS_EXCEPT,
                    onClick = { selectedOption = StatusPrivacyOption.MY_CONTACTS_EXCEPT },
                    colors = RadioButtonDefaults.colors(selectedColor = WhatsAppEmerald)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("My contacts except...", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("Hide your 24h updates from selected contacts", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Option 3: Only share with...
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedOption == StatusPrivacyOption.ONLY_SHARE_WITH,
                    onClick = { selectedOption = StatusPrivacyOption.ONLY_SHARE_WITH },
                    colors = RadioButtonDefaults.colors(selectedColor = WhatsAppEmerald)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Only share with...", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("Select specific friends who can view this story", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSaveOption(selectedOption)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_status_privacy_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Save Privacy Settings", fontWeight = FontWeight.Bold)
            }
        }
    }
}
