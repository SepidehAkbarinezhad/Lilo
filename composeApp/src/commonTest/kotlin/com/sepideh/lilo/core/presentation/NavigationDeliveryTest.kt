package com.sepideh.lilo.core.presentation

import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationDeliveryTest {
    private val store = ViewModelStore()
    @BeforeTest fun setUp() { Dispatchers.setMain(StandardTestDispatcher()) }
    @AfterTest fun tearDown() { store.clear(); Dispatchers.resetMain() }

    @Test fun navigationWaitsForAResumedScreenAndIsConsumedOnce() = runTest {
        val model = object : BaseViewModel() { override fun onResetState() = Unit }
        store.put("navigation", model)
        // A save can finish while BaseRoot's STARTED collector is stopped.
        model.onAction(BaseAction.OnNavigateTo(null))
        runCurrent()
        val delivered = withTimeoutOrNull(1000) { model.baseOneTimeEvents.first() }
        assertEquals(true, delivered?.navigateBack)
        // Re-subscribing after another lifecycle pause must not navigate twice.
        assertNull(withTimeoutOrNull(1000) { model.baseOneTimeEvents.first() })
    }
}
