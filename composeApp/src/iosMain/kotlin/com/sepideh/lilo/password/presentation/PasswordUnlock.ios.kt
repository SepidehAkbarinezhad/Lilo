package com.sepideh.lilo.password.presentation

import com.sepideh.lilo.password.domain.UnlockResult
import com.sepideh.lilo.password.domain.PasswordUnlockRequest
import androidx.compose.runtime.*
import com.sepideh.lilo.password.data.IosVaultCrypto
import com.sepideh.lilo.password.domain.VaultCrypto
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.launch
import platform.Foundation.*
import platform.UIKit.*
import org.koin.compose.koinInject
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun rememberPasswordUnlocker(onResult: (UnlockResult) -> Unit): (PasswordUnlockRequest) -> Unit {
    val crypto = koinInject<VaultCrypto>() as IosVaultCrypto
    val callback by rememberUpdatedState(onResult)
    val scope = rememberCoroutineScope()
    val reason = stringResource(Res.string.password_unlock_reason)
    var busy by remember { mutableStateOf(false) }
    DisposableEffect(crypto) { onDispose { crypto.cancelUnlock() } }
    return { request -> if (!busy) { busy = true; scope.launch { try { callback(crypto.unlock(reason, request)) } finally { busy = false } } } }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PasswordSessionEffect() {
    val crypto = koinInject<VaultCrypto>() as IosVaultCrypto
    DisposableEffect(crypto) {
        val center = NSNotificationCenter.defaultCenter
        val observer = center.addObserverForName(UIApplicationDidEnterBackgroundNotification, null, NSOperationQueue.mainQueue) {
            crypto.cancelUnlock()
            crypto.lock()
        }
        onDispose { center.removeObserver(observer); crypto.cancelUnlock(); crypto.lock() }
    }
}
