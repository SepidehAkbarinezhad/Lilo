package com.sepideh.lilo.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray

data class LiloColors(
    val selectedCategory: Color = Color.Unspecified,
    val unSelectedCategory: Color = Color.Unspecified,
    val primaryTitle: Color = Color.Unspecified,
    val primaryContainerTitle: Color = Color.Unspecified,
)

val selectedCategoryLight = PrimaryLight
val selectedCategoryDark = Color(color = 0xFFFFFFFF)

val unSelectedCategoryLight = Gray
val unSelectedCategoryDark = DarkGray

val primaryTitleLight = PrimaryLight
val primaryTitleDark = PrimaryLight

val primaryContainerTitleLight = PrimaryContainerLight
val primaryContainerTitleDark = PrimaryContainerLight


val LiloColorsLight = LiloColors(
    selectedCategory = selectedCategoryLight,
    unSelectedCategory = unSelectedCategoryLight,
    primaryTitle = primaryTitleLight,
    primaryContainerTitle = primaryContainerTitleLight,
)
val LiloColorsDark = LiloColors(
    selectedCategory = selectedCategoryDark,
    unSelectedCategory = unSelectedCategoryDark,
    primaryTitle = primaryTitleDark,
    primaryContainerTitle=primaryContainerTitleDark
)

val LocalLiloColorsPalette = compositionLocalOf { LiloColors() }
