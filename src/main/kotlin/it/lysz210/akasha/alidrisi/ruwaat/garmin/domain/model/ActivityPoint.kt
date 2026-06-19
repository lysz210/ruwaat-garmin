package it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model

import java.time.Instant

@JvmRecord
data class ActivityPoint(
    val activityId: ActivityId,
    val timestamp: Instant,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
) {
    fun hasGps(): Boolean = latitude != null && longitude != null
}
