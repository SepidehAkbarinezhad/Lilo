package com.sepideh.lilo.password.presentation.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.format.creationDateLabel
import com.sepideh.lilo.password.domain.Password
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PasswordListItem(record: Password, revealed: String? = null, busy: Boolean = false,
    onReveal: () -> Unit, onClick: () -> Unit) {
    val colors = LiloExtendedTheme.colors
    val valueHeight = with(LocalDensity.current) { MaterialTheme.typography.bodyMedium.lineHeight.toDp() }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val date = creationDateLabel(record.updatedAt.takeIf { it > 0 } ?: record.createdAt, rtl)
    val dateDescription = date?.let { stringResource(Res.string.password_updated_date, it) }
    val identifier = record.username.ifBlank { record.title }
    Surface(onClick = onClick, enabled = !busy, modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = .4f))) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                PasswordServiceIcon(record.serviceId, colors.textSecondary, Modifier.size(18.dp))
                Text(record.serviceName.ifBlank { stringResource(Res.string.not_set_label) },
                    color = colors.textSecondary, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column(Modifier.fillMaxWidth().padding(start = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    PasswordItemLabel(stringResource(if (record.username.isNotBlank()) Res.string.password_username else Res.string.title_label))
                    Text(identifier, Modifier.fillMaxWidth(), color = colors.textPrimary,
                        textAlign = if (rtl) TextAlign.Right else TextAlign.Left,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal,
                            textDirection = if (record.username.isNotBlank()) TextDirection.Ltr else TextDirection.Content),
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    PasswordItemLabel(stringResource(Res.string.password_secret))
                    Row(Modifier.fillMaxWidth().height(valueHeight), verticalAlignment = Alignment.CenterVertically) {
                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onReveal, enabled = !busy, modifier = Modifier.width(48.dp).height(valueHeight).wrapContentHeight(unbounded = true).size(48.dp)) {
                                if (busy) CircularProgressIndicator(Modifier.size(18.dp), color = colors.passwordAction, strokeWidth = 2.dp)
                                else Icon(vectorResource(if (revealed == null) Res.drawable.lilo_eye else Res.drawable.lilo_eye_off),
                                    stringResource(if (revealed == null) Res.string.password_reveal else Res.string.password_hide),
                                    Modifier.size(18.dp), tint = colors.passwordAction)
                            }
                            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                if (revealed == null) Row(Modifier.clearAndSetSemantics { text = AnnotatedString("••••••••") },
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    repeat(8) { Box(Modifier.size(4.dp).background(colors.textSupporting, CircleShape)) }
                                } else Text(revealed, Modifier.fillMaxWidth(), color = colors.textPrimary,
                                    textAlign = if (rtl) TextAlign.Right else TextAlign.Left,
                                    style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Ltr),
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        if (date != null) Text(date, color = colors.textSupporting, style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 8.dp).clearAndSetSemantics { contentDescription = dateDescription.orEmpty() })
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordItemLabel(label: String) {
    Text(label, color = LiloExtendedTheme.colors.textSupporting,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal))
}

@AppPreviews
@Composable
private fun PasswordItemPreview() {
    LiloPreviewWrapper {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PasswordListItem(Password(serviceId = "google", serviceName = "Gmail", username = "sepideh@gmail.com", updatedAt = 1791244800000L), onReveal = {}, onClick = {})
            PasswordListItem(Password(serviceId = "google", serviceName = "Gmail", username = "sepideh@gmail.com", updatedAt = 1791244800000L), revealed = "Example-password-123!", onReveal = {}, onClick = {})
            PasswordListItem(Password(serviceId = "github", serviceName = "GitHub", title = "Personal projects", updatedAt = 1791331200000L), onReveal = {}, onClick = {})
        }
    }
}
