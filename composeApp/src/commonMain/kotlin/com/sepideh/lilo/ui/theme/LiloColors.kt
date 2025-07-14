package com.sepideh.lilo.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White

data class LiloColors(
    val appText: Color = Color.Unspecified,
    val taskItem: Color = Color.Unspecified,
    val selectedCategory: Color = Color.Unspecified,
    val unSelectedCategory: Color = Color.Unspecified,
    val reminderColor: Color = Color.Unspecified,
    val titleColor: Color = Color.Unspecified,
)

val appTextLight = Color(color = 0xFF000000)
val appTextDark = Color(color = 0xFFCCCCCC)

val taskItemLight = Color(color = 0x0DFFAB00)
val taskItemDark = Color(color = 0xFF1F1F1F)

val selectedCategoryLight = PrimaryLight
val selectedCategoryDark = Color(color = 0xFFFFFFFF)

val unSelectedCategoryLight = Gray
val unSelectedCategoryDark = DarkGray

val reminderColorLight = PrimaryLight
val reminderColorDark = PrimaryLight


val LiloColorsLight = LiloColors(
    appText = appTextLight,
    taskItem = taskItemLight,
    selectedCategory = selectedCategoryLight,
    unSelectedCategory = unSelectedCategoryLight,
    reminderColor = reminderColorLight
)
val LiloColorsDark = LiloColors(
    appText = appTextDark,
    taskItem = taskItemDark,
    selectedCategory = selectedCategoryDark,
    unSelectedCategory = unSelectedCategoryDark,
    reminderColor = reminderColorDark
)

val LocalLiloColorsPalette = compositionLocalOf { LiloColors() }
