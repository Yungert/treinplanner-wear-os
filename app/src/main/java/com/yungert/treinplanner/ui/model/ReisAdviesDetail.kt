package com.yungert.treinplanner.presentation.ui.model

import androidx.annotation.Keep
import com.yungert.treinplanner.presentation.Data.models.Message
import com.yungert.treinplanner.presentation.Data.models.NesProperties
import com.yungert.treinplanner.presentation.Data.models.TransferMessage

data class DetailReisAdvies(
    var opgeheven: Boolean,
    var redenOpheffen: String?,
    var rit: List<RitDetail>,
    val hoofdBericht: String?,
    var eindTijdVerstoring: String,
)
@Keep
data class RitDetail(
    val treinOperator: String,
    val treinOperatorType: String,
    val ritNummer: String,
    val eindbestemmingTrein: String,
    val datum: String,
    val kortereTreinDanGepland: Boolean,


    val naamVertrekStation: String,
    val geplandeVertrektijd: String,
    val vertrekSpoor: String,
    val vertrekVertraging: String,
    val vertrekStationUicCode: String,

    val naamAankomstStation: String,
    val geplandeAankomsttijd: String,
    val aankomstSpoor: String?,
    val aankomstVertraging: String,
    val aankomstStationUicCode: String,
    val punctualiteit: Double,

    val berichten: List<Message>,

    val transferBericht: List<TransferMessage>?,
    val alternatiefVervoer: Boolean,
    val ritId: String,

    val overstapTijd: String?,
    val opgeheven: Boolean
)
@Keep
data class MessageData(
    val id: String,
    val externalId: String,
    val head: String,
    val text: String,
    val lead: String,
    val routeIdxFrom: Int,
    val routeIdxTo: Int,
    val type: String,
    val nesProperties: NesProperties,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String
)