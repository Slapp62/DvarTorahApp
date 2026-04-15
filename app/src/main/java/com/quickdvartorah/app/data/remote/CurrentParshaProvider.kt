package com.quickdvartorah.app.data.remote

import com.quickdvartorah.app.data.model.ParshaOccasion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

data class CurrentParshaInfo(
    val occasion: ParshaOccasion,
    val targetDateIso: String,
    val isIsraelSchedule: Boolean
)

@Singleton
class CurrentParshaProvider @Inject constructor() {
    @Volatile
    private var cachedKey: String? = null

    @Volatile
    private var cachedParsha: CurrentParshaInfo? = null

    suspend fun getCurrentParsha(mode: ParshaScheduleMode = ParshaScheduleMode.DEVICE): CurrentParshaInfo? = withContext(Dispatchers.IO) {
        getParshaForShabbatOffset(mode, 0)
    }

    suspend fun getParshaForShabbatOffset(
        mode: ParshaScheduleMode = ParshaScheduleMode.DEVICE,
        weekOffset: Int = 0
    ): CurrentParshaInfo? = withContext(Dispatchers.IO) {
        val deviceTimeZone = TimeZone.getDefault()
        val isIsraelSchedule = when (mode) {
            ParshaScheduleMode.DEVICE -> useIsraelSchedule(deviceTimeZone, Locale.getDefault())
            ParshaScheduleMode.ISRAEL -> true
            ParshaScheduleMode.DIASPORA -> false
        }
        val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = deviceTimeZone
        }
        val shabbat = nextOrSameShabbat(deviceTimeZone)
        if (weekOffset != 0) {
            shabbat.add(Calendar.DAY_OF_MONTH, weekOffset * 7)
        }
        val dateIso = isoDateFormat.format(shabbat.time)
        val cacheKey = "${deviceTimeZone.id}|$isIsraelSchedule|$dateIso"

        cachedParsha?.takeIf { cachedKey == cacheKey }?.let { return@withContext it }

        val israelParam = if (isIsraelSchedule) "on" else "off"
        val url = URL("https://www.hebcal.com/leyning?cfg=json&i=$israelParam&triennial=off&date=$dateIso")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8_000
            readTimeout = 8_000
            setRequestProperty("Accept", "application/json")
        }

        try {
            val payload = connection.inputStream.bufferedReader().use { it.readText() }
            val items = JSONObject(payload).optJSONArray("items") ?: return@withContext null
            for (index in 0 until items.length()) {
                val item = items.optJSONObject(index) ?: continue
                val name = item.optJSONObject("name")?.optString("en").orEmpty()
                val occasion = ParshaOccasion.fromHebcalName(name)
                if (occasion != null) {
                    return@withContext CurrentParshaInfo(
                        occasion = occasion,
                        targetDateIso = dateIso,
                        isIsraelSchedule = isIsraelSchedule
                    ).also {
                        cachedKey = cacheKey
                        cachedParsha = it
                    }
                }
            }
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun nextOrSameShabbat(timeZone: TimeZone): Calendar {
        val calendar = Calendar.getInstance(timeZone)
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return calendar
    }

    private fun useIsraelSchedule(timeZone: TimeZone, locale: Locale): Boolean {
        val country = locale.country.uppercase(Locale.US)
        if (country == "IL") return true

        return timeZone.id == "Asia/Jerusalem"
    }
}
