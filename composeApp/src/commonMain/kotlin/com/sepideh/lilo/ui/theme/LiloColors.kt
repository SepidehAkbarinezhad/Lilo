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

)


val LiloColorsLight = LiloColors(
    taskColor = Amber500,
    noteColor = Green500,
    expenseColor = Blue700,
    passwordColor = Purple500,
)

val LiloColorsDark = LiloColors(
    taskColor = Amber300,
    noteColor = Green300,
    expenseColor = Blue300,
    passwordColor = Purple200,
)

val LocalLiloColorsPalette = staticCompositionLocalOf { LiloColors() }
