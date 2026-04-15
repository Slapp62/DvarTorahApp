package com.quickdvartorah.app.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickdvartorah.app.data.model.DvarTorah
import com.quickdvartorah.app.data.model.OccasionCategory
import com.quickdvartorah.app.data.model.ParshaOccasion
import com.quickdvartorah.app.data.remote.CurrentParshaInfo
import com.quickdvartorah.app.data.remote.CurrentParshaProvider
import com.quickdvartorah.app.data.remote.ParshaSchedulePreferenceStore
import com.quickdvartorah.app.data.repository.DvarTorahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FeedParshaFilter {
    data object Current : FeedParshaFilter()
    data object Previous : FeedParshaFilter()
    data object Next : FeedParshaFilter()
    data object All : FeedParshaFilter()
    data class Category(val category: OccasionCategory) : FeedParshaFilter()
    data class Specific(val occasion: ParshaOccasion) : FeedParshaFilter()
}

sealed class FeedUiState {
    data object Loading : FeedUiState()
    data class Success(
        val items: List<DvarTorah>,
        val activeFilter: FeedParshaFilter,
        val activeOccasion: ParshaOccasion?
    ) : FeedUiState()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FeedUiState> = combine(
        _selectedFilter,
        _previousParsha,
        _currentParsha,
        _nextParsha,
        repository.getPublishedDvareiTorah()
    ) { filter, previous, current, next, allItems ->
        resolveFeedState(
            selectedFilter = filter,
            previousOccasion = previous?.occasion,
            currentOccasion = current?.occasion,
            nextOccasion = next?.occasion,
            allItems = allItems
        )
    }
        .catch { e ->
            emit(FeedUiState.Error(e.message ?: "Could not load Divrei Torah"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FeedUiState.Loading)

    fun setFilter(filter: FeedParshaFilter) {
        _selectedFilter.value = filter
    }

    private fun resolveFeedState(
        selectedFilter: FeedParshaFilter,
        previousOccasion: ParshaOccasion?,
        currentOccasion: ParshaOccasion?,
        nextOccasion: ParshaOccasion?,
        allItems: List<DvarTorah>
    ): FeedUiState {
        fun itemsForOccasion(occasion: ParshaOccasion?): List<DvarTorah> {
            if (occasion == null) return emptyList()
            return allItems.filter { it.occasion == occasion.key }
        }

        return when (selectedFilter) {
            FeedParshaFilter.All -> FeedUiState.Success(
                items = allItems,
                activeFilter = FeedParshaFilter.All,
                activeOccasion = null
            )

            FeedParshaFilter.Current -> {
                val currentItems = itemsForOccasion(currentOccasion)
                if (currentItems.isNotEmpty()) {
                    FeedUiState.Success(
                        items = currentItems,
                        activeFilter = FeedParshaFilter.Current,
                        activeOccasion = currentOccasion
                    )
                } else {
                    val nextItems = itemsForOccasion(nextOccasion)
                    if (nextItems.isNotEmpty()) {
                        FeedUiState.Success(
                            items = nextItems,
                            activeFilter = FeedParshaFilter.Next,
                            activeOccasion = nextOccasion
                        )
                    } else {
                        FeedUiState.Success(
                            items = allItems,
                            activeFilter = FeedParshaFilter.All,
                            activeOccasion = null
                        )
                    }
                }
            }

            FeedParshaFilter.Previous -> FeedUiState.Success(
                items = itemsForOccasion(previousOccasion),
                activeFilter = FeedParshaFilter.Previous,
                activeOccasion = previousOccasion
            )

            FeedParshaFilter.Next -> FeedUiState.Success(
                items = itemsForOccasion(nextOccasion),
                activeFilter = FeedParshaFilter.Next,
                activeOccasion = nextOccasion
            )

            is FeedParshaFilter.Category -> FeedUiState.Success(
                items = allItems.filter { dvar ->
                    ParshaOccasion.fromKey(dvar.occasion)?.category == selectedFilter.category
                },
                activeFilter = selectedFilter,
                activeOccasion = null
            )

            is FeedParshaFilter.Specific -> FeedUiState.Success(
                items = itemsForOccasion(selectedFilter.occasion),
                activeFilter = selectedFilter,
                activeOccasion = selectedFilter.occasion
            )
        }
    }
}
