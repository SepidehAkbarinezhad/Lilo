package com.sepideh.lilo.password.presentation

import com.sepideh.lilo.password.domain.model.PasswordService
import com.sepideh.lilo.password.domain.repository.PasswordServiceRepository

import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.core.domain.validation.DuplicateNameException
import com.sepideh.lilo.password.presentation.components.PasswordServicesAction
import com.sepideh.lilo.password.presentation.components.PasswordServicesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PasswordServicesViewModelTest {
    private val store = ViewModelStore()
    @BeforeTest fun setup() { Dispatchers.setMain(StandardTestDispatcher()) }
    @AfterTest fun cleanup() { store.clear(); Dispatchers.resetMain() }

    @Test fun duplicateKeepsEditorAndSelectionThenValidRetrySucceeds() = runTest {
        val model = PasswordServicesViewModel(DuplicateRepository())
        store.put("services", model)
        model.onAction(PasswordServicesAction.OnOpen("google"))
        model.onAction(PasswordServicesAction.OnManage)
        model.onAction(PasswordServicesAction.OnEdit())
        model.onAction(PasswordServicesAction.OnNameChanged("Gmail"))
        model.onAction(PasswordServicesAction.OnSave)
        runCurrent()
        assertTrue(model.state.value.duplicateName)
        assertTrue(model.state.value.editorOpen)
        assertEquals("Gmail", model.state.value.name)
        assertEquals("google", model.state.value.draftId)
        assertFalse(model.state.value.busy)
        model.onAction(PasswordServicesAction.OnNameChanged("Work"))
        assertFalse(model.state.value.duplicateName)
        model.onAction(PasswordServicesAction.OnSave)
        runCurrent()
        assertFalse(model.state.value.editorOpen)
        assertEquals("new", model.state.value.draftId)
        assertEquals("new", model.state.value.createdId)
        assertFalse(model.state.value.open)
        assertFalse(model.state.value.managing)
        model.onAction(PasswordServicesAction.OnConsumeCreation)
        assertNull(model.state.value.createdId)
    }

    @Test fun renamePreservesSelectedIdAndStaysInManagement() = runTest {
        val repo = Services()
        val model = managed(repo)
        for (id in listOf("custom", "other")) {
            model.onAction(PasswordServicesAction.OnEdit(repo.rows.value.first { it.id == id }))
            model.onAction(PasswordServicesAction.OnNameChanged("ZZZ $id"))
            model.onAction(PasswordServicesAction.OnSave)
            runCurrent()
            assertTrue(model.state.value.open)
            assertTrue(model.state.value.managing)
            assertFalse(model.state.value.editorOpen)
            assertEquals("custom", model.state.value.draftId)
            assertNull(model.state.value.createdId)
            assertEquals("ZZZ $id", model.state.value.services.first { it.id == id }.name)
        }
    }

    @Test fun deleteUsedSelectionRequiresReplacementAndStaysInManagement() = runTest {
        val repo = Services().apply { usageCount = 2 }
        val model = managed(repo)
        model.onAction(PasswordServicesAction.OnDeleteSelected(repo.rows.value.first { it.id == "custom" }))
        runCurrent()
        assertEquals(2, model.state.value.usage)
        model.onAction(PasswordServicesAction.OnDeleteConfirm)
        runCurrent()
        assertTrue(model.state.value.error)
        assertNotNull(model.state.value.deleting)
        assertEquals("custom", model.state.value.draftId)
        model.onAction(PasswordServicesAction.OnReplacementSelected("google"))
        model.onAction(PasswordServicesAction.OnDeleteConfirm)
        runCurrent()
        assertEquals("google", repo.lastReplacement)
        assertEquals("google", model.state.value.draftId)
        assertEquals("custom", model.state.value.removedId)
        assertEquals("google", model.state.value.movedToId)
        assertTrue(model.state.value.open)
        assertTrue(model.state.value.managing)
        assertNull(model.state.value.deleting)
        model.onAction(PasswordServicesAction.OnConsumeRemoval)
        assertNull(model.state.value.removedId)
    }

    @Test fun deletingUnselectedServiceKeepsSelectionAndDeletingUnusedSelectionClearsIt() = runTest {
        val repo = Services()
        val model = managed(repo)
        for (id in listOf("other", "custom")) {
            model.onAction(PasswordServicesAction.OnDeleteSelected(repo.rows.value.first { it.id == id }))
            runCurrent()
            model.onAction(PasswordServicesAction.OnDeleteConfirm)
            runCurrent()
            assertEquals(if (id == "other") "custom" else null, model.state.value.draftId)
            assertTrue(model.state.value.open)
            assertTrue(model.state.value.managing)
        }
    }

    @Test fun closeManagementClosesWholeSheetAndReopensInSelectionMode() = runTest {
        val model = managed(Services())
        model.onAction(PasswordServicesAction.OnCloseManagement)
        assertFalse(model.state.value.open)
        assertFalse(model.state.value.managing)
        model.onAction(PasswordServicesAction.OnOpen("custom"))
        assertTrue(model.state.value.open)
        assertFalse(model.state.value.managing)
        assertEquals("custom", model.state.value.draftId)
    }

    @Test fun addWaitsForPersistenceBeforeClosingAndPublishingSelection() = runTest {
        val repo = Services().apply { gate = CompletableDeferred() }
        val model = managed(repo)
        model.onAction(PasswordServicesAction.OnEdit())
        model.onAction(PasswordServicesAction.OnNameChanged("New"))
        model.onAction(PasswordServicesAction.OnSave)
        runCurrent()
        model.onAction(PasswordServicesAction.OnCloseManagement)
        assertTrue(model.state.value.open)
        assertTrue(model.state.value.busy)
        assertNull(model.state.value.createdId)
        repo.gate!!.complete(Unit)
        runCurrent()
        assertFalse(model.state.value.open)
        assertFalse(model.state.value.managing)
        assertFalse(model.state.value.busy)
        assertEquals("created", model.state.value.createdId)
        assertEquals("created", model.state.value.draftId)
    }

    private fun managed(repo: Services) = PasswordServicesViewModel(repo).also {
        store.put("services", it)
        it.onAction(PasswordServicesAction.OnOpen("custom"))
        it.onAction(PasswordServicesAction.OnManage)
    }

    private class Services : PasswordServiceRepository {
        val rows = MutableStateFlow(listOf(PasswordService("google", "Gmail", true),
            PasswordService("custom", "Original"), PasswordService("other", "Other")))
        var gate: CompletableDeferred<Unit>? = null
        var usageCount = 0
        var lastReplacement: String? = null
        override fun observe() = rows
        override suspend fun create(name: String): PasswordService {
            gate?.await()
            return PasswordService("created", name).also { rows.value += it }
        }
        override suspend fun rename(id: String, name: String) {
            rows.value = rows.value.map { if (it.id == id) it.copy(name = name) else it }
        }
        override suspend fun usage(id: String) = usageCount
        override suspend fun delete(id: String, replacementId: String?) {
            require(usageCount == 0 || replacementId != null)
            lastReplacement = replacementId
            rows.value = rows.value.filterNot { it.id == id }
        }
    }

    private class DuplicateRepository : PasswordServiceRepository {
        override fun observe() = flowOf(listOf(PasswordService("google", "Gmail", true)))
        override suspend fun create(name: String): PasswordService {
            if (name == "Gmail") throw DuplicateNameException()
            return PasswordService("new", name)
        }
        override suspend fun rename(id: String, name: String) = Unit
        override suspend fun usage(id: String) = 0
        override suspend fun delete(id: String, replacementId: String?) = Unit
    }
}
