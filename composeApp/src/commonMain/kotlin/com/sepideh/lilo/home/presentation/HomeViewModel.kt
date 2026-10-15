package com.sepideh.lilo.home.presentation

import androidx.lifecycle.viewModelScope
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
    private val taskMutations: TaskMutations
) : BaseViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state : StateFlow<HomeState> = _state
    private val observedFeatures = mutableSetOf<LiloFeature>()


    override fun onResetState() {

    }

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        if (action !is HomeAction) return

        when (action) {
            is HomeAction.CompleteTask -> completeTask(action.id)
            is HomeAction.ObserveFeature -> observeFeature(action.feature)
            is HomeAction.RetryFeature -> if (action.feature in _state.value.failedFeatures) observeFeature(action.feature)
        }
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