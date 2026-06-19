package it.lysz210.akasha.capacnan.geo

import io.nats.client.JetStream
import io.smallrye.mutiny.Uni
import it.lysz210.akasha.alidrisi.ruwaat.garmin.infrastructure.config.CapacnanBlueprint
import it.lysz210.akasha.capacnan.OutgoingChannel
import it.lysz210.akasha.capacnan.quipus.maps.ActivityEntry
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ActivityPointsChannel(
    override val stream: Uni<JetStream>,
    blueprint: CapacnanBlueprint,
) : OutgoingChannel<ActivityEntry> {
    override val subject: OutgoingChannel.Subject =
        OutgoingChannel.Subject(blueprint.geo().activitiesEntriesSubject())
}