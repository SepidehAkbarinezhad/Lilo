package com.sepideh.lilo.password.presentation

import com.sepideh.lilo.password.domain.model.PasswordService
import com.sepideh.lilo.password.domain.repository.PasswordServiceRepository

import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.core.domain.validation.DuplicateNameException
import com.sepideh.lilo.password.presentation.components.PasswordServicesAction
import com.sepideh.lilo.password.presentation.components.PasswordServicesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
