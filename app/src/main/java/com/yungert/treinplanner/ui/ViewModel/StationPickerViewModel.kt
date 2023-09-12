package com.yungert.treinplanner.presentation.ui.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.yungert.treinplanner.presentation.Data.Repository.NsApiRepository
import com.yungert.treinplanner.presentation.Data.Repository.SharedPreferencesRepository
import com.yungert.treinplanner.presentation.Data.api.NSApiClient
import com.yungert.treinplanner.presentation.Data.api.Resource
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.StationNamen
import com.yungert.treinplanner.presentation.ui.model.stationNamen
import com.yungert.treinplanner.presentation.utils.convertMeterNaarKilometer
import com.yungert.treinplanner.presentation.utils.hasInternetConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ViewStateStationPicker {
    object Loading : ViewStateStationPicker()
    data class Success(val details: List<StationNamen>) : ViewStateStationPicker()
    data class Problem(val exception: ErrorState?) : ViewStateStationPicker()
}


class StationPickerViewModel : ViewModel() {
    private val _viewState =
        MutableStateFlow<ViewStateStationPicker>(ViewStateStationPicker.Loading)
    val stations = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)
    private val sharedPreferencesRepository: SharedPreferencesRepository =
        SharedPreferencesRepository()

    fun getStationsZonderGps(vanStation: String?, context: Context) {
        val stations = mutableListOf<StationNamen>()
        viewModelScope.launch {
            stationNamen.forEach { station ->
                station.favorite = false
                if (sharedPreferencesRepository.getFavouriteStation(context = context, key = station.hiddenValue) != null) {
                    station.favorite = true
                }
                if (vanStation == null) {
                    stations.add(station)
                } else if (station.hiddenValue.lowercase() != vanStation.lowercase()) {
                    if (station.displayValue.lowercase() != vanStation.lowercase()) {
                        stations.add(station)
                    }
                }
            }
            val sortedStations = stations.sortedWith(
                compareByDescending<StationNamen> { it.favorite }
                    .thenBy { it.displayValue }
            )
            _viewState.value = ViewStateStationPicker.Success(sortedStations)
        }
    }

    @SuppressLint("MissingPermission")
    fun getStationsMetGps(vanStation: String?, context: Context) {
        if (!hasInternetConnection(context)) {
            _viewState.value = ViewStateStationPicker.Problem(ErrorState.NO_CONNECTION)
            return
        }
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                val latCoordinates = location?.latitude ?: 0.0
                val lngCoordinates = location?.longitude ?: 0.0
                if (latCoordinates != 0.0 && lngCoordinates != 0.0) {
                    fusedLocationClient.removeLocationUpdates(this)
                    getClosedStation(latCoordinates, lngCoordinates, context, vanStation)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    fun getClosedStation(
        latCoordinates: Double,
        lngCoordinates: Double,
        context: Context,
        vanStation: String?
    ) {
        var _stationNamen = stationNamen
        val stations = mutableListOf<StationNamen>()
        viewModelScope.launch {
            if (latCoordinates != 0.0 && lngCoordinates != 0.0) {
                nsApiRepository.fetchDichtbijzijndeStation(
                    lat = latCoordinates.toString(),
                    lng = lngCoordinates.toString()
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            var dichtbijZijndeStations = mutableListOf<StationNamen>()
                            result.data?.payload?.forEach { station ->
                                station.locations?.forEach { locatie ->
                                    if (locatie.distance != null && locatie.name != null && locatie.stationCode != null && locatie.naderenRadius != null) {
                                        dichtbijZijndeStations.add(
                                            StationNamen(
                                                displayValue = locatie.name,
                                                hiddenValue = locatie.stationCode,
                                                distance = locatie.distance,
                                                favorite = sharedPreferencesRepository.getFavouriteStation(
                                                    context = context,
                                                    key = locatie.stationCode
                                                ) != null,
                                                afstandTotGebruiker = convertMeterNaarKilometer(
                                                    locatie.distance
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                            dichtbijZijndeStations.forEach { station ->
                                if (vanStation == null) {
                                    stations.add(station)
                                } else if (station.hiddenValue != vanStation) {
                                    stations.add(station)
                                }
                            }
                            val sortedStations = stations.sortedWith(
                                compareBy<StationNamen> { it.distance }
                                    .thenByDescending { it.favorite }
                            )
                            _viewState.value = ViewStateStationPicker.Success(sortedStations)
                        }

                        is Resource.Loading -> {
                            _viewState.value = ViewStateStationPicker.Loading
                        }

                        is Resource.Error -> {
                            _viewState.value = ViewStateStationPicker.Problem(result.state)
                        }
                    }

                }
            }
        }
    }

    suspend fun toggleFavouriteStation(context: Context, item: StationNamen) {
        sharedPreferencesRepository.editFavouriteStation(
            context = context,
            key = item.hiddenValue,
            value = item.displayValue
        )
    }

    suspend fun setLastPlannedRoute(context: Context, key: String, value: String) {
        sharedPreferencesRepository.editLastRoute(
            context = context,
            key = key,
            value = value
        )
    }
}

