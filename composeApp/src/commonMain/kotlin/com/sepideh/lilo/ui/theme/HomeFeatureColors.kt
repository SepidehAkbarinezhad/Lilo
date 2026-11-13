package com.sepideh.lilo.ui.theme

import androidx.compose.ui.graphics.Color

// Exact sRGB results of the Home colors in commit 4f7f599.
// Compose's Oklab lerp and alpha compositing are evaluated only in OriginalHomeColorsTest.
// Order: card, icon badge, inner report, divider, progress track, checked box, report divider.
val SageHomeLight = HomeFeatureColors(
    Color(0xFFEBEEE0), Color(0xFFDBE3CA), Color(0xFFF8F9F4), Color(0xFFD7DDCA), Color(0xFFE4E9D6), Color(0xFFCFD6C1), Color(0xFFCFD4D6),
)
val CoralHomeLight = HomeFeatureColors(
    Color(0xFFF7EAE2), Color(0xFFF2DACE), Color(0xFFFCF8F5), Color(0xFFE8D4CA), Color(0xFFF5E3D9), Color(0xFFE1CBC0), Color(0xFFD2D3D7),
)
val LavenderHomeLight = HomeFeatureColors(
    Color(0xFFECE5F0), Color(0xFFE1D6EB), Color(0xFFF8F6FA), Color(0xFFDBD0E2), Color(0xFFE7DEEE), Color(0xFFD3C7DC), Color(0xFFCFD2DA),
)
val SageHomeDark = HomeFeatureColors(
    Color(0xFF2E322C), Color(0xFF5D6553), Color(0xFF262725), Color(0xFF43493D), Color(0xFF444A3E), Color(0xFF4B5244), Color(0xFF4A4E51),
)
val CoralHomeDark = HomeFeatureColors(
    Color(0xFF332F2A), Color(0xFF6C5C4E), Color(0xFF272624), Color(0xFF4C433A), Color(0xFF4E443B), Color(0xFF574B40), Color(0xFF4A4D51),
)
val LavenderHomeDark = HomeFeatureColors(
    Color(0xFF303034), Color(0xFF645F6E), Color(0xFF262628), Color(0xFF47444D), Color(0xFF48464F), Color(0xFF514D58), Color(0xFF4A4D53),
)
