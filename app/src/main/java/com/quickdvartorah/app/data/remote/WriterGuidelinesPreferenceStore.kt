package com.quickdvartorah.app.data.remote

import android.content.Context

class WriterGuidelinesPreferenceStore(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun shouldShowForWriteScreen(uid: String, hasWriterAccess: Boolean): Boolean {
        if (!hasWriterAccess || uid.isBlank()) return false
        return !preferences.getBoolean(keyFor(uid), false)
    }

    fun markShown(uid: String) {
        if (uid.isBlank()) return
        preferences.edit().putBoolean(keyFor(uid), true).apply()
    }

    private fun keyFor(uid: String): String = "writer_guidelines_shown_$uid"

    private companion object {
        const val PREFS_NAME = "writer_guidelines_prefs"
    }
}
