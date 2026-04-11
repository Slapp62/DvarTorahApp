package com.example.dvartorahapp.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dvartorahapp.data.model.DvarTorah
import com.example.dvartorahapp.data.model.ParshaOccasion
import com.example.dvartorahapp.data.remote.CurrentParshaInfo
import com.example.dvartorahapp.data.remote.CurrentParshaProvider
import com.example.dvartorahapp.data.remote.ParshaSchedulePreferenceStore
import com.example.dvartorahapp.data.repository.DvarTorahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed class FeedUiState {
    object Loading : FeedUiState()
    data class Success(val items: List<DvarTorah>, val activeFilter: ParshaOccasion?) : FeedUiState()
    data class Error(val message: String) : FeedUiState()
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: DvarTorahRepository,
    private val currentParshaProvider: CurrentParshaProvider,
    private val schedulePreferenceStore: ParshaSchedulePreferenceStore
) : ViewModel() {

    private val _activeFilter = MutableStateFlow<ParshaOccasion?>(null)
    val activeFilter: StateFlow<ParshaOccasion?> = _activeFilter.asStateFlow()

    private val _currentParsha = MutableStateFlow<CurrentParshaInfo?>(null)
    val currentParsha: StateFlow<CurrentParshaInfo?> = _currentParsha.asStateFlow()

    private var defaultApplied = false

    init {
        viewModelScope.launch {
            schedulePreferenceStore.modeFlow().collect { mode ->
                runCatching { currentParshaProvider.getCurrentParsha(mode) }
                    .onSuccess { info ->
                        _currentParsha.value = info
                        if (info != null) {
                            _activeFilter.value = info.occasion
                            defaultApplied = true
                        }
                    }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<FeedUiState> = _activeFilter
        .flatMapLatest { filter ->
            repository.getPublishedDvareiTorah(filter?.key)
                .timeout(15.seconds)
                .map<List<DvarTorah>, FeedUiState> { FeedUiState.Success(it, filter) }
                .catch { e ->
                    if (e is TimeoutCancellationException) {
                        emit(FeedUiState.Error("Could not connect to server. Check your internet connection."))
                    } else {
                        emit(FeedUiState.Error(e.message ?: "Failed to load"))
                    }
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FeedUiState.Loading)

    fun setFilter(occasion: ParshaOccasion?) {
        defaultApplied = true
        _activeFilter.value = occasion
    }
}
