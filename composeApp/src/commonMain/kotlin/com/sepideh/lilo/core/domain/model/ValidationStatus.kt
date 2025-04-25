package com.sepideh.lilo.core.domain.model

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
        var base = stringResource(messageId)
            args.forEach {
                val replacement = when (it) {
                    is StringResource -> stringResource(it)
                    else -> it.toString()
                }
                base = base.replace("%", replacement)
            }
        base
    } else {
        value
    }
}

