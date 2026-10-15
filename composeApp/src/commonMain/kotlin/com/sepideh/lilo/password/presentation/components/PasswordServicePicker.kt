package com.sepideh.lilo.password.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.password.presentation.list.components.PasswordServiceIcon
import com.sepideh.lilo.core.presentation.SheetHeader
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.group.GroupSelectionRow
import com.sepideh.lilo.core.presentation.components.group.InlineEditorActions
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordServicePicker(state: PasswordServicesState, model: PasswordServicesViewModel, onConfirm: (String) -> Unit) {
    val colors = LiloExtendedTheme.colors
    if (state.open && !state.managing) {
        val selectedIndex = state.services.indexOfFirst { it.id == state.draftId }
        val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex.coerceAtLeast(0))
        // Also reveal the selection if services arrive after the sheet opens.
        LaunchedEffect(state.services.map { it.id }) {
            val index = state.services.indexOfFirst { it.id == state.draftId }
            if (index >= 0) listState.scrollToItem(index)
        }
        ModalBottomSheet(onDismissRequest = model::close,
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = MaterialTheme.shapes.extraLarge, containerColor = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = LiloSpacing.Screen)
                .padding(bottom = LiloSpacing.Section), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
                SheetHeader(Res.string.password_service, Res.string.confirm_action, colors.passwordColor, true, onConfirm = {
                    state.draftId?.let(onConfirm)
                    model.confirmSelection()
                })
                LazyColumn(Modifier.weight(1f, fill = false).fillMaxWidth().heightIn(max = 360.dp),
                    state = listState, verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
                    items(state.services, key = { it.id }) { service ->
                        GroupSelectionRow(service.displayName(), state.draftId == service.id, colors.passwordColor,
                            enabled = !state.busy, compact = true,
                            leadingIcon = { PasswordServiceIcon(service.id, colors.textSecondary, Modifier.size(LiloSize.SmallIcon)) }) { model.selectDraft(service.id) }
                    }
                }
                OutlinedButton(onClick = model::manage, enabled = !state.busy,
                    modifier = Modifier.fillMaxWidth().heightIn(min = LiloSize.TouchTarget), shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)) {
                    Icon(LiloIcons.Groups, null, Modifier.size(LiloSize.SmallIcon))
                    Spacer(Modifier.width(LiloSpacing.Small))
                    AppText(text = Res.string.password_services_manage, textType = TextType.Action)
                }
            }
        }
    }
    if (state.open && state.managing) {
        val sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = model::closeManagement, sheetState = sheetState,
            shape = MaterialTheme.shapes.extraLarge, containerColor = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = LiloSpacing.Screen)
                .padding(bottom = LiloSpacing.Section), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
                Row(Modifier.fillMaxWidth().padding(bottom = LiloSpacing.Item), verticalAlignment = Alignment.CenterVertically) {
                    AppText(text = Res.string.password_services_manage, textType = TextType.SectionTitle,
                        modifier = Modifier.weight(1f).padding(end = LiloSpacing.Item))
                    TextButton(onClick = model::closeManagement, enabled = !state.busy) {
                        AppText(text = Res.string.close_group_management_action, textType = TextType.Action, color = colors.passwordAction)
                    }
                }
                if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth(), color = colors.passwordColor)
                if (state.duplicateName) AppText(text = Res.string.service_name_exists, color = MaterialTheme.colorScheme.error)
                if (state.error && state.deleting == null) AppText(text = Res.string.password_service_error, color = MaterialTheme.colorScheme.error)
                LazyColumn(Modifier.weight(1f, fill = false).fillMaxWidth().heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
                    items(state.services, key = { it.id }) { service ->
                        Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = .55f))) {
                            Row(Modifier.fillMaxWidth().heightIn(min = LiloSize.TouchTarget)
                                .padding(start = 16.dp, end = LiloSpacing.Small), verticalAlignment = Alignment.CenterVertically) {
                                PasswordServiceIcon(service.id, colors.textSecondary, Modifier.size(LiloSize.SmallIcon))
                                Spacer(Modifier.width(LiloSpacing.Small))
                                AppText(text = service.displayName(), textType = TextType.BodyLarge,
                                    modifier = Modifier.weight(1f).padding(vertical = LiloSpacing.Small), maxLines = 2)
                                if (!service.isDefault) IconButton(onClick = { model.edit(service) }, enabled = !state.busy,
                                    modifier = Modifier.width(40.dp).height(48.dp)) {
                                    Icon(LiloIcons.Edit, stringResource(Res.string.password_service_edit), Modifier.size(LiloSize.SmallIcon), tint = colors.textSecondary)
                                }
                                if (!service.isDefault) IconButton(onClick = { model.askDelete(service) }, enabled = !state.busy,
                                    modifier = Modifier.width(40.dp).height(48.dp)) {
                                    Icon(LiloIcons.Delete, stringResource(Res.string.password_service_delete), Modifier.size(LiloSize.SmallIcon), tint = colors.textSecondary)
                                }
                            }
                        }
                    }
                }
                if (state.editorOpen) {
                    val focus = remember { FocusRequester() }
                    LaunchedEffect(state.editorOpen) { focus.requestFocus() }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = state.name, onValueChange = model::name, singleLine = true,
                            enabled = !state.busy, modifier = Modifier.weight(1f).focusRequester(focus),
                            label = { AppText(text = stringResource(if (state.editingId == null) Res.string.password_service_add else Res.string.password_service_edit), color = colors.textSecondary) },
                            shape = MaterialTheme.shapes.medium,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { model.save() }),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colors.passwordColor,
                                cursorColor = colors.passwordColor, focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface))
                        InlineEditorActions(state.name, state.busy, model::save, model::cancelEdit)
                    }
                } else {
                    TextButton(onClick = { model.edit() }, enabled = !state.busy) {
                        Icon(LiloIcons.Add, null, Modifier.size(LiloSize.SmallIcon), tint = colors.title)
                        Spacer(Modifier.width(LiloSpacing.Small))
                        AppText(text = Res.string.password_service_add, textType = TextType.Action, color = colors.title)
                    }
                }
            }
        }
    }
    if (state.open && state.managing && state.deleting != null) {
        PasswordServiceDeleteDialog(state, model::replacement, model::delete, model::cancelDelete)
    }
}
