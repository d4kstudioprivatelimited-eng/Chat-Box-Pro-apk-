package com.example.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppStrings
import com.example.ui.localization.LocalCurrentLanguage
import com.example.ui.localization.LocalizationManager
import com.example.ui.theme.WhatsAppEmerald

@Composable
fun LanguageSelectorDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val currentLang = LocalCurrentLanguage.current
    val strings = LocalAppStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.selectLanguage,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
            ) {
                items(AppLanguage.entries.toList()) { lang ->
                    val isSelected = lang == currentLang
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                LocalizationManager.setLanguage(context, lang)
                                onDismiss()
                            }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                LocalizationManager.setLanguage(context, lang)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = WhatsAppEmerald)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = lang.flagEmoji,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lang.nativeName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 15.sp,
                                color = if (isSelected) WhatsAppEmerald else MaterialTheme.colorScheme.onSurface
                            )
                            if (lang.nativeName != lang.englishName) {
                                Text(
                                    text = lang.englishName,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = WhatsAppEmerald, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
