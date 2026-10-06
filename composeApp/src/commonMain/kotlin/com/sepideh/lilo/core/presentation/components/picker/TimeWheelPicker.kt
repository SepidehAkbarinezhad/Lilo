package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.ui.theme.*
import com.sepideh.lilo.core.presentation.format.localizedDigits
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import kotlinx.coroutines.flow.distinctUntilChanged
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

@Composable
fun TimeWheelPicker(hour: Int, minute: Int, accent: Color, onHourChange: (Int) -> Unit, onMinuteChange: (Int) -> Unit) {
    val persian = LocalLayoutDirection.current == LayoutDirection.Rtl
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)) {
            NumberWheel(hour, 24, stringResource(Res.string.hour_label), accent, onHourChange, persian)
            NumberWheel(minute, 60, stringResource(Res.string.minute_label), accent, onMinuteChange, persian)
        }
    }
}

@Composable
private fun NumberWheel(value: Int, count: Int, label: String, accent: Color, onChange: (Int) -> Unit, persian: Boolean) {
    val state = rememberLazyListState(initialFirstVisibleItemIndex = value)
    val currentOnChange by rememberUpdatedState(onChange)
    LaunchedEffect(state) {
        snapshotFlow {
            if (state.isScrollInProgress) null else {
                val info = state.layoutInfo
                val center = (info.viewportStartOffset + info.viewportEndOffset) / 2
                info.visibleItemsInfo.minByOrNull { abs(it.offset + it.size / 2 - center) }?.index
            }
        }.distinctUntilChanged().collect { index -> index?.let { currentOnChange(it) } }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppText(text = label, textType = TextType.FieldLabel)
        Box(Modifier.width(80.dp).height(144.dp), contentAlignment = Alignment.Center) {
            Surface(Modifier.fillMaxWidth().height(48.dp), shape = MaterialTheme.shapes.medium,
                color = accent.copy(alpha = .08f), border = BorderStroke(1.dp, accent)) {}
            // Fixed height bounds the nested list even inside a scrollable form.
            LazyColumn(Modifier.fillMaxSize(), state = state,
                contentPadding = PaddingValues(vertical = 48.dp),
                flingBehavior = rememberSnapFlingBehavior(state),
                horizontalAlignment = Alignment.CenterHorizontally) {
                items(count, key = { it }) { number ->
                    Box(Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center) {
                        AppText(text = number.toString().padStart(2, '0').localizedDigits(persian),
                            textType = if (number == value) TextType.SectionTitle else TextType.Body,
                            color = if (number == value) MaterialTheme.colorScheme.onSurface else LiloExtendedTheme.colors.textSupporting)
                    }
                }
            }
        }
    }
}
