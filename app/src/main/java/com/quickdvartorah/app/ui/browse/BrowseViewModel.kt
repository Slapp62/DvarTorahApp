package com.quickdvartorah.app.ui.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickdvartorah.app.data.model.DvarTorah
import com.quickdvartorah.app.data.model.OccasionCategory
import com.quickdvartorah.app.data.model.ParshaOccasion
import com.quickdvartorah.app.data.repository.DvarTorahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed class BrowseCategoryFilter {
    data object All : BrowseCategoryFilter()
    data class Category(val category: OccasionCategory) : BrowseCategoryFilter()
    data class Occasion(val occasion: ParshaOccasion) : BrowseCategoryFilter()
}

sealed class BrowseUiState {
    data object Loading : BrowseUiState()
    data class Success(
        val items: List<DvarTorah>,
        val popularTags: List<ParshaOccasion>,
        val filter: BrowseCategoryFilter,
        val query: String
    ) : BrowseUiState()
    data class Error(val message: String) : BrowseUiState()
}

@HiltViewModel
class BrowseViewModel @Inject constructor(
    repository: DvarTorahRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow<BrowseCategoryFilter>(BrowseCategoryFilter.All)

    val uiState: StateFlow<BrowseUiState> = combine(
        repository.getPublishedDvareiTorah(),
        searchQuery,
        selectedFilter
    ) { items, query, filter ->
        val normalizedQuery = query.trim().lowercase()
        val filtered = items.filter { dvar ->
            val matchesFilter = when (filter) {
                BrowseCategoryFilter.All -> true
                is BrowseCategoryFilter.Category -> {
                    ParshaOccasion.fromKey(dvar.occasion)?.category == filter.category
                }
                is BrowseCategoryFilter.Occasion -> dvar.occasion == filter.occasion.key
            }

            val matchesQuery = normalizedQuery.isBlank() || buildString {
                append(dvar.title)
                append(' ')
                append(dvar.authorName)
                append(' ')
                append(dvar.body)
                append(' ')
                append(ParshaOccasion.fromKey(dvar.occasion)?.displayNameEn.orEmpty())
            }.lowercase().contains(normalizedQuery)

            matchesFilter && matchesQuery
        }

        val popularTags = items
            .mapNotNull { ParshaOccasion.fromKey(it.occasion) }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(8)
            .map { it.key }

        BrowseUiState.Success(
            items = filtered,
            popularTags = popularTags,
            filter = filter,
            query = query
        ) as BrowseUiState
    }.catch { emit(BrowseUiState.Error(it.message ?: "Could not load the library")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BrowseUiState.Loading)

    fun onQueryChange(query: String) {
        searchQuery.value = query
    }

    fun setFilter(filter: BrowseCategoryFilter) {
        selectedFilter.value = filter
    }
}
