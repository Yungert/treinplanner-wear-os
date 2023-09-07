package com.yungert.treinplanner.presentation.Data.models

import androidx.annotation.Keep

@Keep
data class ReisAdviesApiResponse(
    val source: String,
    val trips: List<TripDetail>,
    val scrollRequestBackwardContext: String,
    val scrollRequestForwardContext: String,
    val message: String
)

@Keep
data class TripDetail(
    val uid: String,
    val ctxRecon: String,
    val plannedDurationInMinutes: Int,
    val actualDurationInMinutes: Int,
    val transfers: Int,
    val status: String,
    val primaryMessage: PrimaryMessage?,
    val messages: List<Message>,
    val legs: List<Leg>,
    val overviewPolyLine: List<Coordinate>,
    val crowdForecast: String,
    val punctuality: Double?,
    val optimal: Boolean,
    val fareRoute: FareRoute,
    val fares: List<Fare>,
    val fareLegs: List<FareLeg>,
    val productFare: Fare,
    val fareOptions: FareOptions,
    val bookingUrl: Link,
    val nsiLink: NsiLink,
    val type: String,
    val shareUrl: Link,
    val realtime: Boolean,
    val travelAssistanceInfo: TravelAssistanceInfo,
    val routeId: String,
    val registerJourney: RegisterJourney,
    val eco: Eco,
    val modalityListItems: List<ModalityListItem>
)

@Keep
data class Message(
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
    val endTime: String,
    val phase: String
)

@Keep
data class Leg(
    val idx: String,
    val ctxRecon: String,
    val name: String,
    val travelType: String,
    val direction: String,
    val cancelled: Boolean,
    val changePossible: Boolean,
    val alternativeTransport: Boolean,
    val journeyDetailRef: String,
    val origin: Location,
    val destination: Location,
    val product: Product,
    val sharedModality: SharedModality,
    val notes: List<Note>,
    val messages: List<Message>,
    val transferMessages: List<TransferMessage>?,
    val stops: List<Stop>,
    val steps: List<Step>,
    val coordinates: List<List<Double>>,
    val crowdForecast: String,
    val bicycleSpotCount: Int,
    val punctuality: Double?,
    val crossPlatformTransfer: Boolean,
    val shorterStock: Boolean,
    val changeCouldBePossible: Boolean,
    val shorterStockWarning: String,
    val shorterStockClassification: String,
    val journeyDetail: List<Detail>,
    val reachable: Boolean,
    val plannedDurationInMinutes: Int,
    val actualDurationInMinutes: Int?,
    val transfers: Int,
    val overviewPolyLine: List<Coordinate>,
    val nesProperties: NesProperties
)

@Keep
data class Coordinate(
    val lat: Double,
    val lng: Double
)

@Keep
data class Location(
    val name: String,
    val lng: Double,
    val lat: Double,
    val city: String,
    val countryCode: String,
    val uicCode: String,
    val stationCode: String,
    val type: String,
    val prognosisType: String,
    val plannedTimeZoneOffset: Int,
    val plannedDateTime: String,
    val actualTimeZoneOffset: Int,
    val actualDateTime: String?,
    val plannedTrack: String,
    val actualTrack: String?,
    val exitSide: String,
    val checkinStatus: String,
    val travelAssistanceMeetingPoints: List<String>,
    val notes: List<Note>,
    val quayCode: String
)

@Keep
data class Product(
    val number: String,
    val categoryCode: String,
    val shortCategoryName: String,
    val longCategoryName: String,
    val operatorCode: String,
    val operatorName: String,
    val operatorAdministrativeCode: Int,
    val type: String,
    val displayName: String,
    val nesProperties: NesProperties
)

@Keep
data class SharedModality(
    val provider: String,
    val name: String,
    val availability: Boolean,
    val nearByMeMapping: String,
    val planIcon: String
)

@Keep
data class FareLegStop(
    val name: String,
    val lng: Double,
    val lat: Double,
    val countryCode: String,
    val uicCode: String,
    val stationCode: String,
    val type: String
)

@Keep
data class Fare(
    val priceInCents: Int,
    val product: String,
    val travelClass: String,
    val discountType: String
)

@Keep
data class FareLeg(
    val origin: FareLegStop,
    val destination: FareLegStop,
    val operator: String,
    val productTypes: List<String>,
    val fares: List<Fare>
)

@Keep
data class FareRoute(
    val routeId: String,
    val origin: FareLegStop,
    val destination: FareLegStop
)

