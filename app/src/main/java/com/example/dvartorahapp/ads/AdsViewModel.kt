package com.example.dvartorahapp.ads

import android.app.Activity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AdsViewModel @Inject constructor(
    private val adsManager: AdsManager
) : ViewModel() {
    val uiState: StateFlow<AdsUiState> = adsManager.uiState

    fun refreshConsent(activity: Activity) {
        adsManager.refreshConsent(activity)
    }

    fun showPrivacyOptions(activity: Activity, onDismissed: (String?) -> Unit = {}) {
        adsManager.showPrivacyOptionsForm(activity, onDismissed)
    }
}
