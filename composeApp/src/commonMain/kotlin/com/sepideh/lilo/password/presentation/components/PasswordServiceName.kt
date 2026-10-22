package com.sepideh.lilo.password.presentation.components

import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.password.domain.model.PasswordService
import com.sepideh.lilo.ui.theme.LocalLiloAppLanguage

@Composable
fun localizedServiceName(name: String, nameFa: String): String =
    if (LocalLiloAppLanguage.current == AppLanguage.FA) nameFa else name

@Composable
fun PasswordService.displayName(): String = localizedServiceName(name, nameFa)
