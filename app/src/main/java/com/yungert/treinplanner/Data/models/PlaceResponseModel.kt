package com.yungert.treinplanner.presentation.Data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class PlaceResponse(
    @SerializedName("payload")
    val payload: List<Data>?
)

@Keep
data class Data(
    @SerializedName("type")
    val type: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("identifiers")
    val identifiers: List<String>?,
    @SerializedName("locations")
    val locations: List<Locatie>?,
    @SerializedName("open")
    val open: String?,
    @SerializedName("keywords")
    val keywords: List<String>?,
    @SerializedName("stationBound")
    val stationBound: Boolean?,
    @SerializedName("advertImages")
    val advertImages: List<String>?,
    @SerializedName("infoImages")
    val infoImages: List<String>?
)
@Keep
data class Locatie(
    @SerializedName("distance")
    val distance: Double?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("stationCode")
    val stationCode: String?,
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?,
    @SerializedName("open")
    val open: String?,
    @SerializedName("infoImages")
    val infoImages: List<String>?,
    @SerializedName("apps")
    val apps: List<String>?,
    @SerializedName("sites")
    val sites: List<Site>?,
    @SerializedName("extraInfo")
    val extraInfo: List<String>?,
    @SerializedName("code")
    val code: String?,
    @SerializedName("namen")
    val namen: Namen?,
    @SerializedName("naderenRadius")
    val naderenRadius: Double?,
    @SerializedName("heeftFaciliteiten")
    val heeftFaciliteiten: Boolean?,
    @SerializedName("heeftVertrektijden")
    val heeftVertrektijden: Boolean?,
    @SerializedName("heeftReisassistentie")
    val heeftReisassistentie: Boolean?,
    @SerializedName("stationType")
    val stationType: String?,
    @SerializedName("land")
    val land: String?,
    @SerializedName("synoniemen")
    val synoniemen: List<String>?,
    @SerializedName("UICCode")
    val uicCode: String?,
    @SerializedName("EVACode")
    val evaCode: String?
)
@Keep
data class Site(
    @SerializedName("qualifier")
    val qualifier: String?,
    @SerializedName("url")
    val url: String?
)
@Keep
data class Namen(
    @SerializedName("middel")
    val middel: String?,
    @SerializedName("kort")
    val kort: String?,
    @SerializedName("lang")
    val lang: String?
)


