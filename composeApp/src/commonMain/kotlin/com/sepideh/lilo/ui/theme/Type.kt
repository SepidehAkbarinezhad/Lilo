package com.sepideh.lilo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.Font

/** Bundled Vazirmatn covers Persian and Latin on both platforms; no system-font dependency. */
@Composable
fun liloTypography(): Typography {
    val regular = Font(Res.font.vazirmatn_regular, FontWeight.Normal)
    val medium = Font(Res.font.vazirmatn_medium, FontWeight.Medium)
    return remember(regular, medium) {
        val family = FontFamily(regular, medium)
        fun style(size: Int, height: Int, weight: FontWeight = FontWeight.Normal) =
            TextStyle(fontFamily = family, fontSize = size.sp, lineHeight = height.sp, fontWeight = weight, letterSpacing = 0.sp)
        Typography(
            displayLarge = style(40, 52), displayMedium = style(34, 44), displaySmall = style(28, 38),
            headlineLarge = style(26, 36, FontWeight.Medium), headlineMedium = style(24, 34, FontWeight.Medium), headlineSmall = style(22, 32, FontWeight.Medium),
            titleLarge = style(20, 30, FontWeight.Medium), titleMedium = style(17, 27, FontWeight.Medium), titleSmall = style(15, 24, FontWeight.Medium),
            bodyLarge = style(17, 28), bodyMedium = style(15, 25), bodySmall = style(13, 22),
            labelLarge = style(15, 24, FontWeight.Medium), labelMedium = style(13, 22, FontWeight.Medium), labelSmall = style(12, 20),
        )
    }
}
