package com.example.dvartorahapp.data.remote

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

enum class ParshaScheduleMode {
    DEVICE,
    ISRAEL,
    DIASPORA
}

@Singleton
class ParshaSchedulePreferenceStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun modeFlow(): Flow<ParshaScheduleMode> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _: SharedPreferences, key: String? ->
            if (key == KEY_MODE) {
                trySend(getMode())
            }
        }
        trySend(getMode())
        preferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    fun getMode(): ParshaScheduleMode {
        val raw = preferences.getString(KEY_MODE, ParshaScheduleMode.DEVICE.name)
        return raw?.let {
            runCatching { ParshaScheduleMode.valueOf(it) }.getOrDefault(ParshaScheduleMode.DEVICE)
        } ?: ParshaScheduleMode.DEVICE
    }

    fun setMode(mode: ParshaScheduleMode) {
        preferences.edit().putString(KEY_MODE, mode.name).apply()
    }

    private companion object {
        const val PREFS_NAME = "parsha_schedule_prefs"
        const val KEY_MODE = "parsha_schedule_mode"
    }
}
