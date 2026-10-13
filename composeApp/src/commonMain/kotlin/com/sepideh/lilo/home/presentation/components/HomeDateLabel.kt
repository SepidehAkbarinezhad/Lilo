package com.sepideh.lilo.home.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.sepideh.lilo.core.presentation.format.creationDateLabel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun homeDateLabel(timestamp: Long?): String? {
    if (timestamp == null || timestamp <= 0) return null
    val zone = TimeZone.currentSystemDefault()
    val date = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(zone).date
    val today = Clock.System.now().toLocalDateTime(zone).date
    return when (today.toEpochDays() - date.toEpochDays()) {
        0L -> stringResource(Res.string.task_today)
        1L -> stringResource(Res.string.home_yesterday)
        else -> creationDateLabel(timestamp, LocalLayoutDirection.current == LayoutDirection.Rtl)
    }
}
