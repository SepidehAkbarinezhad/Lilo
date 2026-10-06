package com.sepideh.lilo.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.test.*

class LiloColorsTest {
    @Test fun enabledLightTextRemainsReadableOnBothReferenceSurfaces() {
        val colors = LiloColorsLight
        val text = listOf(colors.title, colors.textPrimary, colors.textSecondary, colors.textSupporting, colors.textPlaceholder, colors.taskAction)
        listOf(LightBackground, White).forEach { background ->
            text.forEach { foreground -> assertTrue(contrast(foreground, background) >= 4.5f, "Low text contrast: $foreground") }
        }
    }
    @Test fun semanticContentAndSurfacesNeverUsePureBlack() {
        listOf(LiloColorsLight, LiloColorsDark).forEach { colors ->
            listOf(colors.title, colors.textPrimary, colors.textSecondary, colors.textSupporting, colors.textPlaceholder, colors.textDisabled, colors.onAccent).forEach { color ->
                assertFalse(color.red == 0f && color.green == 0f && color.blue == 0f)
            }
        }
        assertEquals(White, LiloColorsLight.onPrioritySelected)
        assertEquals(White, LiloColorsDark.onPrioritySelected)
    }
}
private fun contrast(a: Color, b: Color): Float {
    val first = a.luminance(); val second = b.luminance()
    return (maxOf(first, second) + .05f) / (minOf(first, second) + .05f)
}
