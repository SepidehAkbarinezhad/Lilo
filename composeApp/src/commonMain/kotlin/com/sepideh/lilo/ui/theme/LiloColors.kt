package com.sepideh.lilo.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Semantic roles; components use these instead of choosing slightly different neutrals. */
@Immutable
data class LiloColors(
    val taskColor: Color,
    val noteColor: Color,
    val expenseColor: Color,
    val passwordColor: Color,
    val title: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textSupporting: Color,
    val textPlaceholder: Color,
    val textDisabled: Color,
    val onAccent: Color,
    val onPrioritySelected: Color,
    val taskAction: Color,
    val priorityHigh: Color,
    val priorityLow: Color,
)

val LiloColorsLight = LiloColors(
    taskColor = SageLight, noteColor = CoralLight, expenseColor = BlueLight, passwordColor = LavenderLight,
    title = Color(0xFF183B50), textPrimary = Color(0xFF46566C), textSecondary = Color(0xFF5F6F83),
    textSupporting = Color(0xFF667488), textPlaceholder = Color(0xFF657487), textDisabled = Color(0xFF9BA5B2),
    onAccent = Color(0xFF183B50), onPrioritySelected = White, taskAction = SageActionLight,
    priorityHigh = Color(0xFFF05262), priorityLow = Color(0xFF32BF99),
)
val LiloColorsDark = LiloColors(
    taskColor = SageDark, noteColor = CoralDark, expenseColor = BlueDark, passwordColor = LavenderDark,
    title = Color(0xFFE6ECF3), textPrimary = Color(0xFFD8E0EA), textSecondary = Color(0xFFBAC6D4),
    textSupporting = Color(0xFFA6B3C4), textPlaceholder = Color(0xFF97A5B7), textDisabled = Color(0xFF748191),
    onAccent = Color(0xFF183B50), onPrioritySelected = White, taskAction = SageDark,
    priorityHigh = Color(0xFFFF8090), priorityLow = Color(0xFF72D5B5),
)
val LocalLiloColorsPalette = staticCompositionLocalOf { LiloColorsLight }
