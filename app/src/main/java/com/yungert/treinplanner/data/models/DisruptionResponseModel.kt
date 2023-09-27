package com.yungert.treinplanner.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DisruptionResponseModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("registrationTime")
    val registrationTime: String,
    @SerializedName("releaseTime")
    val releaseTime: String,
    @SerializedName("local")
    val local: Boolean,
    @SerializedName("title")
    val title: String,
    @SerializedName("titleSections")
    val titleSections: List<List<TitleSection>>,
    @SerializedName("isActive")
    val isActive: Boolean,
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String,
    @SerializedName("period")
    val period: String,
    @SerializedName("impact")
    val impact: Impact,
    @SerializedName("summaryAdditionalTravelTime")
    val summaryAdditionalTravelTime: SummaryAdditionalTravelTime,
    @SerializedName("publicationSections")
    val publicationSections: List<PublicationSection>,
    @SerializedName("timespans")
    val timespans: List<Timespan>,
    @SerializedName("alternativeTransportTimespans")
    val alternativeTransportTimespans: List<AlternativeTransportTimespan>,
    @SerializedName("expectedDuration")
    val expectedDuration: ExpectedDuration,
)

@Keep
data class TitleSection(
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: String
)

@Keep
data class Impact(
    @SerializedName("value")
    val value: Int
)

@Keep
data class SummaryAdditionalTravelTime(
    @SerializedName("label")
    val label: String,
    @SerializedName("shortLabel")
    val shortLabel: String,
    @SerializedName("minimumDurationInMinutes")
    val minimumDurationInMinutes: Int?,
    @SerializedName("maximumDurationInMinutes")
    val maximumDurationInMinutes: Int?
)

@Keep
data class PublicationSection(
    @SerializedName("section")
    val section: Section,
    @SerializedName("consequence")
    val consequence: Consequence,
    @SerializedName("sectionType")
    val sectionType: String
)

@Keep
data class Section(
    @SerializedName("stations")
    val stations: List<Station>,
    @SerializedName("direction")
    val direction: String
)

@Keep
data class Station(
    @SerializedName("uicCode")
    val uicCode: String,
    @SerializedName("stationCode")
    val stationCode: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("coordinate")
    val coordinate: Coordinate,
    @SerializedName("countryCode")
    val countryCode: String
)

@Keep
data class Coordinate(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)

@Keep
data class Consequence(
    @SerializedName("section")
    val section: Section,
    @SerializedName("description")
    val description: String,
    @SerializedName("level")
    val level: String
)

@Keep
data class Timespan(
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String,
    @SerializedName("period")
    val period: String,
    @SerializedName("situation")
    val situation: Situation,
    @SerializedName("cause")
    val cause: Cause,
    @SerializedName("additionalTravelTime")
    val additionalTravelTime: SummaryAdditionalTravelTime?,
    @SerializedName("alternativeTransport")
    val alternativeTransport: AlternativeTransport?,
    @SerializedName("advices")
    val advices: List<String>
)

@Keep
data class Situation(
    @SerializedName("label")
    val label: String
)

@Keep
data class Cause(
    @SerializedName("label")
    val label: String
)

@Keep
data class AlternativeTransport(
    @SerializedName("label")
    val label: String?,
    @SerializedName("shortLabel")
    val shortLabel: String?
)

@Keep
data class AlternativeTransportTimespan(
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String,
    @SerializedName("alternativeTransport")
    val alternativeTransport: AlternativeTransportData,
)

@Keep
data class AlternativeTransportData(
    @SerializedName("location")
    val location: List<LocationAlternatiefVervoer>,
)

@Keep
data class LocationAlternatiefVervoer(
    @SerializedName("station")
    val station: Station,
    @SerializedName("description")
    val description: String
)

@Keep
data class ExpectedDuration(
    @SerializedName("description")
    val description: String,
    @SerializedName("endTime")
    val endTime: String
)
