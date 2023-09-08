package com.yungert.treinplanner.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.yungert.treinplanner.presentation.Data.models.Link

@Keep
data class OvFietsResponseModel(
    @SerializedName("links")
    val links: Link,

    @SerializedName("payload")
    val payload: List<Location>,
)
@Keep
data class Location(
    @SerializedName("type")
    val type: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("identifiers")
    val identifiers: List<String>,

    @SerializedName("categories")
    val categories: List<String>,

    @SerializedName("locations")
    val locations: List<LocationItem>,

    @SerializedName("listLogoImage")
    val listLogoImage: ImageItem,

    @SerializedName("primaryColor")
    val primaryColor: String,

    @SerializedName("open")
    val open: String,

    @SerializedName("openingHours")
    val openingHours: List<OpeningHoursItem>,

    @SerializedName("keywords")
    val keywords: List<String>,

    @SerializedName("stationBound")
    val stationBound: Boolean,

    @SerializedName("headerImage")
    val headerImage: ImageItem,
)
@Keep
data class LocationItem(
    @SerializedName("distance")
    val distance: Double,

    @SerializedName("name")
    val name: String,

    @SerializedName("stationCode")
    val stationCode: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lng")
    val lng: Double,

    @SerializedName("open")
    val open: String,

    @SerializedName("link")
    val link: LinkItem,

    @SerializedName("thumbnail")
    val thumbnail: ImageItem,

    @SerializedName("description")
    val description: String,

    @SerializedName("openingHours")
    val openingHours: List<OpeningHoursItem>,

    @SerializedName("extra")
    val extra: Extra,

    @SerializedName("infoImages")
    val infoImages: List<InfoImage>,

    @SerializedName("nearbyMeLocationId")
    val nearbyMeLocationId: NearbyMeLocationId,

    @SerializedName("city")
    val city: String,

    @SerializedName("houseNumber")
    val houseNumber: String,

    @SerializedName("postalCode")
    val postalCode: String,

    @SerializedName("street")
    val street: String,

    @SerializedName("ovFiets")
    val ovFiets: Boolean
)
@Keep
data class LinkItem(
    @SerializedName("uri")
    val uri: String
)
@Keep
data class ImageItem(
    @SerializedName("uri")
    val uri: String
)
@Keep
data class OpeningHoursItem(
    @SerializedName("dayOfWeek")
    val dayOfWeek: Int,

    @SerializedName("startTime")
    val startTime: String,

    @SerializedName("endTime")
    val endTime: String,

    @SerializedName("closesNextDay")
    val closesNextDay: Boolean
)
@Keep
data class Extra(
    @SerializedName("serviceType")
    val serviceType: String,

    @SerializedName("rentalBikes")
    val rentalBikes: String,

    @SerializedName("locationCode")
    val locationCode: String,

    @SerializedName("type")
    val type: String
)
@Keep
data class InfoImage(
    @SerializedName("name")
    val name: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("body")
    val body: String
)
@Keep
data class NearbyMeLocationId(
    @SerializedName("value")
    val value: String,

    @SerializedName("type")
    val type: String
)
