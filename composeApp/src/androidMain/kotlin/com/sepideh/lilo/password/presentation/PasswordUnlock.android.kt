package com.sepideh.lilo.password.presentation

import com.sepideh.lilo.password.domain.UnlockResult
import com.sepideh.lilo.password.domain.PasswordUnlockRequest
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.ContextWrapper
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.BiometricManager
import android.os.Build
import android.os.CancellationSignal
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.sepideh.lilo.password.data.AndroidVaultCrypto
import com.sepideh.lilo.password.domain.VaultCrypto
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private fun Context.activity(): Activity = when (this) { is Activity -> this; is ContextWrapper -> baseContext.activity(); else -> error("No activity") }

@Composable
actual fun rememberPasswordUnlocker(onResult: (UnlockResult) -> Unit): (PasswordUnlockRequest) -> Unit {
    val activity = LocalContext.current.activity()
    val crypto = koinInject<VaultCrypto>() as AndroidVaultCrypto
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val callback by rememberUpdatedState(onResult)
    val title = stringResource(Res.string.password_unlock_title)
    val reason = stringResource(Res.string.password_unlock_reason)
    var signal by remember { mutableStateOf<CancellationSignal?>(null) }
    var pending by remember { mutableStateOf<AndroidVaultCrypto.UnlockOperation?>(null) }
    var requested by remember { mutableStateOf<PasswordUnlockRequest?>(null) }
    val credential = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK && lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                // Old Android authorizes the timed Keystore key after credential confirmation.
                val operation = crypto.prepareUnlock(requireNotNull(requested))
                crypto.completeUnlock(operation)
                callback(UnlockResult.SUCCESS)
            } else callback(UnlockResult.CANCELLED)
        } catch (e: Exception) { callback(unlockFailure(e)) }
    }
    DisposableEffect(Unit) { onDispose { signal?.cancel(); pending?.input?.fill(0) } }
    return unlock@{ request ->
        requested = request
        crypto.lock()
        if (signal != null) return@unlock
        val keyguard = activity.getSystemService(KeyguardManager::class.java)
        if (!keyguard.isDeviceSecure) { callback(UnlockResult.UNAVAILABLE); return@unlock }
        if (Build.VERSION.SDK_INT < 30) {
            val intent = keyguard.createConfirmDeviceCredentialIntent(title, reason)
            if (intent != null) credential.launch(intent) else callback(UnlockResult.UNAVAILABLE)
        } else try {
            val operation = crypto.prepareUnlock(request)
            pending = operation
            val cancellation = CancellationSignal()
            signal = cancellation
            BiometricPrompt.Builder(activity).setTitle(title).setSubtitle(reason)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build().authenticate(BiometricPrompt.CryptoObject(operation.cipher), cancellation, activity.mainExecutor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            signal = null; pending = null
                            try {
                                if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) { operation.input.fill(0); callback(UnlockResult.CANCELLED); return }
                                check(result.cryptoObject?.cipher === operation.cipher)
                                crypto.completeUnlock(operation); callback(UnlockResult.SUCCESS)
                            } catch (e: Exception) { operation.input.fill(0); callback(unlockFailure(e)) }
                        }
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            android.util.Log.w("LiloPasswordAuth", "Authentication error code: $errorCode")
                            signal = null; pending = null; operation.input.fill(0)
                            callback(if (errorCode == BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED || errorCode == BiometricPrompt.BIOMETRIC_ERROR_CANCELED) UnlockResult.CANCELLED else UnlockResult.FAILED)
                        }
                    })
        } catch (e: Exception) { pending?.input?.fill(0); pending = null; signal = null; callback(unlockFailure(e)) }
    }
}

@Composable
actual fun PasswordSessionEffect() {
    val crypto = koinInject<VaultCrypto>()
    val activityLifecycle = (LocalContext.current.activity() as androidx.lifecycle.LifecycleOwner).lifecycle
    DisposableEffect(activityLifecycle) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_STOP) crypto.lock() }
        activityLifecycle.addObserver(observer)
        onDispose { activityLifecycle.removeObserver(observer) }
    }
}

private fun unlockFailure(error: Exception): UnlockResult {
    android.util.Log.w("LiloPasswordAuth", error.javaClass.simpleName)
    return when (error) {
    is android.security.keystore.KeyPermanentlyInvalidatedException,
    is com.sepideh.lilo.password.domain.VaultKeyUnavailableException -> UnlockResult.KEY_UNAVAILABLE
    is com.sepideh.lilo.password.domain.DeviceLockRequiredException -> UnlockResult.UNAVAILABLE
    else -> UnlockResult.FAILED
}
}
