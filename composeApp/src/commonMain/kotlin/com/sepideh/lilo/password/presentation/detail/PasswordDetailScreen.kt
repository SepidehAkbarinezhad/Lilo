package com.sepideh.lilo.password.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.password.presentation.components.*
import org.koin.compose.viewmodel.koinViewModel
import com.sepideh.lilo.password.presentation.detail.components.PasswordFields
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasswordDetailScreenRoot(id: String?, viewModel: PasswordDetailViewModel, onNavigateTo: (AppRoutes) -> Unit, onBack: () -> Boolean, servicesModel: PasswordServicesViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LiloExtendedTheme.colors
    val services by servicesModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(services.removedId) {
        services.removedId?.let { removed ->
            if (state.record.serviceId == removed) viewModel.edit { it.copy(serviceId = services.movedToId) }
            servicesModel.consumeRemoval()
        }
    }
    LaunchedEffect(id) { viewModel.initialize(id) }
    AppBackHandler { viewModel.back() }
    BaseRoot(viewModel = viewModel, navigateTo = onNavigateTo, onBack = onBack, bodyContainer = {
        PasswordGate(state.request, id != null && state.locked, { viewModel.back(); true }) {
            BaseFormScreen(title = if (id == null) Res.string.password_add_title else Res.string.password_edit_title,
                accent = colors.passwordColor, saveActionColor = colors.passwordAction, textSaveAction = true,
                actionLabel = Res.string.note_save_action, saveEnabled = !state.loading && !state.saving && !state.loadFailed,
                onBack = { viewModel.back(); true }, onSave = viewModel::save) { padding ->
                Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(LiloSpacing.Screen),
                    verticalArrangement = Arrangement.spacedBy(LiloSpacing.Section)) {
                    if (state.loading || state.saving) LinearProgressIndicator(Modifier.fillMaxWidth(), color = colors.passwordColor)
                    if (state.error) {
                        AppText(text = if (state.setupRequired) Res.string.password_auth_unavailable else Res.string.password_operation_error, color = MaterialTheme.colorScheme.error)
                        if (state.loadFailed) TextButton(onClick = viewModel::load) { Text(stringResource(Res.string.note_retry)) }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
                        AppText(text = stringResource(Res.string.password_service) + " *", color = colors.textSecondary)
                        OutlinedButton(onClick = { servicesModel.open(state.record.serviceId) }, enabled = !state.loading && !state.saving && !state.loadFailed,
                            modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, contentPadding = PaddingValues(18.dp)) {
                            Text(services.services.find { it.id == state.record.serviceId }?.name ?: stringResource(Res.string.password_service_select),
                                Modifier.weight(1f), color = colors.textPrimary)
                            Icon(com.sepideh.lilo.core.presentation.icons.LiloIcons.Chevron, null, tint = colors.textSecondary)
                        }
                        if (state.serviceInvalid) AppText(text = Res.string.password_service_required, color = MaterialTheme.colorScheme.error)
                    }
                    PasswordFields(state, viewModel::edit, viewModel::visibility)
                }
            }
        }
    }, dialogContent = {
        if (!state.locked) PasswordServicePicker(services, servicesModel) { id -> viewModel.edit { it.copy(serviceId = id) } }
        if (!state.locked && state.discardOpen) EditCancellationDialog(colors.passwordColor,
            onKeepEditing = viewModel::keepEditing, onDiscard = viewModel::discard)
    })
}
