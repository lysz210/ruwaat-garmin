package it.lysz210.akasha.alidrisi.ruwaat.garmin.infrastructure.capacnan

import io.nats.client.Connection
import io.nats.client.ConsumerContext
import io.nats.client.JetStream
import io.nats.client.Nats
import io.nats.client.StreamContext
import io.nats.client.api.ConsumerConfiguration
import io.nats.client.api.DeliverPolicy
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import it.lysz210.akasha.alidrisi.ruwaat.garmin.infrastructure.config.CapacnanBlueprint
import it.lysz210.akasha.capacnan.CapacnanGeo
import it.lysz210.akasha.capacnan.CapacnanGeoUploadedFits
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Duration

@ApplicationScoped
class CapacnanBeanProvider {

    @Produces
    @ApplicationScoped
    fun natsConnection(@ConfigProperty(name = "nats.connection.url") natsUrl: String): Connection {
        return Nats.connect(natsUrl)
    }

    @Produces
    @ApplicationScoped
    fun jetStream(nats: Connection): Uni<JetStream> =
        Uni.createFrom().item { nats.jetStream() }

    @Produces
    @ApplicationScoped
    @CapacnanGeo
    fun geoStreamContext(
        nats: Connection,
        blueprint: CapacnanBlueprint
    ): Uni<StreamContext> {

        val streamName = blueprint.geo().stream()
        return Uni.createFrom().item {
                Log.info("Attempting to fetch NATS StreamContext for $streamName")
                nats.getStreamContext(streamName)
            }
            .onFailure().invoke { ex ->
                Log.warn("Stream $streamName not available yet. Retrying in 5 seconds...", ex)
            }
            .onFailure().retry()
            .withBackOff(Duration.ofSeconds(5))
            .indefinitely()
            .memoize().indefinitely()
    }


    @Produces
    @ApplicationScoped
    @CapacnanGeoUploadedFits
    fun uploadedFits(
        @CapacnanGeo streamContext: Uni<StreamContext>,
        blueprint: CapacnanBlueprint,
    ): Uni<ConsumerContext> =
        streamContext.map { context ->
            val consumerName = blueprint.geo().uploadedFitsConsumer()
            val subject = blueprint.geo().uploadedFitsSubject()
            val config = ConsumerConfiguration.builder()
                .durable(consumerName)
                .filterSubject(subject)
                .deliverPolicy(DeliverPolicy.Last)
//                .ackWait(Duration.ofMinutes(5))
//                .maxDeliver(3)
                .build()
            context.createOrUpdateConsumer(config)
        }
            .memoize().indefinitely()
}