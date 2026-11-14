
package com.sepideh.lilo.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Semantic roles for Lilo UI colors. */
@Immutable
data class LiloColors(
    val brand: Color,
    val tasks: FeatureColors,
    val notes: FeatureColors,
    val expenseColor: Color,
    val passwords: FeatureColors,
    val title: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textSupporting: Color,
    val textPlaceholder: Color,
    val textDisabled: Color,
    val onAccent: Color,
    val onPrioritySelected: Color,
    val success: Color,
    val priorityHigh: Color,
    val priorityMedium: Color,
    val priorityLow: Color,
)

val LiloColorsLight = LiloColors(
    brand = HomeBrandLight,

    tasks = FeatureColors(
        accent = Color(0xFFB9CC9D),
        action = Color(0xFF607447),
        onAccent = Color(0xFF607447),
        primary = Color(0xFFA1B189),
        onPrimary = Color(0xFF3E4047),
        primaryContainer = SageHomeLight.container,
        onPrimaryContainer = Sage10,
        headerControlContainer = SageHomeLight.iconContainer,
        home = SageHomeLight,
    ),

    notes = FeatureColors(
        accent = Color(0xFFE8B9A4),
        action = Color(0xFF8B4F37),
        onAccent = Color(0xFF8B4F37),
        primary = Color(0xFFD4A692),
        onPrimary = Color(0xFF3E4047),
        primaryContainer = CoralHomeLight.container,
        onPrimaryContainer = Coral10,
        headerControlContainer = CoralHomeLight.iconContainer,
        home = CoralHomeLight,
    ),

    passwords = FeatureColors(
        accent = Color(0xFFCBB7E2),
        action = Color(0xFF71518E),
        onAccent = Color(0xFF71518E),
        primary = Color(0xFFBAA8CE),
        onPrimary = Color(0xFF3E4047),
        primaryContainer = LavenderHomeLight.container,
        onPrimaryContainer = Lavender10,
        headerControlContainer = LavenderHomeLight.iconContainer,
        home = LavenderHomeLight,
    ),

    expenseColor = BlueLight,

    title = Color(0xFF183B50),
    textPrimary = Color(0xFF46566C),
    textSecondary = Color(0xFF5F6F83),
    textSupporting = Color(0xFF667488),
    textPlaceholder = Color(0xFF657487),
    textDisabled = Color(0xFF9BA5B2),

    onAccent = Color(0xFF183B50),
    onPrioritySelected = White,
    success = Color(0xFF388456),

    priorityHigh = Color(0xFFF05262),
    priorityMedium = Color(0xFFFFC107),
    priorityLow = Color(0xFF32BF99),
)

val LiloColorsDark = LiloColors(
    brand = HomeBrandDark,

    tasks = FeatureColors(
        accent = Color(0xFFC0D2A4),
        action = Color(0xFFC0D2A4),
        onAccent = Color(0xFF607447),
        primary = SageHomeDark.iconContainer,
        onPrimary = White,
        primaryContainer = Color(0xFF383D36),
        onPrimaryContainer = Sage90,
        headerControlContainer = SageHomeDark.iconContainer,
        home = SageHomeDark,
    ),

    notes = FeatureColors(
        accent = Color(0xFFF0B8AB),
        action = Color(0xFFF0B8AB),
        onAccent = Color(0xFF75433F),
        primary = Color(0xFF7B6260),
        onPrimary = White,
        primaryContainer = Color(0xFF443638),
        onPrimaryContainer = Color(0xFFFFDAD2),
        headerControlContainer = CoralHomeDark.iconContainer,
        home = CoralHomeDark,
    ),

    passwords = FeatureColors(
        accent = Color(0xFFD3C1E7),
        action = Color(0xFFD3C1E7),
        onAccent = Color(0xFF71518E),
        primary = Color(0xFF796B88),
        onPrimary = White,
        primaryContainer = Color(0xFF423B4C),
        onPrimaryContainer = Lavender90,
        headerControlContainer = LavenderHomeDark.iconContainer,
        home = LavenderHomeDark,
    ),

    expenseColor = BlueDark,

    title = Color(0xFFE6ECF3),
    textPrimary = Color(0xFFD8E0EA),
    textSecondary = Color(0xFFBAC6D4),
    textSupporting = Color(0xFFA6B3C4),
    textPlaceholder = Color(0xFF97A5B7),
    textDisabled = Color(0xFF748191),

    onAccent = Color(0xFF183B50),
    onPrioritySelected = White,
    success = Color(0xFF83CC9B),

    priorityHigh = Color(0xFFFF8090),
    priorityMedium = Color(0xFFF2C166),
    priorityLow = Color(0xFF72D5B5),
)

val LocalLiloColorsPalette =
    staticCompositionLocalOf { LiloColorsLight }
