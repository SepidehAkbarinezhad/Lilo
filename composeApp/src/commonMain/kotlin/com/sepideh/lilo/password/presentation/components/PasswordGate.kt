package com.sepideh.lilo.password.presentation.components

import com.sepideh.lilo.password.domain.UnlockResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.BaseHeader
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.password.presentation.*
import com.sepideh.lilo.ui.icons.PasswordCardIcon
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasswordGate(request: com.sepideh.lilo.password.domain.PasswordUnlockRequest?, locked: Boolean, onBack: () -> Boolean, content: @Composable () -> Unit) {
    var busy by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<UnlockResult?>(null) }
    val unlock = rememberPasswordUnlocker { busy = false; result = it }
    if (!locked) content()
    else PasswordLockedScreen(busy, result, onBack) { if (!busy) { result = null; request?.let { busy = true; unlock(it) } } }
}

@Composable
private fun PasswordLockedScreen(busy: Boolean, result: UnlockResult?, onBack: () -> Boolean, unlock: () -> Unit) {
    val colors = LiloExtendedTheme.colors
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        BaseHeader(title = Res.string.password_feature_title, modifier = Modifier.statusBarsPadding(), onBackPressed = onBack)
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)) {
            Surface(shape = CircleShape, color = colors.passwordColor.copy(alpha = .12f)) {
                Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) { Icon(PasswordCardIcon, null, Modifier.size(32.dp), tint = colors.passwordAction) }
            }
            AppText(text = Res.string.password_locked, textType = TextType.SectionTitle)
            AppText(text = Res.string.password_unlock_reason, color = colors.textSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            if (result == UnlockResult.UNAVAILABLE || result == UnlockResult.FAILED || result == UnlockResult.KEY_UNAVAILABLE) AppText(
                text = when (result) {
                    UnlockResult.UNAVAILABLE -> Res.string.password_auth_unavailable
                    UnlockResult.KEY_UNAVAILABLE -> Res.string.password_key_unavailable
                    else -> Res.string.password_unlock_failed
                },
                color = MaterialTheme.colorScheme.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Button(onClick = unlock, enabled = !busy, colors = ButtonDefaults.buttonColors(containerColor = colors.passwordColor, contentColor = colors.onPasswordAccent)) {
                Text(stringResource(if (busy) Res.string.password_unlocking else Res.string.password_unlock_action))
            }
        }
    }
}

@AppPreviews @Composable private fun PasswordLockedPreview() { LiloPreviewWrapper { PasswordLockedScreen(false, null, { true }, {}) } }
