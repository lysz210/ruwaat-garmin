package it.lysz210.akasha.alidrisi.ruwaat.garmin.infrastructure.config

import io.quarkus.runtime.annotations.IgnoreProperty
import io.smallrye.config.ConfigMapping
import it.lysz210.akasha.capacnan.blueprint.Blueprint

@ConfigMapping(prefix = "capacnan")
interface CapacnanBlueprint : Blueprint {

    fun geo(): CapacnanGeo

    interface CapacnanGeo :
            Blueprint.Resources,
            Blueprint.WithKeyValue,
            Blueprint.WithObjectStore,
            Blueprint.WithStream {
                @IgnoreProperty
                fun uploadedFitsConsumer() = "${namePrefix()}_consumer_fits"
                @IgnoreProperty
                fun uploadedFitsSubject() = "${keyPrefix()}.activities.fits.uploaded"

                @IgnoreProperty
                fun activitiesEntriesSubject() = "${keyPrefix()}.activities.entries"
            }
}