package com.sepideh.lilo.settings.presentation.model

import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.core.domain.model.AppTheme
import lilo.composeapp.generated.resources.lilo_theme_system
import lilo.composeapp.generated.resources.lilo_theme_light
import lilo.composeapp.generated.resources.lilo_theme_dark
import org.jetbrains.compose.resources.DrawableResource
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.dark_mode_label
import lilo.composeapp.generated.resources.english_label
import lilo.composeapp.generated.resources.light_mode_label
import lilo.composeapp.generated.resources.persian_label
import lilo.composeapp.generated.resources.system_mode_label
import org.jetbrains.compose.resources.StringResource

val AppLanguage.labelRes: StringResource
    get() = when (this) {
        AppLanguage.FA -> Res.string.persian_label
        AppLanguage.EN -> Res.string.english_label
    }

val AppTheme.labelRes: StringResource
    get() = when (this) {
        AppTheme.SYSTEM -> Res.string.system_mode_label
        AppTheme.LIGHT -> Res.string.light_mode_label
        AppTheme.DARK -> Res.string.dark_mode_label
    }

val AppTheme.iconRes: DrawableResource
    get() = when (this) {
        AppTheme.SYSTEM -> Res.drawable.lilo_theme_system
        AppTheme.LIGHT -> Res.drawable.lilo_theme_light
        AppTheme.DARK -> Res.drawable.lilo_theme_dark
    }
