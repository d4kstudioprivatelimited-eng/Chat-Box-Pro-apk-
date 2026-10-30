package com.example.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreenDark

enum class AppPermission(
    val manifestPermission: String,
    val title: String,
    val rationale: String,
    val icon: ImageVector,
    val minSdk: Int = 0
) {
    CAMERA(
        manifestPermission = Manifest.permission.CAMERA,
        title = "Camera Permission Required",
        rationale = "Chat Box Pro needs access to your Camera to let you take photos, record videos, and make video calls.",
        icon = Icons.Default.CameraAlt
    ),
    MICROPHONE(
        manifestPermission = Manifest.permission.RECORD_AUDIO,
        title = "Microphone Permission Required",
        rationale = "Chat Box Pro needs access to your Microphone to record voice notes and connect high-quality voice and video calls.",
        icon = Icons.Default.Mic
    ),
    CONTACTS(
        manifestPermission = Manifest.permission.READ_CONTACTS,
        title = "Contacts Permission Required",
        rationale = "Chat Box Pro uses your contacts to find which of your friends and family are already using Chat Box Pro. Contacts are hashed and never shared.",
        icon = Icons.Default.Contacts
    ),
    NOTIFICATIONS(
        manifestPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            ""
        },
        title = "Notification Permission Required",
        rationale = "Chat Box Pro needs permission to notify you about new incoming messages, group chats, and incoming calls.",
        icon = Icons.Default.Notifications,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )
}

object PermissionManager {

    fun hasPermission(context: Context, permission: AppPermission): Boolean {
        if (permission.manifestPermission.isEmpty()) return true
        if (Build.VERSION.SDK_INT < permission.minSdk) return true
        return ContextCompat.checkSelfPermission(
            context,
            permission.manifestPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}

/**
 * State and launcher for Just-In-Time (JIT) permission requests in Compose.
 */
@Composable
fun rememberPermissionRequester(
    permission: AppPermission,
    onPermissionGranted: () -> Unit
): PermissionRequesterState {
    val context = LocalContext.current
    var showRationaleDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showRationaleDialog = false
            onPermissionGranted()
        } else {
            showRationaleDialog = true
        }
    }

    return remember(permission) {
        PermissionRequesterState(
            permission = permission,
            request = {
                if (PermissionManager.hasPermission(context, permission)) {
                    onPermissionGranted()
                } else {
                    launcher.launch(permission.manifestPermission)
                }
            },
            showRationale = { showRationaleDialog },
            dismissRationale = { showRationaleDialog = false }
        )
    }
}

class PermissionRequesterState(
    val permission: AppPermission,
    val request: () -> Unit,
    val showRationale: () -> Boolean,
    val dismissRationale: () -> Unit
)

/**
 * Standard WhatsApp-style permission explanation dialog guiding the user to app settings.
 */
@Composable
fun PermissionRationaleDialog(
    permission: AppPermission,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = permission.icon,
                contentDescription = permission.title,
                tint = WhatsAppEmerald,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = permission.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = permission.rationale,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onOpenSettings()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
