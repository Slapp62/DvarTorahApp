package com.example.dvartorahapp.ui.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dvartorahapp.data.model.DvarTorah
import com.example.dvartorahapp.data.model.ParshaOccasion
import com.example.dvartorahapp.data.remote.CurrentParshaProvider
import com.example.dvartorahapp.data.remote.ParshaSchedulePreferenceStore
import com.example.dvartorahapp.data.repository.DvarTorahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WriteUiEffect {
    object NavigateBack : WriteUiEffect()
    data class ShowError(val message: String) : WriteUiEffect()
}

@HiltViewModel
class WriteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DvarTorahRepository,
    private val currentParshaProvider: CurrentParshaProvider,
    private val schedulePreferenceStore: ParshaSchedulePreferenceStore
) : ViewModel() {

    private val editDvarId: String? = savedStateHandle["dvarId"]

    var title by mutableStateOf("")
    var selectedOccasion by mutableStateOf<ParshaOccasion?>(null)
    var body by mutableStateOf("")
    var sources by mutableStateOf("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _effect = Channel<WriteUiEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        if (editDvarId != null) {
            viewModelScope.launch {
                repository.getDvarTorahById(editDvarId).firstOrNull()?.let { dvar ->
                    title = dvar.title
                    selectedOccasion = dvar.parshaOccasion
                    body = dvar.body
                    sources = dvar.sources
                }
            }
        } else {
            viewModelScope.launch {
                selectedOccasion = currentParshaProvider.getCurrentParsha(schedulePreferenceStore.getMode())?.occasion
            }
        }
    }

    fun submit(authorUid: String, authorName: String) {
        if (title.isBlank()) {
            viewModelScope.launch { _effect.send(WriteUiEffect.ShowError("Please enter a title")) }
            return
        }
        if (selectedOccasion == null) {
            viewModelScope.launch { _effect.send(WriteUiEffect.ShowError("Please select a Parsha or occasion")) }
            return
        }
        if (body.isBlank()) {
            viewModelScope.launch { _effect.send(WriteUiEffect.ShowError("Please write the body of your Dvar Torah")) }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = if (editDvarId != null) {
                val dvar = DvarTorah(
                    id = editDvarId,
                    title = title.trim(),
                    occasion = selectedOccasion!!.key,
                    authorUid = authorUid,
                    authorName = authorName,
                    body = body.trim(),
                    sources = sources.trim()
                )
                repository.updateDvarTorah(dvar)
            } else {
                val dvar = DvarTorah(
                    title = title.trim(),
                    occasion = selectedOccasion!!.key,
                    authorUid = authorUid,
                    authorName = authorName,
                    body = body.trim(),
                    sources = sources.trim(),
                    status = "published"
                )
                repository.createDvarTorah(dvar).map {}
            }

            result.fold(
                onSuccess = { _effect.send(WriteUiEffect.NavigateBack) },
                onFailure = { _effect.send(WriteUiEffect.ShowError(it.message ?: "Failed to save")) }
            )
            _isLoading.value = false
        }
    }
}
