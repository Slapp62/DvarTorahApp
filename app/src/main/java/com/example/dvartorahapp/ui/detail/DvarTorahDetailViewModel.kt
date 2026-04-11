package com.example.dvartorahapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dvartorahapp.data.model.DvarTorah
import com.example.dvartorahapp.data.repository.DvarTorahRepository
import com.example.dvartorahapp.data.repository.ReportRepository
import com.example.dvartorahapp.data.model.Report
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val dvarTorah: DvarTorah) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

sealed class DetailUiEffect {
    data class ShowMessage(val message: String) : DetailUiEffect()
}

@HiltViewModel
class DvarTorahDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dvarTorahRepository: DvarTorahRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val dvarId: String = checkNotNull(savedStateHandle["dvarId"])

    val uiState: StateFlow<DetailUiState> = dvarTorahRepository.getDvarTorahById(dvarId)
        .map<DvarTorah?, DetailUiState> { dvar ->
            if (dvar != null) DetailUiState.Success(dvar) else DetailUiState.Error("Not found")
        }
        .catch { emit(DetailUiState.Error(it.message ?: "Failed to load")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailUiState.Loading)

    private val _effect = Channel<DetailUiEffect>()
    val effect = _effect.receiveAsFlow()

    fun toggleLike(uid: String) {
        viewModelScope.launch {
            dvarTorahRepository.toggleLike(dvarId, uid).onFailure {
                _effect.send(DetailUiEffect.ShowMessage("Failed to update like"))
            }
        }
    }

    fun getUserLikedStatus(uid: String): Flow<Boolean> =
        dvarTorahRepository.getUserLikedStatus(dvarId, uid)

    fun submitReport(reporterUid: String, reason: String) {
        viewModelScope.launch {
            val report = Report(dvarId = dvarId, reporterUid = reporterUid, reason = reason)
            reportRepository.submitReport(report).fold(
                onSuccess = { _effect.send(DetailUiEffect.ShowMessage("Report submitted. Thank you.")) },
                onFailure = { _effect.send(DetailUiEffect.ShowMessage("Failed to submit report")) }
            )
        }
    }
}
