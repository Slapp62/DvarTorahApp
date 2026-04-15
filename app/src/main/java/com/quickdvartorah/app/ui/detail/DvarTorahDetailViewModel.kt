package com.quickdvartorah.app.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickdvartorah.app.data.model.DvarTorah
import com.quickdvartorah.app.data.repository.DvarTorahRepository
import com.quickdvartorah.app.data.repository.ReportRepository
import com.quickdvartorah.app.data.model.Report
import com.quickdvartorah.app.data.model.UserProfile
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
            if (dvar != null) DetailUiState.Success(dvar) else DetailUiState.Error("Dvar Torah not found")
        }
        .catch { emit(DetailUiState.Error(it.message ?: "Could not load Dvar Torah")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailUiState.Loading)

    private val _effect = Channel<DetailUiEffect>()
    val effect = _effect.receiveAsFlow()

    fun toggleLike(uid: String) {
        viewModelScope.launch {
            dvarTorahRepository.toggleLike(dvarId, uid).onFailure {
                _effect.send(DetailUiEffect.ShowMessage("Could not update like"))
            }
        }
    }

    fun getUserLikedStatus(uid: String): Flow<Boolean> =
        dvarTorahRepository.getUserLikedStatus(dvarId, uid)

    fun submitReport(reporter: UserProfile, reason: String) {
        viewModelScope.launch {
            val currentDvar = (uiState.value as? DetailUiState.Success)?.dvarTorah
            val bodyPreview = currentDvar?.body
                ?.replace("\\s+".toRegex(), " ")
                ?.trim()
                ?.take(220)
                .orEmpty()
            val report = Report(
                dvarId = dvarId,
                reporterUid = reporter.uid,
                reporterName = reporter.displayName,
                reporterEmail = reporter.email,
                reason = reason,
                dvarTitle = currentDvar?.title.orEmpty(),
                dvarAuthorUid = currentDvar?.authorUid.orEmpty(),
                dvarAuthorName = currentDvar?.authorName.orEmpty(),
                dvarOccasion = currentDvar?.occasion.orEmpty(),
                dvarBodyPreview = bodyPreview,
                dvarStatus = currentDvar?.status ?: "published"
            )
            reportRepository.submitReport(report).fold(
                onSuccess = { _effect.send(DetailUiEffect.ShowMessage("Report submitted")) },
                onFailure = { _effect.send(DetailUiEffect.ShowMessage("Could not submit report")) }
            )
        }
    }
}
