package com.sepideh.lilo.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray

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
    headerSurface = Amber600,
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
    primaryContainerTitle = Grey800,
    elevatedCard = Grey800
)

val LocalLiloColorsPalette = staticCompositionLocalOf { LiloColors() }
