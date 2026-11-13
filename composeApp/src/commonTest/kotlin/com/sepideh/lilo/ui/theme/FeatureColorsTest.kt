package com.sepideh.lilo.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeatureColorsTest {
    @Test
    fun featureContentMeetsNormalTextContrastInBothThemes() {
        palettes().forEach { (name, theme) ->
            features(theme).forEach { (feature, colors) ->
                assertContrast(colors.onPrimary, colors.primary, "$name $feature action")
                assertContrast(colors.onPrimaryContainer, colors.primaryContainer, "$name $feature container")
                assertContrast(colors.onPrimaryContainer, colors.headerControlContainer, "$name $feature header control")
            }
        }
    }

    @Test
    fun featureTextActionsAndFocusIndicatorsRemainReadableOnNeutralSurfaces() {
        listOf(LiloColorsLight to LightColorScheme, LiloColorsDark to DarkColorScheme).forEach { (theme, material) ->
            features(theme).forEach { (name, colors) ->
                listOf(material.background, material.surface, material.surfaceContainerLow).forEach {
                    assertContrast(colors.primary, it, "$name action on neutral surface")
                }
            }
        }
    }

    @Test
    fun allFeatureFillsAreOpaqueAndHaveDistinctEmphasis() {
        palettes().forEach { (name, theme) ->
            features(theme).forEach { (feature, colors) ->
                listOf(colors.primary, colors.onPrimary, colors.primaryContainer,
                    colors.onPrimaryContainer, colors.headerControlContainer, colors.home.container).forEach {
                    assertEquals(1f, it.alpha, "$name $feature must use opaque tonal tokens")
                }
                val light = theme == LiloColorsLight
                assertTrue(if (light) colors.primary.luminance() < colors.primaryContainer.luminance()
                    else colors.primary.luminance() > colors.primaryContainer.luminance())
            }
        }
    }

    private fun palettes() = listOf("Light" to LiloColorsLight, "Dark" to LiloColorsDark)
    private fun features(colors: LiloColors) = listOf("Tasks" to colors.tasks, "Notes" to colors.notes, "Passwords" to colors.passwords)

    private fun assertContrast(foreground: Color, background: Color, role: String, minimum: Float = 4.5f) {
        val first = foreground.luminance()
        val second = background.luminance()
        val ratio = (maxOf(first, second) + .05f) / (minOf(first, second) + .05f)
        assertTrue(ratio >= minimum, "$role contrast $ratio is below $minimum: $foreground on $background")
    }
}
