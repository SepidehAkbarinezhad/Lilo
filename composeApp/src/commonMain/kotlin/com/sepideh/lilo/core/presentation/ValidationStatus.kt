package com.sepideh.lilo.core.presentation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data class ValidationStatus(
    val value: String = "",
    val isSuccessful: Boolean = true,
    val messageId: StringResource? = null,
    val args: Array<Any> = arrayOf()
)

@Composable
fun ValidationStatus.resolveMessage(): String {
    return if (messageId != null) {
        stringResource(
            resource = messageId,
            *args.map {
                if (it is StringResource) stringResource(it) else it.toString()
            }.toTypedArray()
        )
    } else {
        value
    }
}

