package com.example.dvartorahapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dvartorahapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiEffect {
    object NavigateToFeed : LoginUiEffect()
    data class ShowError(val message: String) : LoginUiEffect()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _effect = Channel<LoginUiEffect>()
    val effect = _effect.receiveAsFlow()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch { _effect.send(LoginUiEffect.ShowError("Please fill in all fields")) }
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signIn(email.trim(), password).fold(
                onSuccess = { _effect.send(LoginUiEffect.NavigateToFeed) },
                onFailure = { _effect.send(LoginUiEffect.ShowError(it.message ?: "Sign in failed")) }
            )
            _isLoading.value = false
        }
    }
}
