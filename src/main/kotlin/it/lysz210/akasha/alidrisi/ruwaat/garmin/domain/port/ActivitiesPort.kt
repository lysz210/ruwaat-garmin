package it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.port

import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model.ActivityPoint
import it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model.FitSourceInfo

interface ActivitiesPort {
    fun stream(sourceInfo: FitSourceInfo): Multi<ActivityPoint>
    fun push(entry: ActivityPoint): Uni<Long>
}