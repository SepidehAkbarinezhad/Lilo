package com.sepideh.lilo.core.presentation.components.gesture

import com.sepideh.lilo.ui.theme.*

import com.sepideh.lilo.core.presentation.icons.LiloIcons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Dragging only reveals the action. Only an explicit tap requests deletion. */
@Composable
fun SwipeToRevealDelete(
    enabled: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val actionWidth = 80.dp
    val widthPx = with(LocalDensity.current) { actionWidth.toPx() }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    var revealed by rememberSaveable { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val target = if (dragging) dragOffset else if (revealed) -widthPx else 0f
    val offset by animateFloatAsState(target, tween(if (dragging) 0 else 180), label = "reveal-delete")
    LaunchedEffect(enabled) { if (!enabled) revealed = false }
    Box(modifier.clipToBounds()) {
        if (offset < -1f) {
            FilledTonalIconButton(
                onClick = { revealed = false; onDelete() }, enabled = enabled && revealed && !dragging,
                modifier = Modifier.align(Alignment.CenterEnd).padding(horizontal = LiloSpacing.Item).size(LiloSize.TouchTarget),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer),
            ) {
                Icon(LiloIcons.Delete, stringResource(Res.string.delete_action), Modifier.size(LiloSize.SmallIcon))
            }
        }
        Box(Modifier.offset { IntOffset(offset.roundToInt(), 0) }
            .draggable(
                state = rememberDraggableState { delta -> dragOffset = (dragOffset + delta).coerceIn(-widthPx, 0f) },
                orientation = Orientation.Horizontal, enabled = enabled, reverseDirection = rtl,
                onDragStarted = { dragOffset = offset; dragging = true },
                onDragStopped = { velocity ->
                    revealed = when {
                        velocity < -600f -> true
                        velocity > 600f -> false
                        else -> dragOffset < -widthPx * .4f
                    }
                    dragging = false
                },
            )) { content() }
    }
}
