package it.lysz210.akasha.alidrisi.ruwaat.garmin.infrastructure.capacnan

import com.garmin.fit.MesgBroadcaster
import com.garmin.fit.RecordMesgListener
import io.quarkus.logging.Log
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model.ActivityPoint
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model.FitSourceInfo
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.port.ActivitiesPort
import it.lysz210.akasha.capacnan.geo.ActivityPointsChannel
import it.lysz210.akasha.capacnan.geo.FitFilesStore
import jakarta.enterprise.context.ApplicationScoped
import java.io.ByteArrayInputStream
import java.io.IOException

@ApplicationScoped
class ActivitiesChaski(
    private val fitFilesStore: FitFilesStore,
    private val activityPointsChannel: ActivityPointsChannel,
    private val mapper: GarminQuipucamayoc,
) : ActivitiesPort {
    override fun stream(sourceInfo: FitSourceInfo): Multi<ActivityPoint> =
        fitFilesStore.get(sourceInfo)
            .onItem().transformToMulti { source ->
                Multi.createFrom().emitter { emitter ->
                    val inputStream = ByteArrayInputStream(source.data)
                    val mesgBroadcaster = MesgBroadcaster()
                    mesgBroadcaster.addListener(RecordMesgListener { mesg ->
                        if (mesg != null) {
                            val point = mapper.untie(sourceInfo.activitiId, mesg)
                            emitter.emit(point)
                        }
                    })
                    try {
                        mesgBroadcaster.run(inputStream)
                        emitter.complete()
                    } catch (e: IOException) {
                        Log.warn("Error while receiving mesg", e)
                        emitter.fail(e)
                    }
                }
                    .filter { it.hasGps() }
            }

    override fun push(entry: ActivityPoint): Uni<Long> =
        activityPointsChannel.send(mapper.tie(entry))

}