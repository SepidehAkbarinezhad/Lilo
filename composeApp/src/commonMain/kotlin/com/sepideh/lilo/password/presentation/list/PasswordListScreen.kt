package com.sepideh.lilo.password.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.gesture.SwipeToRevealDelete
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.password.domain.model.matching
import com.sepideh.lilo.password.presentation.components.PasswordServicesViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.sepideh.lilo.password.presentation.rememberPasswordUnlocker
import com.sepideh.lilo.password.domain.crypto.UnlockResult
import com.sepideh.lilo.password.presentation.list.components.PasswordListItem
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasswordListScreenRoot(viewModel: PasswordListViewModel, onNavigateTo: (AppRoutes) -> Unit, onBack: () -> Boolean, servicesModel: PasswordServicesViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val services by servicesModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(services.loaded, services.services) {
        if (services.loaded) viewModel.onAction(PasswordListAction.OnServicesChanged(services.services.map { it.id }.toSet()))
    }
    val unlock = rememberPasswordUnlocker { viewModel.onAction(PasswordListAction.OnAuthenticated(it)) }
    LaunchedEffect(state.request) { state.request?.let(unlock) }
    BaseRoot(viewModel = viewModel, navigateTo = onNavigateTo, onBack = onBack,
        bodyContainer = { PasswordListScreen(state, viewModel::onAction) })
}

@Composable
fun PasswordListScreen(state: PasswordListState, onAction: (BaseAction) -> Unit) {
    val colors = LiloExtendedTheme.colors
    BaseListScreen(title = Res.string.password_feature_title, accent = colors.passwordColor, referenceStyle = true,
        searchVisible = state.searchVisible, query = state.query, searchHint = Res.string.password_search,
        filtersActive = state.alphabetical || state.serviceId != null, onBack = { onAction(BaseAction.OnNavigateTo(null)); true }, onSearchVisible = { onAction(PasswordListAction.OnSearchChanged(it)) },
        onQueryChange = { onAction(PasswordListAction.OnQueryChanged(it)) }, onFilter = null,
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(BaseAction.OnNavigateTo(AppRoutes.Passwords.Detail(null))) }, modifier = Modifier.size(64.dp),
                shape = CircleShape, containerColor = colors.passwordColor, contentColor = colors.onPasswordAccent) {
                Icon(LiloIcons.Add, stringResource(Res.string.password_add_title), Modifier.size(32.dp))
            }
        }) { padding ->
        val records = state.records.filter { state.serviceId == null || it.serviceId == state.serviceId }.matching(state.query, state.alphabetical)
        Column(Modifier.fillMaxSize().padding(padding)) {
            state.authError?.let { error ->
                AppText(text = when (error) {
                    UnlockResult.UNAVAILABLE -> Res.string.password_auth_unavailable
                    UnlockResult.KEY_UNAVAILABLE -> Res.string.password_key_unavailable
                    else -> Res.string.password_unlock_failed
                }, modifier = Modifier.padding(20.dp), color = MaterialTheme.colorScheme.error)
            }
            if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = colors.passwordColor)
            if (state.error) {
                AppText(text = Res.string.password_operation_error, modifier = Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { onAction(PasswordListAction.Retry) }) { Text(stringResource(Res.string.note_retry)) }
            }
            if (!state.loading && !state.error && records.isEmpty()) ListEmptyState(
                if (state.query.isBlank() && state.serviceId == null) Res.string.password_empty else Res.string.password_no_results, colors.passwordColor, icon = com.sepideh.lilo.ui.icons.PasswordCardIcon)
            else LazyColumn(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(records, key = { it.id }) { record ->
                    SwipeToRevealDelete(enabled = true, onDelete = { onAction(PasswordListAction.OnDeleteSelected(record.id)) }) {
                        PasswordListItem(record, revealed = state.revealedSecret.takeIf { state.revealedId == record.id },
                            busy = state.busyId == record.id, copied = state.copiedId == record.id,
                            onCopy = { onAction(PasswordListAction.OnCopy(record.id)) },
                            onReveal = { onAction(PasswordListAction.OnReveal(record.id)) }, onClick = { onAction(PasswordListAction.OnEdit(record.id)) })
                    }
                }
            }
        }
    }

    if (state.deleteId != null) DeleteConfirmationDialog(colors.passwordColor,
        { onAction(PasswordListAction.OnDeleteConfirm) },
        { onAction(PasswordListAction.OnDeleteSelected(null)) },
        title = stringResource(Res.string.password_delete_title), message = stringResource(Res.string.delete_item_confirmation))
}

@AppPreviews
@Composable
private fun PasswordListPreview() {
    LiloPreviewWrapper {
        PasswordListScreen(PasswordListState(loading = false, records = listOf(
            com.sepideh.lilo.password.domain.model.Password(id = "sample", title = "Personal email",
                username = "hello@example.com", serviceId = "google", serviceName = "Google"),
        )), {})
    }
}

@AppPreviews
@Composable
private fun PasswordListEmptyPreview() {
    LiloPreviewWrapper { PasswordListScreen(PasswordListState(loading = false), {}) }
}
