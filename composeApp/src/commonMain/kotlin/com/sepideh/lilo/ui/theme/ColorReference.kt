package com.sepideh.lilo.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Raw color catalog.
 *
 * This file only defines color primitives.
 * Semantic meaning is assigned in ColorScheme.kt and LiloColors.kt.
 */

// Brand
val Amber300 = Color(0xFFFFD54F)
val Amber500 = Color(0xFFFFC107)
val Amber600 = Color(0xFFFFA000)

// Neutral colors
val White = Color(0xFFFFFFFF)

val Gray400 = Color(0xFFBDBDBD)
val Gray600 = Color(0xFF757575)
val Gray800 = Color(0xFF424242)
val Gray900 = Color(0xFF212121)

val BlueGray900 = Color(0xFF263238)

// Light theme
val LightBackground = Color(0xFFFBF9F6)
val LightOnBackground = Color(0xFF252A28)

val LightSurfaceVariant = Color(0xFFF1EEE8)
val LightSurfaceContainerLow = Color(0xFFFDFBF8)

val LightError = Color(0xFFBA1A1A)

// Dark theme
val DarkBackground = Color(0xFF171918)
val DarkOnBackground = Color(0xFFE6E8E5)

val DarkSurfaceVariant = Color(0xFF303331)
val DarkSurfaceContainerLow = Color(0xFF1D201F)

val DarkError = Color(0xFFFFB4AB)



// Expenses retain their existing identity; no expense UI is part of this refactor.
val BlueLight = Color(0xFFB4D6E0)
val BlueDark = Color(0xFFA4CED9)

// Fixed HCT tones generated with Material Color Utilities TonalPalette.fromInt(seed).
// Generator: com.google.android.material:material:1.13.0 (offline tooling only).
// Preserve seed hue/chroma. Header/action Light roles: 40/100/90/10.
// Dark roles: 80/20/30/90. Home is preserved separately. See docs/feature-colors.md.

// Sage seed #B9CC9D
val Sage10 = Color(0xFF121F02)
val Sage20 = Color(0xFF263513)
val Sage30 = Color(0xFF3C4C28)
val Sage40 = Color(0xFF53643D)
val Sage80 = Color(0xFFBACD9E)
val Sage90 = Color(0xFFD6E9B8)

// Coral seed #E8B9A4
val Coral10 = Color(0xFF2E1508)
val Coral20 = Color(0xFF46291B)
val Coral30 = Color(0xFF603F2F)
val Coral40 = Color(0xFF7A5645)
val Coral80 = Color(0xFFECBCA7)
val Coral90 = Color(0xFFFFDBCC)

// Lavender seed #CBB7E2
val Lavender10 = Color(0xFF231436)
val Lavender20 = Color(0xFF382A4C)
val Lavender30 = Color(0xFF4F4064)
val Lavender40 = Color(0xFF68577D)
val Lavender80 = Color(0xFFD3BEEA)
val Lavender90 = Color(0xFFEDDCFF)
