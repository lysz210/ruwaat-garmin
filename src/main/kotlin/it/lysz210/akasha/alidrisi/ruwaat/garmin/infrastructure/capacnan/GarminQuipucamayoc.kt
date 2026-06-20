package it.lysz210.akasha.alidrisi.ruwaat.garmin.infrastructure.capacnan

import com.garmin.fit.RecordMesg
import com.garmin.fit.util.SemicirclesConverter
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model.ActivityId
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model.ActivityPoint
import it.lysz210.akasha.capacnan.quipus.maps.ActivityEntry
import it.lysz210.akasha.capacnan.quipus.maps.ActivityEntryKt.latLng
import it.lysz210.akasha.capacnan.quipus.maps.activityEntry
import it.lysz210.akasha.capacnan.quipus.maps.activityId
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class GarminQuipucamayoc {

    fun untie(activityId: ActivityId, mesg: RecordMesg): ActivityPoint =
        ActivityPoint(
            activityId = activityId,
            timestamp = mesg.timestamp.instant,
            latitude = mesg.positionLat?.let {
                SemicirclesConverter.semicirclesToDegrees(it)
            },
            longitude = mesg.positionLong?.let {
                SemicirclesConverter.semicirclesToDegrees(it)
            },
            altitude = mesg.enhancedAltitude?.toDouble(),
        )

    fun tie(entry: ActivityPoint): ActivityEntry =
        activityEntry {
            activityId = activityId {
                provider = entry.activityId.provider
                id = entry.activityId.id
            }
            if (entry.hasGps()) {
                latlng = latLng {
                    latitude = entry.latitude ?: 0.0
                    longitude = entry.longitude ?: 0.0
                }
            }
            if (entry.altitude != null) {
                altitude = entry.altitude
            }
        }
}