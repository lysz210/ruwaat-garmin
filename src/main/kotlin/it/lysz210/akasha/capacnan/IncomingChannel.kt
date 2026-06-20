package it.lysz210.akasha.capacnan

import com.google.protobuf.MessageLite
import com.google.protobuf.Parser
import io.nats.client.ConsumerContext
import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import io.smallrye.mutiny.Uni
import jakarta.enterprise.event.Observes

interface IncomingChannel<T: MessageLite> {

    val consumer: Uni<ConsumerContext>
    val payloadParser: Parser<T>

    fun onStart(@Observes e: StartupEvent) {
        Log.info("Initializing GeoConsumer Init...")

        consumer.subscribe().with { consumerContext ->
            Log.info("Attaching consumer")
            consumerContext.consume { message ->
                val payload = payloadParser.parseFrom(message.data)
                Log.info("Received message: ${message.metaData().streamSequence()}")

                onMessage(payload)
                    .subscribe().with(
                        {
                            message.ack()
                            Log.info("Successfully processed fit for activity.")
                        },
                        { error ->
                            message.nak()
                            Log.error("Failed to process fit", error)
                        }
                    )
            }
        }
    }

    fun onMessage(payload: T): Uni<Void>

}