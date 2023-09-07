package com.yungert.treinplanner.data.models

import androidx.annotation.Keep

@Keep
data class DisruptionResponseModel(
    val id: String,
    val type: String,
    val registrationTime: String,
    val releaseTime: String,
    val local: Boolean,
    val title: String,
    val titleSections: List<List<TitleSection>>,
    val topic: String,
    val isActive: Boolean,
    val start: String,
    val end: String,
    val phase: Phase,
    val impact: Impact,
    val expectedDuration: ExpectedDuration,
    val summaryAdditionalTravelTime: SummaryAdditionalTravelTime,
    val publicationSections: List<PublicationSection>,
    val timespans: List<Timespan>,
    val alternativeTransportTimespans: List<AlternativeTransportTimespans>
)

@Keep
data class TitleSection(
    val type: String,
    val value: String
)

@Keep
data class Phase(
    val id: String,
    val label: String
)

@Keep
data class Impact(
    val value: Int
)

@Keep
data class ExpectedDuration(
    val description: String,
    val endTime: String
)

@Keep
data class SummaryAdditionalTravelTime(
    val label: String,
    val shortLabel: String,
    val maximumDurationInMinutes: Int
)

@Keep
data class PublicationSection(
    val section: Section,
    val consequence: Consequence,
    val sectionType: String
)

@Keep
data class Section(
    val stations: List<Station>,
    val direction: String
)

@Keep
data class Station(
    val uicCode: String,
    val stationCode: String,
    val name: String,
    val coordinate: Coordinate,
    val countryCode: String
)

@Keep
data class Coordinate(
    val lat: Double,
    val lng: Double
)

@Keep
data class Consequence(
    val section: Section,
    val description: String,
    val level: String
)

@Keep
data class Timespan(
    val start: String,
    val end: String,
    val situation: Situation,
    val cause: Cause,
    val additionalTravelTime: SummaryAdditionalTravelTime,
    val advices: List<String>
)

@Keep
data class Situation(
    val label: String
)

@Keep
data class Cause(
    val label: String
)

@Keep
data class AlternativeTransportTimespans(
    val required: List<String>,
    val type: String,
    val properties: TimespanProperties,
    val description: String
)

@Keep
data class TimespanProperties(
    val start: StartProperty,
    val end: EndProperty,
    val alternativeTransport: AlternativeTransportProperty
)

@Keep
data class StartProperty(
    val type: String,
    val description: String,
    val format: String
)

@Keep
data class EndProperty(
    val type: String,
    val description: String,
    val format: String
)

@Keep
data class AlternativeTransportProperty(
    val `$ref`: String
)
