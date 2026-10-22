package com.sepideh.lilo.home.presentation

import com.sepideh.lilo.password.domain.repository.PasswordRepository
import com.sepideh.lilo.password.domain.crypto.UnlockResult
import com.sepideh.lilo.password.domain.crypto.VaultCrypto

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.app.navigation.AppRoutes
import kotlinx.coroutines.Job
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.home.domain.FeatureCardFactory
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class HomeViewModel(private val featureCardFactory: FeatureCardFactory,
    private val taskMutations: TaskMutations,
    private val passwordRepository: PasswordRepository,
    private val crypto: VaultCrypto
) : BaseViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state : StateFlow<HomeState> = _state
    private val observedFeatures = mutableSetOf<LiloFeature>()


    private var passwordOpening: Job? = null

    override fun onResetState() {
        passwordOpening?.cancel()
        if (_state.value.openingPasswordId != null) crypto.lock()
        _state.update { it.copy(openingPasswordId = null, passwordRequest = null,
            passwordAuthError = null, passwordOpenFailed = false) }
    }

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        if (action !is HomeAction) return

        when (action) {
            is HomeAction.OpenPassword -> openPassword(action.id)
            is HomeAction.CompleteTask -> completeTask(action.id)
            is HomeAction.ObserveFeature -> observeFeature(action.feature)
            is HomeAction.RetryFeature -> if (action.feature in _state.value.failedFeatures) observeFeature(action.feature)
        }
    }

    private fun openPassword(id: String) {
        if (id.isBlank() || _state.value.openingPasswordId != null) return
        crypto.lock()
        _state.update { it.copy(openingPasswordId = id, passwordAuthError = null, passwordOpenFailed = false) }
        passwordOpening = viewModelScope.launch {
            try {
                val request = passwordRepository.unlockRequest(id)
                if (_state.value.openingPasswordId == id) _state.update { it.copy(passwordRequest = request) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) {
                _state.update { it.copy(openingPasswordId = null, passwordOpenFailed = true) }
            }
        }
    }

    fun passwordAuthenticated(result: UnlockResult) {
        val id = _state.value.openingPasswordId ?: return
        val session = crypto.session.value
        val authenticated = result == UnlockResult.SUCCESS && _state.value.passwordRequest?.id == id &&
            session.unlocked && session.recordId == id
        if (!authenticated) crypto.lock()
        _state.update { it.copy(openingPasswordId = null, passwordRequest = null,
            passwordAuthError = when {
                authenticated || result == UnlockResult.CANCELLED -> null
                result == UnlockResult.SUCCESS -> UnlockResult.FAILED
                else -> result
            }) }
        if (authenticated) onAction(BaseAction.OnNavigateTo(AppRoutes.Passwords.Detail(id)))
    }

    private fun completeTask(id: Long) {
        if (id in _state.value.completingTaskIds) return
        _state.update { it.copy(completingTaskIds = it.completingTaskIds + id, completionFailed = false) }
        viewModelScope.launch {
            try { taskMutations.setCompleted(id, true) }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { _state.update { it.copy(completionFailed = true) } }
            finally { _state.update { it.copy(completingTaskIds = it.completingTaskIds - id) } }
        }
    }

    private fun observeFeature(feature: LiloFeature) {
        if (!observedFeatures.add(feature)) return // already subscribed

        _state.update { it.copy(failedFeatures = it.failedFeatures - feature) }
        featureCardFactory.cardFor(feature)
            .getReportDetailStrategy()
            .observeReportDetail()
            .onEach { detail ->
                _state.update { current ->
                    current.copy(reportDetails = current.reportDetails + (feature to detail))
                }
            }
            .catch {
                observedFeatures.remove(feature)
                _state.update { it.copy(failedFeatures = it.failedFeatures + feature) }
            }
            .launchIn(viewModelScope)
    }



}