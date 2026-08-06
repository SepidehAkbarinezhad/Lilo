package com.sepideh.lilo.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray

/**
 * Custom color palette holding additional domain-specific colors beyond Material3's ColorScheme.
 *
 * @Immutable tells the Compose compiler these properties never change at runtime, enabling recomposition skipping.
 * otherwise  any composable reading the class will be forced to recompose on every parent update
 */
@Immutable
data class LiloColors(
    val taskColor: Color = Color.Unspecified,
    val noteColor: Color = Color.Unspecified,
    val expenseColor: Color = Color.Unspecified,
    val passwordColor: Color = Color.Unspecified,

    val headerSurface: Color = Color.Unspecified,
    val selectedCategory: Color = Color.Unspecified,
    val unSelectedCategory: Color = Color.Unspecified,
    val primaryTitle: Color = Color.Unspecified,
    val primaryContainerTitle: Color = Color.Unspecified,
    val elevatedCard: Color = Color.Unspecified,
)


val LiloColorsLight = LiloColors(
    taskColor = Amber500,
    noteColor = Green500,
    expenseColor = Blue700,
    passwordColor = Purple500,
    headerSurface = Color(0xFFF5F2EC),
    selectedCategory = Amber600,
    unSelectedCategory = Gray,
    primaryTitle = Amber600,
    primaryContainerTitle = Amber500,
    elevatedCard = White
)

val LiloColorsDark = LiloColors(
    taskColor = Amber300,
    noteColor = Green300,
    expenseColor = Blue300,
    passwordColor = Purple200,
    headerSurface = Black,
    selectedCategory = White,
    unSelectedCategory = Gray,
    primaryTitle = Amber600,
    primaryContainerTitle = Gray800,
    elevatedCard = Gray800
)

val LocalLiloColorsPalette = staticCompositionLocalOf { LiloColors() }
