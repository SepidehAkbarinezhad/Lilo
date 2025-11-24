package com.sepideh.lilo.settings.presentation.model

import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.dark_mode_label
import lilo.composeapp.generated.resources.english_label
import lilo.composeapp.generated.resources.light_mode_label
import lilo.composeapp.generated.resources.persian_label
import lilo.composeapp.generated.resources.system_mode_label
import org.jetbrains.compose.resources.StringResource

enum class AppLanguage(val label: StringResource) {
    FA(Res.string.persian_label),
    EN(Res.string.english_label)
}

enum class AppTheme(val label: StringResource) {
    SYSTEM(Res.string.system_mode_label),
    LIGHT(Res.string.light_mode_label),
    DARK(Res.string.dark_mode_label),
}