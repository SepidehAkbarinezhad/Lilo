package com.sepideh.lilo.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Feature roles supplied by LiloExtendedTheme; no runtime palette generation. */
@Immutable
data class FeatureColors(
    /** Existing control fills and text/icon actions, preserved from 4f7f599. */
    val accent: Color,
    val action: Color,
    val onAccent: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val headerControlContainer: Color,
    /** Exact committed Home palette, independent of header/action roles. */
    val home: HomeFeatureColors,
)

@Immutable
data class HomeFeatureColors(
    val container: Color,
    val iconContainer: Color,
    val reportContainer: Color,
    val divider: Color,
    val progressTrack: Color,
    val checkedContainer: Color,
    val summaryDivider: Color,
)
