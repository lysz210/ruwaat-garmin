package it.lysz210.akasha.alidrisi.ruwaat.garmin.infrastructure.capacnan

import com.google.protobuf.Parser
import io.nats.client.ConsumerContext
import io.smallrye.mutiny.Uni
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.FitService
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model.ActivityId
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model.FitSourceInfo
import it.lysz210.akasha.capacnan.CapacnanGeoUploadedFits
import it.lysz210.akasha.capacnan.IncomingChannel
import it.lysz210.akasha.capacnan.quipus.maps.ActivityFitSource
import jakarta.enterprise.context.ApplicationScoped
import java.net.URI

@ApplicationScoped
class FitsChannel (
    @param:CapacnanGeoUploadedFits override val consumer: Uni<ConsumerContext>,
    private val fitService: FitService,
) : IncomingChannel<ActivityFitSource> {

    override val payloadParser: Parser<ActivityFitSource> = ActivityFitSource.parser()

    override fun onMessage(payload: ActivityFitSource): Uni<Void> =
        fitService.processFit(
            FitSourceInfo(
                activitiId = payload.activityId.let { ActivityId("${it.provider}.${it.id}") },
                uri = URI.create(payload.uri)
            )
        )
}