package com.example.dvartorahapp.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsManager @Inject constructor(
    @param:ApplicationContext private val appContext: Context
) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(appContext)

    private val _uiState = MutableStateFlow(AdsUiState())
    val uiState: StateFlow<AdsUiState> = _uiState.asStateFlow()

    private var mobileAdsInitialized = false

    fun refreshConsent(activity: Activity) {
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    updateState(formError)
                }
            },
            { requestError ->
                updateState(requestError)
            }
        )
    }

    fun showPrivacyOptionsForm(activity: Activity, onDismissed: (String?) -> Unit = {}) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            updateState(formError)
            onDismissed(formError?.message)
        }
    }

    private fun updateState(formError: FormError?) {
        if (consentInformation.canRequestAds() && !mobileAdsInitialized) {
            MobileAds.initialize(appContext)
            mobileAdsInitialized = true
        }

        _uiState.value = AdsUiState(
            canRequestAds = consentInformation.canRequestAds(),
            privacyOptionsRequired =
                consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED,
            lastError = formError?.message
        )
    }
}
