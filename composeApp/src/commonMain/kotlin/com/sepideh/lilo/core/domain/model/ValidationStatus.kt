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
        // Convert all arguments to strings
        val stringArgs = args.map { arg ->
            if (arg is StringResource) stringResource(arg) else arg.toString()
        }

        // Let Android's stringResource function handle the formatting
        when (stringArgs.size) {
            0 -> stringResource(messageId)
            1 -> stringResource(messageId, stringArgs[0])
            2 -> stringResource(messageId, stringArgs[0], stringArgs[1])
            else -> {
                // For 3+ arguments, we need a different approach
                var result = stringResource(messageId)
                stringArgs.forEachIndexed { index, arg ->
                    result = result.replace("%${index + 1}\$s", arg)
                }
                result
            }
        }
    } else {
        value
    }
}

