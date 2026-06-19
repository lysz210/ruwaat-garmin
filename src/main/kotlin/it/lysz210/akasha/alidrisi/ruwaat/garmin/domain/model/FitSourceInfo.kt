package it.lysz210.akasha.alidrisi.ruwaat.garmin.domain.model

import java.net.URI

@JvmRecord
data class FitSourceInfo(
    val activitiId: ActivityId,
    val uri: URI
)