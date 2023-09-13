package com.yungert.treinplanner.presentation.Data.Repository

import com.yungert.treinplanner.BuildConfig
import com.yungert.treinplanner.data.models.DisruptionResponseModel
import com.yungert.treinplanner.data.models.OvFietsResponseModel
import com.yungert.treinplanner.presentation.Data.api.NSApiClient
import com.yungert.treinplanner.presentation.Data.api.Resource
import com.yungert.treinplanner.presentation.Data.models.PlaceResponse
import com.yungert.treinplanner.presentation.Data.models.ReisAdviesApiResponse
import com.yungert.treinplanner.presentation.Data.models.RitDetailApiResponse
import com.yungert.treinplanner.presentation.Data.models.TripDetail
import com.yungert.treinplanner.presentation.ui.ErrorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class NsApiRepository(private val nsApiClient: NSApiClient) {
    val apiKey = BuildConfig.API_KEY_NS
    suspend fun fetchReisAdviezen(
        vetrekStation: String,
        aankomstStation: String
    ): Flow<Resource<ReisAdviesApiResponse>> {
        return flow {
            emit(Resource.Loading())
            try {
                val apiResult = nsApiClient.apiService.getReisadviezen(
                    startStation = vetrekStation,
                    eindStation = aankomstStation,
                    authToken = apiKey
                )
                if (apiResult.isSuccessful) {
                    if (apiResult.body() != null) {
                        emit(Resource.Success(apiResult.body()!!))
                    }
                } else {
                    emit(Resource.Error(ErrorState.SERVER_ERROR))
                }
            } catch (e: IOException) {
                emit(Resource.Error(ErrorState.SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchSingleTripById(reisadviesId: String): Flow<Resource<TripDetail>> {
        return flow {
            emit(Resource.Loading())
            try {
                val apiResult =
                    nsApiClient.apiService.getSingleReisById(id = reisadviesId, authToken = apiKey)
                if (apiResult.isSuccessful) {
                    if (apiResult.body() != null) {
                        emit(Resource.Success(apiResult.body()!!))
                    }
                } else {
                    emit(Resource.Error(ErrorState.SERVER_ERROR))
                }
            } catch (e: IOException) {
                emit(Resource.Error(ErrorState.SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchDichtbijzijndeStation(
        lat: String,
        lng: String
    ): Flow<Resource<PlaceResponse>> {
        return flow {
            emit(Resource.Loading())
            try {
                val apiResult = nsApiClient.apiService.getDichtbijzijndeStation(
                    lat = lat,
                    lng = lng,
                    authToken = apiKey
                )
                if (apiResult.isSuccessful) {
                    if (apiResult.body() != null) {
                        emit(Resource.Success(apiResult.body()!!))
                    }
                } else {
                    emit(Resource.Error(ErrorState.SERVER_ERROR))
                }
            } catch (e: IOException) {
                emit(Resource.Error(ErrorState.SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchRitById(
        depatureUicCode: String,
        arrivalUicCode: String,
        reisId: String,
        dateTime: String
    ): Flow<Resource<RitDetailApiResponse>> {
        return flow {
            emit(Resource.Loading())
            try {
                val apiResult = nsApiClient.apiService.getReis(
                    departureUicCode = depatureUicCode,
                    arrivalUicCode = arrivalUicCode,
                    dateTime = dateTime,
                    id = reisId,
                    authToken = apiKey
                )
                if (apiResult.isSuccessful) {
                    if (apiResult.body() != null) {
                        emit(Resource.Success(apiResult.body()!!))
                    }
                } else {
                    emit(Resource.Error(ErrorState.SERVER_ERROR))
                }
            } catch (e: IOException) {
                emit(Resource.Error(ErrorState.SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchDisruptionById(
        disruptionId: String,
        type: String
    ): Flow<Resource<DisruptionResponseModel>> {
        return flow {
            emit(Resource.Loading())
            try {
                val apiResult = nsApiClient.apiService.getDisruptionById(
                    disruptionId = disruptionId,
                    type = type,
                    authToken = apiKey
                )
                if (apiResult.isSuccessful) {
                    if (apiResult.body() != null) {
                        emit(Resource.Success(apiResult.body()!!))
                    }
                } else {
                    emit(Resource.Error(ErrorState.SERVER_ERROR))
                }
            } catch (e: IOException) {
                emit(Resource.Error(ErrorState.SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchOvFietsByStationId(stationId: String): Flow<Resource<OvFietsResponseModel>> {
        return flow {
            emit(Resource.Loading())
            try {
                val apiResult = nsApiClient.apiService.getOvFietsByStationId(
                    stationId = stationId,
                    authToken = apiKey
                )
                if (apiResult.isSuccessful) {
                    if (apiResult.body() != null) {
                        emit(Resource.Success(apiResult.body()!!))
                    }
                } else {
                    emit(Resource.Error(ErrorState.SERVER_ERROR))
                }
            } catch (e: IOException) {
                emit(Resource.Error(ErrorState.SERVER_ERROR))
            }
        }.flowOn(Dispatchers.IO)
    }
}