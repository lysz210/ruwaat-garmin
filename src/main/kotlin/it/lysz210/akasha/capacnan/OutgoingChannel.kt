package it.lysz210.akasha.capacnan

import com.google.protobuf.MessageLite
import io.nats.client.JetStream
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni

interface OutgoingChannel<T: MessageLite> {
    @JvmInline
    value class Subject (val value: String)

    val stream: Uni<JetStream>
    val subject: Subject

    fun send(message: T) : Uni<Long> =
        stream.map {
            Log.info("Sending $message")
            val ack = it.publish(subject.value, message.toByteArray())
            Log.info("Published message: $ack")
            ack.seqno
        }
}