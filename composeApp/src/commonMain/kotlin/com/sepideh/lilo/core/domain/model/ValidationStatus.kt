package com.sepideh.lilo.core.domain.model

import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.validation.resolveMessage as resolveUiMessage

// Compatibility for legacy features. New presentation code uses presentation.validation.
typealias ValidationStatus = com.sepideh.lilo.core.presentation.validation.ValidationStatus

@Composable
fun ValidationStatus.resolveMessage(): String = resolveUiMessage()
