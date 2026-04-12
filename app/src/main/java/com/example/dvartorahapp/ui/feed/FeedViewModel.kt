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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FeedParshaFilter {
    data object Current : FeedParshaFilter()
    data object Previous : FeedParshaFilter()
    data object Next : FeedParshaFilter()
    data object All : FeedParshaFilter()
    data class Specific(val occasion: ParshaOccasion) : FeedParshaFilter()
}

sealed class FeedUiState {
    data object Loading : FeedUiState()
    data class Success(val items: List<DvarTorah>, val activeFilter: ParshaOccasion?) : FeedUiState()
    data class Error(val message: String) : FeedUiState()
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: DvarTorahRepository,
    private val currentParshaProvider: CurrentParshaProvider,
    private val schedulePreferenceStore: ParshaSchedulePreferenceStore
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<FeedParshaFilter>(FeedParshaFilter.Current)
    val selectedFilter: StateFlow<FeedParshaFilter> = _selectedFilter.asStateFlow()

    private val _currentParsha = MutableStateFlow<CurrentParshaInfo?>(null)
    val currentParsha: StateFlow<CurrentParshaInfo?> = _currentParsha.asStateFlow()

    private val _previousParsha = MutableStateFlow<CurrentParshaInfo?>(null)
    val previousParsha: StateFlow<CurrentParshaInfo?> = _previousParsha.asStateFlow()

    private val _nextParsha = MutableStateFlow<CurrentParshaInfo?>(null)
    val nextParsha: StateFlow<CurrentParshaInfo?> = _nextParsha.asStateFlow()

    init {
        viewModelScope.launch {
            schedulePreferenceStore.modeFlow().collect { mode ->
                runCatching {
                    Triple(
                        currentParshaProvider.getParshaForShabbatOffset(mode, -1),
                        currentParshaProvider.getParshaForShabbatOffset(mode, 0),
                        currentParshaProvider.getParshaForShabbatOffset(mode, 1)
                    )
                }.onSuccess { (previous, current, next) ->
                    _previousParsha.value = previous
                    _currentParsha.value = current
                    _nextParsha.value = next
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<FeedUiState> = combine(
        _selectedFilter,
        _previousParsha,
        _currentParsha
        ,
        _nextParsha
    ) { filter, previous, current, next ->
        when (filter) {
            FeedParshaFilter.All -> null
            FeedParshaFilter.Previous -> previous?.occasion
            FeedParshaFilter.Current -> current?.occasion
            FeedParshaFilter.Next -> next?.occasion
            is FeedParshaFilter.Specific -> filter.occasion
        }
    }.flatMapLatest { occasion ->
            repository.getPublishedDvareiTorah(occasion?.key)
                .map<List<DvarTorah>, FeedUiState> { FeedUiState.Success(it, occasion) }
                .catch { e ->
                    emit(FeedUiState.Error(e.message ?: "Could not load Divrei Torah"))
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FeedUiState.Loading)

    fun setFilter(filter: FeedParshaFilter) {
        _selectedFilter.value = filter
    }
}
