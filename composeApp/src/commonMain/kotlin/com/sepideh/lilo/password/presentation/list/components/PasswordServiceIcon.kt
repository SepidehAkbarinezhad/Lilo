package com.sepideh.lilo.password.presentation.list.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun PasswordServiceIcon(id: String?, tint: Color, modifier: Modifier = Modifier) {
    val resource = when (id) {
        "google", "gmail" -> Res.drawable.lilo_service_gmail
        "yahoo" -> Res.drawable.lilo_service_yahoo
        "github" -> Res.drawable.lilo_service_github
        "linkedin" -> Res.drawable.lilo_service_linkedin
        "mobile-banking" -> Res.drawable.lilo_service_bank
        "instagram" -> Res.drawable.lilo_service_instagram
        "apple" -> Res.drawable.lilo_service_apple
        else -> Res.drawable.lilo_service_generic
    }
    Icon(vectorResource(resource), null, modifier, tint = tint)
}

@AppPreviews
@Composable
private fun PasswordServiceIconsPreview() {
    LiloPreviewWrapper {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("google", "yahoo", "custom").forEach { id ->
                PasswordServiceIcon(id, LiloExtendedTheme.colors.textSecondary, Modifier.size(24.dp))
            }
        }
    }
}