@Keep
data class FareOptions(
    val isInternationalBookable: Boolean,
    val isInternational: Boolean,
    val isEticketBuyable: Boolean,
    val isPossibleWithOvChipkaart: Boolean,
    val isTotalPriceUnknown: Boolean
)

@Keep
data class NsiLink(
    val url: String,
    val showInternationalBanner: Boolean
)

@Keep
data class ShareUrl(
    val uri: String
)

@Keep
data class ModalityListItem(
    val name: String,
    val nameNesProperties: NameNesProperties,
    val iconNesProperties: IconNesProperties,
    val actualTrack: String,
    val accessibilityName: String
)

@Keep
data class NameNesProperties(
    val color: String,
    val styles: Styles
)

@Keep
data class IconNesProperties(
    val color: String,
    val icon: String
)

@Keep
data class Styles(
    val type: String,
    val strikethrough: Boolean
)

@Keep
data class RouteResponse(
    val fareRoute: FareRoute,
    val fares: List<Fare>,
    val fareLegs: List<FareLeg>,
    val productFare: Fare,
    val fareOptions: FareOptions,
    val nsiLink: NsiLink,
    val type: String,
    val shareUrl: ShareUrl,
    val realtime: Boolean,
    val routeId: String,
    val registerJourney: RegisterJourney,
    val modalityListItems: List<ModalityListItem>
)

@Keep
data class RegisterJourney(
    val url: String,
    val searchUrl: String,
    val status: String,
    val bicycleReservationRequired: Boolean
)

@Keep
data class NesProperties(
    val color: String,
    val scope: String,
    val styles: LineStyles
)

@Keep
data class LineStyles(
    val type: String,
    val dashed: Boolean
)

@Keep
data class Note(
    val value: String?,
    val accessibilityValue: String?,
    val key: String?,
    val noteType: String,
    val priority: Int,
    val routeIdxFrom: Int,
    val routeIdxTo: Int,
    val link: Link?,
    val isPresentationRequired: Boolean,
    val category: String
)

@Keep
data class Link(
    val title: String?,
    val href: String?
)

@Keep
data class TravelAssistanceInfo(
    val termsAndConditionsLink: String?,
    val tripRequestId: Int,
    val isAssistanceRequired: Boolean
)

@Keep
data class Eco(
    val co2kg: Double
)

@Keep
data class TransferMessage(
    val message: String,
    val accessibilityMessage: String,
    val type: String,
    val messageNesProperties: MessageNesProperties
)

@Keep
data class MessageNesProperties(
    val color: String
)

@Keep
data class Detail(
    val type: String,
    val link: JourneyLink
)

@Keep
data class JourneyLink(
    val uri: String
)

@Keep
data class StopNote(
    val value: String,
    val key: String,
    val type: String,
    val priority: Int
)

@Keep
data class Stop(
    val uicCode: String?,
    val name: String?,
    val lat: Double?,
    val lng: Double?,
    val countryCode: String?,
    val notes: List<StopNote>?,
    val routeIdx: Int?,
    val departurePrognosisType: String?,
    val plannedDepartureDateTime: String?,
    val plannedDepartureTimeZoneOffset: Int?,
    val actualDepartureDateTime: String?,
    val actualDepartureTimeZoneOffset: Int?,
    val plannedArrivalDateTime: String?,
    val plannedArrivalTimeZoneOffset: Int?,
    val actualArrivalDateTime: String?,
    val actualArrivalTimeZoneOffset: Int?,
    val plannedPassingDateTime: String?,
    val actualPassingDateTime: String?,
    val arrivalPrognosisType: String?,
    val actualDepartureTrack: String?,
    val plannedDepartureTrack: String?,
    val plannedArrivalTrack: String?,
    val actualArrivalTrack: String?,
    val departureDelayInSeconds: Long?,
    val arrivalDelayInSeconds: Long?,
    val cancelled: Boolean?,
    val borderStop: Boolean?,
    val passing: Boolean?,
    val quayCode: String?
)

@Keep
data class Step(
    val distanceInMeters: Int,
    val durationInSeconds: Int,
    val startLocation: Location,
    val endLocation: Location,
    val instructions: String
)

@Keep
data class StationReference(
    val uicCode: String,
    val stationCode: String?,
    val name: String,
    val coordinate: Coordinate?,
    val countryCode: String
)

@Keep
data class PrimaryMessage(
    val title: String,
    val nesProperties: NesProperties?,
    val message: Message?,
    val type: String?
)
