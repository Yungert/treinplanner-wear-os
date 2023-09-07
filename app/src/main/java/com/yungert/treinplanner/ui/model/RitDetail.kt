package com.yungert.treinplanner.presentation.ui.model

import androidx.annotation.Keep
import com.yungert.treinplanner.presentation.utils.StopStatusType

@Keep
data class TreinRitDetail(
    val eindbestemmingTrein: String,
    val ritNummer: String,
    var opgeheven: Boolean,
    var aantalZitplaatsen: Int,
    var aantalTreinDelen: Int,
    var materieelType: String,
    var ingekort: Boolean,
    var materieelInzet: List<MaterieelInzet>,
    val stops: List<StopOpRoute>,
)

data class StopOpRoute(
    val stationNaam: String,
    val spoor: String?,
    val geplandeAankomstTijd: String,
    val actueleAankomstTijd: String,
    val aankomstVertraging: String,
    val geplandeVertrektTijd: String,
    val actueleVertrekTijd: String,
    val vertrekVertraging: String,
    val drukte: DrukteIndicator,
    val punctualiteit: String,
    val opgeheven: Boolean,
    val status: StopStatusType?
)

data class MaterieelInzet(
    var treinNummer : String,
    var eindBestemmingTreindeel: String
)

