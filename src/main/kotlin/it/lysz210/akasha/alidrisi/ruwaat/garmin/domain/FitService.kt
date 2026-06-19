package it.lysz210.akasha.alidrisi.ruwaat.garmin.domain

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model.FitSourceInfo
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.port.ActivitiesPort
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class FitService(
    private val garminActivities: ActivitiesPort,
) {
    fun processFit(fitSourceInfo: FitSourceInfo): Uni<Void> {
        Log.info("Processing FitSourceInfo $fitSourceInfo")
        val objectName = fitSourceInfo.uri.path
        Log.info("Object: $objectName")
        return garminActivities.stream(fitSourceInfo)
            .invoke { point -> Log.info("Received: $point") }
            .onItem().transformToUniAndMerge { garminActivities.push(it) }
            .invoke { seq -> Log.info("Pushed as seq: $seq") }
            .collect().asList()
            .replaceWithVoid()
    }
}