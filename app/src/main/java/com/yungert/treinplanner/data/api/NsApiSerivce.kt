package com.yungert.treinplanner.presentation.Data.api

import androidx.annotation.Keep
import com.yungert.treinplanner.data.models.DisruptionResponseModel
import com.yungert.treinplanner.data.models.OvFietsResponseModel
import com.yungert.treinplanner.presentation.Data.models.PlaceResponse
import com.yungert.treinplanner.presentation.Data.models.ReisAdviesApiResponse
import com.yungert.treinplanner.presentation.Data.models.RitDetailApiResponse
import com.yungert.treinplanner.presentation.Data.models.TripDetail
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

@Keep
interface NSApiService {
    @GET("reisinformatie-api/api/v3/trips/")
    suspend fun getReisadviezen(
        @Query("fromStation") startStation: String,
        @Query("toStation") eindStation: String,
        @Header("Ocp-Apim-Subscription-Key") authToken: String
    ): Response<ReisAdviesApiResponse>

    @GET("reisinformatie-api/api/v2/journey/")
    suspend fun getReis(
        @Query("id") id: String,
        @Query("departureUicCode") departureUicCode: String,
        @Query("arrivalUicCode") arrivalUicCode: String,
        @Query("dateTime") dateTime: String,
        @Header("Ocp-Apim-Subscription-Key") authToken: String
    ): Response<RitDetailApiResponse>

    @GET("reisinformatie-api/api/v3/trips/trip/")
    suspend fun getSingleReisById(
        @Query("ctxRecon") id: String,
        @Header("Ocp-Apim-Subscription-Key") authToken: String
    ): Response<TripDetail>

    @GET("places-api/v2/places/")
    suspend fun getDichtbijzijndeStation(
        @Query("lat") lat: String,
        @Query("lng") lng: String,
        @Query("limit") limit: Int = 5,
        @Query("radius") radius: Int = 10000,
        @Query("details") details: Boolean = false,
        @Query("name") name: String = "Stations",
        @Header("Ocp-Apim-Subscription-Key") authToken: String
    ): Response<PlaceResponse>

    @GET("reisinformatie-api/api/v3/disruptions/")
    suspend fun getDisruptionById(
        @Query("id") disruptionId: String,
        @Query("type") type: String,
        @Header("Ocp-Apim-Subscription-Key") authToken: String
    ): Response<List<DisruptionResponseModel>>

    @GET("places-api/v2/ovfiets/")
    suspend fun getOvFietsByStationId(
        @Query("station_code") stationId: String,
        @Header("Ocp-Apim-Subscription-Key") authToken: String
    ): Response<OvFietsResponseModel>
}