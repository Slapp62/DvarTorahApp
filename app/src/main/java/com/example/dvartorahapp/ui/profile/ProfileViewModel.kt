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
import com.example.dvartorahapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiEffect {
    object AccountDeleted : ProfileUiEffect()
    data class ShowMessage(val message: String) : ProfileUiEffect()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dvarTorahRepository: DvarTorahRepository,
    private val applicationRepository: ApplicationRepository,
    private val schedulePreferenceStore: ParshaSchedulePreferenceStore,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isDeletingAccount = MutableStateFlow(false)
    val isDeletingAccount: StateFlow<Boolean> = _isDeletingAccount.asStateFlow()

    private val _effect = Channel<ProfileUiEffect>()
    val effect = _effect.receiveAsFlow()

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

    fun showMessage(message: String) {
        viewModelScope.launch {
            _effect.send(ProfileUiEffect.ShowMessage(message))
        }
    }

    fun deleteAccount() {
        val user = authRepository.currentUser ?: run {
            showMessage("No signed-in account to delete.")
            return
        }

        viewModelScope.launch {
            _isDeletingAccount.value = true
            userRepository.deleteAccountData(user.uid).fold(
                onSuccess = {
                    authRepository.deleteCurrentUser().fold(
                        onSuccess = { _effect.send(ProfileUiEffect.AccountDeleted) },
                        onFailure = {
                            _effect.send(
                                ProfileUiEffect.ShowMessage(
                                    it.message ?: "Account data was removed, but the sign-in account still needs a fresh login before deletion."
                                )
                            )
                        }
                    )
                },
                onFailure = {
                    _effect.send(ProfileUiEffect.ShowMessage(it.message ?: "Could not delete the account."))
                }
            )
            _isDeletingAccount.value = false
        }
    }
}
