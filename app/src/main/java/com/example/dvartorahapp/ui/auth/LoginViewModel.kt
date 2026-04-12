package com.example.dvartorahapp.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dvartorahapp.data.repository.AuthRepository
import com.example.dvartorahapp.data.repository.UserRepository
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
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    val isGoogleSignInConfigured: Boolean = authRepository.isGoogleSignInConfigured()

    private val _effect = Channel<LoginUiEffect>()
    val effect = _effect.receiveAsFlow()

    fun signIn(email: String, password: String) {
        if (_isLoading.value) return
        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch { _effect.send(LoginUiEffect.ShowError("Enter your email and password")) }
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signIn(email.trim(), password).fold(
                onSuccess = { _effect.send(LoginUiEffect.NavigateToFeed) },
                onFailure = { _effect.send(LoginUiEffect.ShowError(it.message ?: "Could not sign in")) }
            )
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(activity: Activity?) {
        if (_isLoading.value) return
        if (!authRepository.isGoogleSignInConfigured()) {
            viewModelScope.launch {
                _effect.send(LoginUiEffect.ShowError("Google sign-in is not set up yet. Finish Firebase Google Auth setup first."))
            }
            return
        }
        if (activity == null) {
            viewModelScope.launch {
                _effect.send(LoginUiEffect.ShowError("Google sign-in is unavailable on this screen right now."))
            }
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signInWithGoogle(activity).fold(
                onSuccess = { user ->
                    userRepository.ensureUserProfile(
                        uid = user.uid,
                        displayName = user.displayName.orEmpty(),
                        email = user.email.orEmpty()
                    ).fold(
                        onSuccess = { _effect.send(LoginUiEffect.NavigateToFeed) },
                        onFailure = {
                            authRepository.signOut()
                            _effect.send(LoginUiEffect.ShowError("Google sign-in worked, but the profile could not be prepared."))
                        }
                    )
                },
                onFailure = { _effect.send(LoginUiEffect.ShowError(it.message ?: "Could not sign in with Google")) }
            )
            _isLoading.value = false
        }
    }
}
