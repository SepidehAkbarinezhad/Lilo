package com.sepideh.lilo.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray

data class LiloColors(
    val headerSurface: Color = Color.Unspecified,
    val selectedCategory: Color = Color.Unspecified,
    val unSelectedCategory: Color = Color.Unspecified,
    val primaryTitle: Color = Color.Unspecified,
    val primaryContainerTitle: Color = Color.Unspecified,
    val elevatedCard: Color = Color.Unspecified,
)

val headerSurfaceLight = PrimaryLight
val headerSurfaceDark = Black

val selectedCategoryLight = PrimaryLight
val selectedCategoryDark = Color(color = 0xFFFFFFFF)

val unSelectedCategoryLight = Gray
val unSelectedCategoryDark = Gray

val primaryTitleLight = PrimaryLight
val primaryTitleDark = PrimaryLight

val primaryContainerTitleLight = PrimaryContainerLight
val primaryContainerTitleDark = PrimaryContainerLight


val elevatedCardLight = Color(color = 0xFFFFFFFF)
val elevatedCardDark = Grey800

val LiloColorsLight = LiloColors(
    headerSurface=headerSurfaceLight,
    selectedCategory = selectedCategoryLight,
    unSelectedCategory = unSelectedCategoryLight,
    primaryTitle = primaryTitleLight,
    primaryContainerTitle = primaryContainerTitleLight,
    elevatedCard = elevatedCardLight
)
val LiloColorsDark = LiloColors(
    headerSurface = headerSurfaceDark,
    selectedCategory = selectedCategoryDark,
    unSelectedCategory = unSelectedCategoryDark,
    primaryTitle = primaryTitleDark,
    primaryContainerTitle=primaryContainerTitleDark,
    elevatedCard = elevatedCardDark
)

val LocalLiloColorsPalette = compositionLocalOf { LiloColors() }
