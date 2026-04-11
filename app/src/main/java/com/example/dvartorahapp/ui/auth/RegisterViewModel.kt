package com.example.dvartorahapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.example.dvartorahapp.data.repository.AuthRepository
import com.example.dvartorahapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterUiEffect {
    object NavigateToFeed : RegisterUiEffect()
    data class ShowError(val message: String) : RegisterUiEffect()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _effect = Channel<RegisterUiEffect>()
    val effect = _effect.receiveAsFlow()

    fun register(displayName: String, email: String, password: String, confirmPassword: String) {
        when {
            displayName.isBlank() || email.isBlank() || password.isBlank() ->
                viewModelScope.launch { _effect.send(RegisterUiEffect.ShowError("Please fill in all fields")) }
            password != confirmPassword ->
                viewModelScope.launch { _effect.send(RegisterUiEffect.ShowError("Passwords do not match")) }
            password.length < 6 ->
                viewModelScope.launch { _effect.send(RegisterUiEffect.ShowError("Password must be at least 6 characters")) }
            else -> viewModelScope.launch {
                _isLoading.value = true
                authRepository.register(email.trim(), password).fold(
                    onSuccess = { user ->
                        val profile = UserProfile(
                            uid = user.uid,
                            displayName = displayName.trim(),
                            email = email.trim(),
                            role = FirestoreConstants.Roles.VIEWER
                        )
                        userRepository.createUserProfile(profile).fold(
                            onSuccess = { _effect.send(RegisterUiEffect.NavigateToFeed) },
                            onFailure = { _effect.send(RegisterUiEffect.ShowError("Account created but profile setup failed")) }
                        )
                    },
                    onFailure = { _effect.send(RegisterUiEffect.ShowError(it.message ?: "Registration failed")) }
                )
                _isLoading.value = false
            }
        }
    }
}
