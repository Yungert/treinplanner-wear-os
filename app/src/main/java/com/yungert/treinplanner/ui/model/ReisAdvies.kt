package com.yungert.treinplanner.presentation.ui.model

import androidx.annotation.Keep
import com.yungert.treinplanner.presentation.Data.models.Message
import com.yungert.treinplanner.presentation.Data.models.PrimaryMessage
import com.yungert.treinplanner.presentation.utils.ShorterStockClassificationType
import com.yungert.treinplanner.presentation.utils.TripStatus

@Keep
data class Reisadvies(
    val advies: List<Adviezen>,
    val verstrekStation: String,
    val aankomstStation: String,
)

@Keep
data class Adviezen(
    val primaryMessage: PrimaryMessage?,
    val verstrekStation: String,
    val aankomstStation: String,
    val geplandeVertrekTijd: String,
    val vertrekVertraging: String,
    val geplandeAankomstTijd: String,
    val aankomstVertraging: String,
    val actueleReistijd: String,
    val geplandeReistijd: String,
    val aantalTransfers: Int,
    val reinadviesId: String,
    val bericht: List<Message>?,
    val eindTijdverstoring: String?,
    val drukte: DrukteIndicator,
    val status: TripStatus,
    val aandachtsPunten: String?,
    val treinSoortenOpRit: String,
    val alternatiefVervoer: Boolean,
    val kortereTrein: ShorterStockClassificationType,
)
