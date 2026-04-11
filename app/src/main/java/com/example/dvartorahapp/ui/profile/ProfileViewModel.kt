package com.example.dvartorahapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dvartorahapp.data.model.DvarTorah
import com.example.dvartorahapp.data.model.WriterApplication
import com.example.dvartorahapp.data.remote.ParshaScheduleMode
import com.example.dvartorahapp.data.remote.ParshaSchedulePreferenceStore
import com.example.dvartorahapp.data.repository.ApplicationRepository
import com.example.dvartorahapp.data.repository.AuthRepository
import com.example.dvartorahapp.data.repository.DvarTorahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dvarTorahRepository: DvarTorahRepository,
    private val applicationRepository: ApplicationRepository,
    private val schedulePreferenceStore: ParshaSchedulePreferenceStore
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val userDvareiTorah: StateFlow<List<DvarTorah>> = authRepository.authStateFlow()
        .flatMapLatest { user ->
            if (user != null) dvarTorahRepository.getUserDvareiTorah(user.uid)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val userApplication: StateFlow<WriterApplication?> = authRepository.authStateFlow()
        .flatMapLatest { user ->
            if (user != null) applicationRepository.getUserApplication(user.uid)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val parshaScheduleMode: StateFlow<ParshaScheduleMode> = schedulePreferenceStore.modeFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), schedulePreferenceStore.getMode())

    fun setParshaScheduleMode(mode: ParshaScheduleMode) {
        schedulePreferenceStore.setMode(mode)
    }
}
