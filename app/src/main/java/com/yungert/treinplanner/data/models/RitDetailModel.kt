package com.yungert.treinplanner.presentation.Data.models

import androidx.annotation.Keep

@Keep
data class RitDetailApiResponse(
    val payload: Payload
)

@Keep
data class Payload(
    val notes: List<JourneyNote>,
    val productNumbers: List<String>,
    val stops: List<JourneyStop>,
    val allowCrowdReporting: Boolean,
    val source: String,
)

@Keep
data class JourneyNote(
    val value: String?,
    val accessibilityValue: String?,
    val key: String?,
    val noteType: String?,
    val priority: Int?,
    val routeIdxFrom: Int?,
    val routeIdxTo: Int?,
    val link: Link?,
    val isPresentationRequired: Boolean?,
    val category: String?
)

@Keep
data class JourneyStop(
    val id: String,
    val stop: StopDetails,
    val previousStopId: List<String>,
    val nextStopId: List<String>,
    val destination: String,
    val status: String,
    val kind: String?,
    val arrivals: List<Arrival>,
    val departures: List<Departure>,
    val actualStock: TrainStock?,
    val plannedStock: TrainStock?,
    val platformFeatures: List<PlatformFeature>,
    val coachCrowdForecast: List<CoachCrowdForecast>
)

@Keep
data class StopDetails(
    val name: String,
    val lng: Double,
    val lat: Double,
    val countryCode: String,
    val uicCode: String
)

@Keep
data class Departure(
    val product: JourneyProduct,
    val origin: StopDetails,
    val destination: StopDetails,
    val plannedTime: String,
    val actualTime: String,
    val delayInSeconds: Int,
    val plannedTrack: String,
    val actualTrack: String,
    val cancelled: Boolean,
    val crowdForecast: String,
    val stockIdentifiers: List<String>
)

@Keep
data class JourneyProduct(
    val number: String,
    val categoryCode: String,
    val shortCategoryName: String,
    val longCategoryName: String,
    val operatorCode: String,
    val operatorName: String,
    val type: String
)

@Keep
data class TrainStock(
    val trainType: String,
    val numberOfSeats: Int,
    val numberOfParts: Int,
    val trainParts: List<TrainPart>,
    val hasSignificantChange: Boolean?
)

@Keep
data class TrainPart(
    val stockIdentifier: String,
    val facilities: List<String>,
    val image: Image,
    val destination: Eindbestemming?,
)

@Keep
data class Eindbestemming(
    val name: String,
    val lng: String,
    val lat: String,
    val countryCode: String,
    val uicCode: String,
)

@Keep
data class Arrival(
    val product: JourneyProduct,
    val origin: StopDetails,
    val destination: StopDetails,
    val plannedTime: String,
    val actualTime: String,
    val delayInSeconds: Int,
    val punctuality: Double,
    val plannedTrack: String,
    val actualTrack: String,
    val cancelled: Boolean,
    val crowdForecast: String,
    val stockIdentifiers: List<String>
)

@Keep
data class Image(
    val uri: String
)

@Keep
data class Station(
    val UICCode: String?,
    val stationType: String?,
    val EVACode: String?,
    val code: String?,
    val sporen: List<Track>?,
    val synoniemen: List<String>?,
    val heeftFaciliteiten: Boolean?,
    val heeftVertrektijden: Boolean?,
    val heeftReisassistentie: Boolean?,
    val namen: StationsNamen?,
    val land: String?,
    val lat: Double?,
    val lng: Double?,
    val radius: Int?,
    val naderenRadius: Int?,
    val distance: Double?,
    val ingangsDatum: String?,
    val eindDatum: String?,
    val nearbyMeLocationId: NearbyMeLocationId?
)

@Keep
data class Track(
    val spoorNummer: String?
)

@Keep
data class StationsNamen(
    val lang: String?,
    val middel: String?,
    val kort: String?,
    val festive: String?
)

@Keep
data class NearbyMeLocationId(
    val type: String?,
    val value: String?
)

@Keep
data class PlatformFeature(
    val paddingLeft: Int?,
    val width: Int?,
    val type: String?,
    val description: String?
)

@Keep
data class CoachCrowdForecast(
    val paddingLeft: Int?,
    val width: Int?,
    val classification: String?
)
