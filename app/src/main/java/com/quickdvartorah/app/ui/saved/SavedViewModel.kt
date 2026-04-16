package com.quickdvartorah.app.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickdvartorah.app.data.model.DvarTorah
import com.quickdvartorah.app.data.model.OccasionCategory
import com.quickdvartorah.app.data.repository.AuthRepository
import com.quickdvartorah.app.data.repository.DvarTorahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

sealed class SavedFilter {
    data object All : SavedFilter()
    data object Parsha : SavedFilter()
    data object YomTov : SavedFilter()
    data object Occasion : SavedFilter()
}

sealed class SavedUiState {
    data object Loading : SavedUiState()
    data object SignedOut : SavedUiState()
    data class Empty(val filter: SavedFilter) : SavedUiState()
    data class Success(
        val items: List<DvarTorah>,
        val filter: SavedFilter
    ) : SavedUiState()
    data class Error(val message: String) : SavedUiState()
}

@HiltViewModel
class SavedViewModel @Inject constructor(
    authRepository: AuthRepository,
    dvarTorahRepository: DvarTorahRepository
) : ViewModel() {

    private val selectedFilter = kotlinx.coroutines.flow.MutableStateFlow<SavedFilter>(SavedFilter.All)
    val filter: StateFlow<SavedFilter> = selectedFilter.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        SavedFilter.All
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SavedUiState> = authRepository.authStateFlow()
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(SavedUiState.SignedOut)
            } else {
                dvarTorahRepository.getLikedDvareiTorah(user.uid).combine(selectedFilter) { items, currentFilter ->
                    val filtered = items.filter { dvar ->
                        when (currentFilter) {
                            SavedFilter.All -> true
                            SavedFilter.Parsha -> dvar.parshaOccasion?.category == OccasionCategory.PARSHA
                            SavedFilter.YomTov -> dvar.parshaOccasion?.category == OccasionCategory.YOM_TOV
                            SavedFilter.Occasion -> dvar.parshaOccasion?.category == OccasionCategory.SPECIAL_OCCASION
                        }
                    }

                    if (filtered.isEmpty()) {
                        SavedUiState.Empty(currentFilter)
                    } else {
                        SavedUiState.Success(filtered, currentFilter)
                    }
                }
            }
        }
        .catch { emit(SavedUiState.Error(it.message ?: "Could not load saved insights")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SavedUiState.Loading)

    fun setFilter(filter: SavedFilter) {
        selectedFilter.value = filter
    }
}
