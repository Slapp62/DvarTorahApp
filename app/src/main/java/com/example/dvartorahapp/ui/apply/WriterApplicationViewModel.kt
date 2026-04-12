package com.example.dvartorahapp.ui.apply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dvartorahapp.data.model.WriterApplication
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.example.dvartorahapp.data.repository.ApplicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ApplyUiEffect {
    object Success : ApplyUiEffect()
    data class ShowError(val message: String) : ApplyUiEffect()
}

@HiltViewModel
class WriterApplicationViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _currentApplication = MutableStateFlow<WriterApplication?>(null)
    val currentApplication = _currentApplication.asStateFlow()

    private val _effect = Channel<ApplyUiEffect>()
    val effect = _effect.receiveAsFlow()

    private var loadJob: Job? = null

    fun loadApplication(uid: String) {
        if (loadJob?.isActive == true) {
            loadJob?.cancel()
        }
        loadJob = viewModelScope.launch {
            applicationRepository.getUserApplication(uid).collect { application ->
                _currentApplication.value = application
            }
        }
    }

    fun submitApplication(user: UserProfile, motivation: String, agreedToContentPolicy: Boolean) {
        if (motivation.isBlank()) {
            viewModelScope.launch { _effect.send(ApplyUiEffect.ShowError("Please tell us why you want to write")) }
            return
        }
        if (!agreedToContentPolicy) {
            viewModelScope.launch { _effect.send(ApplyUiEffect.ShowError("Please agree to the content policy before applying")) }
            return
        }
        val existingApplication = _currentApplication.value
        if (existingApplication?.status == FirestoreConstants.ApplicationStatus.PENDING) {
            viewModelScope.launch { _effect.send(ApplyUiEffect.ShowError("Your application is already under review")) }
            return
        }
        if (existingApplication?.status == FirestoreConstants.ApplicationStatus.APPROVED) {
            viewModelScope.launch { _effect.send(ApplyUiEffect.ShowError("Your account already has writer access")) }
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val application = WriterApplication(
                id = user.uid,
                applicantUid = user.uid,
                applicantName = user.displayName,
                applicantEmail = user.email,
                motivation = motivation.trim()
            )
            applicationRepository.submitApplication(application).fold(
                onSuccess = { _effect.send(ApplyUiEffect.Success) },
                onFailure = { _effect.send(ApplyUiEffect.ShowError(it.message ?: "Could not submit application")) }
            )
            _isLoading.value = false
        }
    }
}
