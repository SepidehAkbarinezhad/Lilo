package com.sepideh.lilo.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray

data class LiloColors(
    val appText: Color = Color.Unspecified,
    val selectedCategory: Color = Color.Unspecified,
    val unSelectedCategory: Color = Color.Unspecified,
)

val appTextLight = Color(color = 0xFF000000)
val appTextDark = Color(color = 0xFFCCCCCC)

val selectedCategoryLight = PrimaryLight
val selectedCategoryDark = Color(color = 0xFFFFFFFF)

val unSelectedCategoryLight = Gray
val unSelectedCategoryDark = DarkGray

val LiloColorsLight = LiloColors(
    appText = appTextLight,
    selectedCategory = selectedCategoryLight,
    unSelectedCategory = unSelectedCategoryLight
)
val LiloColorsDark = LiloColors(
    appText = appTextDark,
    selectedCategory = selectedCategoryDark,
    unSelectedCategory = unSelectedCategoryDark
)

val LocalLiloColorsPalette = compositionLocalOf { LiloColors() }
