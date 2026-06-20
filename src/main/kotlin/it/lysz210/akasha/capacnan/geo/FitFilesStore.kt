package it.lysz210.akasha.capacnan.geo

import io.nats.client.Connection
import io.nats.client.ObjectStore
import io.nats.client.api.ObjectInfo
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.infrastructure.Infrastructure
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model.FitSourceInfo
import it.lysz210.akasha.alidrisi.ruwaat.garmin.infrastructure.config.CapacnanBlueprint
import jakarta.enterprise.context.ApplicationScoped
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.GZIPInputStream

@ApplicationScoped
class FitFilesStore (
    private val connection: Connection,
    blueprint: CapacnanBlueprint,
) {
    @JvmRecord
    data class NatsObject(
        val info: ObjectInfo,
        val data: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as NatsObject

            if (info != other.info) return false
            if (!data.contentEquals(other.data)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = info.hashCode()
            result = 31 * result + data.contentHashCode()
            return result
        }
    }

    private val storeName = blueprint.geo().objectStore().bucket()

    protected fun objectStore(): Uni<ObjectStore> =
        connection.objectStore(this.storeName).let {
            Uni.createFrom().item(it)
        }

    fun get(source: FitSourceInfo): Uni<NatsObject> {
        return objectStore().map { store ->
            val objectName = source.uri.path
            val zipFile = ByteArrayOutputStream()
            val objectInfo = store.get(objectName, zipFile)
            val fitFile = ByteArrayOutputStream()
            Log.info("Read $objectInfo")
            GZIPInputStream(ByteArrayInputStream(zipFile.toByteArray())).use {
                it.copyTo(fitFile)
            }
            NatsObject(
                info = objectInfo,
                data = fitFile.toByteArray(),
            )
        }
            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
    }

}