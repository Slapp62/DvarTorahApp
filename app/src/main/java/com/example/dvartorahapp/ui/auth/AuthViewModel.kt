package com.example.dvartorahapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.example.dvartorahapp.data.repository.AuthRepository
import com.example.dvartorahapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser, val profile: UserProfile?) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser get() = authRepository.currentUser
    val userProfile: StateFlow<UserProfile?> = _authState.map { state ->
        (state as? AuthState.Authenticated)?.profile
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            authRepository.authStateFlow().collectLatest { user ->
                if (user == null) {
                    _authState.value = AuthState.Unauthenticated
                } else {
                    userRepository.getUserProfile(user.uid).collectLatest { profile ->
                        _authState.value = AuthState.Authenticated(user, profile)
                    }
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }
}
