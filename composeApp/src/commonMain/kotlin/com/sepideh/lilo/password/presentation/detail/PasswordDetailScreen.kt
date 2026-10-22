package com.sepideh.lilo.password.presentation.detail

import com.sepideh.lilo.password.presentation.components.displayName

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.password.presentation.components.*
import org.koin.compose.viewmodel.koinViewModel
import com.sepideh.lilo.password.presentation.detail.components.PasswordFields
import com.sepideh.lilo.password.presentation.detail.components.PasswordServiceSelector
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasswordDetailScreenRoot(id: String?, viewModel: PasswordDetailViewModel, onNavigateTo: (AppRoutes) -> Unit, onBack: () -> Boolean, servicesModel: PasswordServicesViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val services by servicesModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(services.removedId) {
        services.removedId?.let { removed ->
            if (state.record.serviceId == removed) viewModel.onAction(PasswordDetailAction.OnServiceSelected(services.movedToId))
            servicesModel.onAction(PasswordServicesAction.OnConsumeRemoval)
        }
    }
    LaunchedEffect(id) { viewModel.onAction(PasswordDetailAction.OnInitialize(id)) }
    AppBackHandler { viewModel.onAction(PasswordDetailAction.Back) }
    BaseRoot(viewModel = viewModel, navigateTo = onNavigateTo, onBack = onBack,
        bodyContainer = {
            PasswordDetailScreen(state.copy(isEditing = id != null, services = services)) { action ->
                when (action) {
                    is PasswordServicesAction -> servicesModel.onAction(action)
                    else -> viewModel.onAction(action)
                }
            }
        })
}

@Composable
fun PasswordDetailScreen(state: PasswordDetailState, onAction: (BaseAction) -> Unit) {
    val colors = LiloExtendedTheme.colors
    if (state.isEditing && state.locked && !state.loadFailed) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {}
    } else {
        BaseFormScreen(title = if (!state.isEditing) Res.string.password_add_title else Res.string.password_edit_title,
            accent = colors.passwordColor, saveActionColor = colors.passwordAction, textSaveAction = true,
            actionLabel = Res.string.note_save_action, saveEnabled = !state.locked && !state.loading && !state.saving && !state.loadFailed,
            onBack = { onAction(PasswordDetailAction.Back); true }, onSave = { onAction(PasswordDetailAction.OnSave) }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(LiloSpacing.Screen),
                verticalArrangement = Arrangement.spacedBy(LiloSpacing.Section)) {
                if (state.loading || state.saving) LinearProgressIndicator(Modifier.fillMaxWidth(), color = colors.passwordColor)
                if (state.error) {
                    AppText(text = if (state.setupRequired) Res.string.password_auth_unavailable else Res.string.password_operation_error, color = MaterialTheme.colorScheme.error)
                    if (state.loadFailed) TextButton(onClick = { onAction(PasswordDetailAction.Retry) }) { Text(stringResource(Res.string.note_retry)) }
                }
                PasswordServiceSelector(
                    selectedId = state.record.serviceId,
                    selectedName = state.services.services.find { it.id == state.record.serviceId }?.displayName(),
                    enabled = !state.locked && !state.loading && !state.saving && !state.loadFailed,
                    isError = state.serviceInvalid,
                    onClick = { onAction(PasswordServicesAction.OnOpen(state.record.serviceId)) },
                )
                PasswordFields(state, onAction)
            }
        }
    }

    if (!state.locked) PasswordServicePicker(state.services, onAction) { id ->
        onAction(PasswordDetailAction.OnServiceSelected(id))
    }
    if (!state.locked && state.discardOpen) EditCancellationDialog(colors.passwordColor,
        onKeepEditing = { onAction(PasswordDetailAction.KeepEditing) },
        onDiscard = { onAction(PasswordDetailAction.Discard) })
}

@AppPreviews
@Composable
private fun PasswordDetailPreview() {
    LiloPreviewWrapper {
        PasswordDetailScreen(PasswordDetailState(locked = false, isEditing = true,
            record = com.sepideh.lilo.password.domain.model.Password(id = "sample", title = "Personal email",
                username = "hello@example.com", secret = "sample-password", website = "example.com", serviceId = "google"),
            services = PasswordServicesState(loaded = true, services = listOf(
                com.sepideh.lilo.password.domain.model.PasswordService("google", "Google", true),
            ))), {})
    }
}

@AppPreviews
@Composable
private fun PasswordDetailNewPreview() {
    LiloPreviewWrapper { PasswordDetailScreen(PasswordDetailState(locked = false, visible = true), {}) }
}
