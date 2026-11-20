package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

/**
 * Compose only while visible. Material owns animations, gestures and platform back handling.
 * Removing this component also releases sheet input (including button/navigation dismissal).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissEnabled: Boolean = true,
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    content: @Composable ColumnScope.() -> Unit,
) {
    val parentFocus = LocalFocusManager.current
    val parentKeyboard = LocalSoftwareKeyboardController.current
    var parentInputReleased by remember { mutableStateOf(false) }

    // Run before composing the modal: an effect launched alongside its content could race
    // an editor's initial focus request, especially across separate platform dialog owners.
    DisposableEffect(parentFocus, parentKeyboard) {
        parentFocus.clearFocus(force = true)
        parentKeyboard?.hide()
        parentInputReleased = true
        onDispose { }
    }

    val currentDismissEnabled = rememberUpdatedState(dismissEnabled)
    // Keep identity stable: changing the busy flag must not recreate SheetState.
    val confirmValueChange: (SheetValue) -> Boolean = remember {
        { target -> target != SheetValue.Hidden || currentDismissEnabled.value }
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = confirmValueChange,
    )

    if (parentInputReleased) {
        ModalBottomSheet(
            onDismissRequest = { if (currentDismissEnabled.value) onDismissRequest() },
            modifier = modifier,
            sheetState = sheetState,
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = contentWindowInsets,
        ) {
            // Read inside the modal: Android dialogs and iOS Compose layers can have
            // a different focus owner / keyboard controller from the underlying screen.
            val sheetFocus = LocalFocusManager.current
            val sheetKeyboard = LocalSoftwareKeyboardController.current
            DisposableEffect(sheetFocus, sheetKeyboard) {
                onDispose {
                    sheetFocus.clearFocus(force = true)
                    sheetKeyboard?.hide()
                }
            }
            content()
        }
    }
}
