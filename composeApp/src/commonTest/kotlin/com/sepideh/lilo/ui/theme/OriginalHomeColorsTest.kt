package com.sepideh.lilo.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import kotlin.test.Test
import kotlin.test.assertEquals

class OriginalHomeColorsTest {
    @Test fun fixedHomeTokensMatchCommittedAppearance() {
        listOf(LiloColorsLight, LiloColorsDark).forEachIndexed { index, theme ->
            val dark = index == 1
            listOf(theme.tasks, theme.notes, theme.passwords).forEachIndexed { feature, colors ->
                val accent = listOf(
                    listOf(Color(0xFFB9CC9D), Color(0xFFE8B9A4), Color(0xFFCBB7E2)),
                    // Dark notes use a pink-peach seed; the other reference colors stay fixed.
                    listOf(Color(0xFFC0D2A4), Color(0xFFF0B8AB), Color(0xFFD3C1E7)))[index][feature]
                val action = if (dark) accent else listOf(Color(0xFF607447), Color(0xFF8B4F37), Color(0xFF71518E))[feature]
                assertEquals(accent, colors.accent)
                assertEquals(action, colors.action)
                val container = lerp(if (dark) DarkBackground else LightBackground, accent,
                    if (dark) .16f else if (feature == 2) .30f else .24f)
                val report = (if (dark) Gray900 else White).copy(alpha = .65f).compositeOver(container)
                val values = listOf(container, accent.copy(alpha = .32f).compositeOver(container), report,
                    action.copy(alpha = .14f).compositeOver(container), accent.copy(alpha = .15f).compositeOver(container),
                    action.copy(alpha = .2f).compositeOver(container), theme.textSupporting.copy(alpha = .28f).compositeOver(report))
                val home = colors.home
                val actual = listOf(home.container, home.iconContainer, home.reportContainer, home.divider,
                    home.progressTrack, home.checkedContainer, home.summaryDivider)
                assertEquals(values.map { it.toArgb() }, actual.map { it.toArgb() }, "theme=$index feature=$feature")
            }
        }
    }
}
