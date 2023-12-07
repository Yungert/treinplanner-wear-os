package com.yungert.treinplanner.presentation.ui.model

import androidx.annotation.Keep
import com.yungert.treinplanner.presentation.Data.models.Message
import com.yungert.treinplanner.presentation.Data.models.TransferMessage
import com.yungert.treinplanner.presentation.utils.ShorterStockClassificationType

@Keep
data class DetailReisadvies(
    var opgeheven: Boolean,
    var redenOpheffen: String?,
    var rit: List<RitDetail>,
    val hoofdBericht: String?,
    var eindTijdVerstoring: String,
    var dataEindStation: DataEindbestemmingStation?,
    var dataAlternatiefVervoer: AlternatiefVervoer?
)

@Keep
data class AlternatiefVervoer(
    var advies: String?,
    var soortVervoer: String?,
    var vertrekLocatieStation: String?,
    var minumimExtraReistijd: String?,
    var maximumExtraReistijd: String?,
)

@Keep
data class RitDetail(
    val treinOperator: String,
    val treinOperatorType: String,
    val ritNummer: String,
    val eindbestemmingTrein: String,
    val datum: String,
    val kortereTreinDanGepland: ShorterStockClassificationType,


    val naamVertrekStation: String,
    val geplandeVertrektijd: String,
    val geplandVertrekSpoor: String,
    val actueelVertrekSpoor: String?,
    val vertrekVertraging: String,
    val vertrekStationUicCode: String,

    val naamAankomstStation: String,
    val geplandeAankomsttijd: String,
    val geplandAankomstSpoor: String,
    val actueelAankomstspoor: String?,
    val aankomstVertraging: String,
    val aankomstStationUicCode: String,
    val punctualiteit: Double,

    val berichten: List<Message>?,

    val transferBericht: List<TransferMessage>?,
    val alternatiefVervoer: Boolean,
    val ritId: String,

    val crossPlatform: Boolean,
    val overstapMogelijk: Boolean,
    val overstapTijd: String?,
    val opgeheven: Boolean,

    )

@Keep
data class DataEindbestemmingStation(
    var ovFiets: List<OvFiets>?,
    val ritPrijsInEuro: String,
)

@Keep
data class OvFiets(
    val aantalOvFietsen: String,
    val locatieFietsStalling: String,
)