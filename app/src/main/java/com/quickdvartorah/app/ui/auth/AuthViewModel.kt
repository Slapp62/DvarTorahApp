package com.quickdvartorah.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickdvartorah.app.data.model.UserProfile
import com.quickdvartorah.app.data.remote.FirestoreConstants
import com.quickdvartorah.app.data.repository.AuthRepository
import com.quickdvartorah.app.data.repository.UserRepository
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
