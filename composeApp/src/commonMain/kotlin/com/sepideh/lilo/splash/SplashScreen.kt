package com.sepideh.lilo.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.core.utils.LiloInfo
import com.sepideh.lilo.core.utils.PlatformType
import com.sepideh.lilo.ui.theme.DeepOrange500
import com.sepideh.lilo.ui.theme.White
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.app_name
import lilo.composeapp.generated.resources.app_slogan
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    liloInfo: LiloInfo = koinInject(),
    onNavigateTo: (AppRoutes) -> Unit,

    ) {
    LaunchedEffect(Unit) {
        delay(2_000.milliseconds)
        onNavigateTo(AppRoutes.Home)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepOrange500),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                color = White,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppText(
                text = Res.string.app_slogan,
                color = White.copy(alpha = 0.85f),
                textType = TextType.SubTitle
            )
        }

        AppText(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(6.dp),
            text = liloInfo.appVersion,
            color = White.copy(alpha = 0.85f),
            textType = TextType.Body
        )
    }
}

@AppPreviews
@Composable
private fun SplashScreenPreview() {
    LiloPreviewWrapper {
        SplashScreen(
            liloInfo = object : LiloInfo {
            override val platformType = PlatformType.ANDROID
            override val appVersion = "1.0.0"
        }, onNavigateTo = {},
        )
    }
}
