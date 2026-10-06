package com.sepideh.lilo.core.presentation.components.group

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Inline creation is local UI state; owners perform validation/persistence and report completion. */
@Composable
fun InlineGroupEditor(accent: Color, busy: Boolean, addedVersion: Int, onCreate: (String) -> Unit,
    onEditingChanged: (Boolean) -> Unit = {}) {
    var editing by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val currentChanged by rememberUpdatedState(onEditingChanged)
    LaunchedEffect(editing) { currentChanged(editing); if (editing) focus.requestFocus() }
    LaunchedEffect(addedVersion) {
        if (addedVersion > 0) { editing = false; name = ""; focusManager.clearFocus(); keyboard?.hide() }
    }
    fun cancel() { editing = false; name = ""; focusManager.clearFocus(); keyboard?.hide() }
    fun submit() { if (name.isNotBlank() && !busy) onCreate(name.trim()) }
    if (editing) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = name, onValueChange = { name = it }, enabled = !busy, singleLine = true,
                modifier = Modifier.weight(1f).focusRequester(focus), shape = MaterialTheme.shapes.medium,
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = { AppText(text = Res.string.new_group_hint, color = LiloExtendedTheme.colors.textPlaceholder) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { submit() }),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent, cursorColor = accent,
                    focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface))
            IconButton(onClick = { submit() }, enabled = name.isNotBlank() && !busy) {
                if (busy) CircularProgressIndicator(Modifier.size(LiloSize.SmallIcon), color = accent)
                else Icon(LiloIcons.Check, stringResource(Res.string.confirm_action), tint = if (name.isNotBlank()) LiloExtendedTheme.colors.title else LiloExtendedTheme.colors.textDisabled)
            }
            IconButton(onClick = { cancel() }, enabled = !busy) { Icon(LiloIcons.Close, stringResource(Res.string.cancel_button), tint = LiloExtendedTheme.colors.textSecondary) }
        }
    } else {
        TextButton(onClick = { editing = true }, enabled = !busy) {
            Icon(LiloIcons.Add, null, Modifier.size(LiloSize.SmallIcon), tint = LiloExtendedTheme.colors.title)
            Spacer(Modifier.width(LiloSpacing.Small))
            AppText(text = Res.string.add_group_action, textType = TextType.Action, color = LiloExtendedTheme.colors.title)
        }
    }
}
