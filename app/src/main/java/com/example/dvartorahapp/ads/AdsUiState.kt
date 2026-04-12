package com.example.dvartorahapp.ads

data class AdsUiState(
    val canRequestAds: Boolean = false,
    val privacyOptionsRequired: Boolean = false,
    val lastError: String? = null
)
