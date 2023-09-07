package com.yungert.treinplanner.presentation.ui.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.Data.Repository.NsApiRepository
import com.yungert.treinplanner.presentation.Data.api.NSApiClient
import com.yungert.treinplanner.presentation.Data.api.Resource
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.MaterieelInzet
import com.yungert.treinplanner.presentation.ui.model.StopOpRoute
import com.yungert.treinplanner.presentation.ui.model.TreinRitDetail
import com.yungert.treinplanner.presentation.utils.DrukteIndicatorFormatter
import com.yungert.treinplanner.presentation.utils.StopKindType
import com.yungert.treinplanner.presentation.utils.StopStatusType
import com.yungert.treinplanner.presentation.utils.calculateDelay
import com.yungert.treinplanner.presentation.utils.formatTime
import com.yungert.treinplanner.presentation.utils.hasInternetConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ViewStateRitDetail {
    object Loading : ViewStateRitDetail()
    data class Success(val details: TreinRitDetail) : ViewStateRitDetail()
    data class Problem(val exception: ErrorState?) : ViewStateRitDetail()
}

class RitDetailViewModel : ViewModel() {
    private val _viewState = MutableStateFlow<ViewStateRitDetail>(ViewStateRitDetail.Loading)
    val stops = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)
    fun getReisadviezen(
        depatureUicCode: String,
        arrivalUicCode: String,
        reisId: String,
        dateTime: String,
        context: Context
    ) {
        if (!hasInternetConnection(context)) {
            _viewState.value = ViewStateRitDetail.Problem(ErrorState.NO_CONNECTION)
            return
        }
        viewModelScope.launch {
            nsApiRepository.fetchRitById(
                depatureUicCode = depatureUicCode,
                arrivalUicCode = arrivalUicCode,
                dateTime = dateTime,
                reisId = reisId
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        var treinStops = mutableListOf<StopOpRoute>()
                        var treinRit = TreinRitDetail(
                            eindbestemmingTrein = result.data?.payload?.stops?.getOrNull(0)?.destination
                                ?: "",
                            ritNummer = result.data?.payload?.productNumbers?.getOrNull(0) ?: "0",
                            stops = treinStops,
                            opgeheven = false,
                            ingekort = false,
                            aantalTreinDelen = 0,
                            aantalZitplaatsen = 0,
                            materieelType = "",
                            materieelInzet = emptyList()
                        )
                        var stopOpRoute = false
                        var ingezetMaterieel = mutableListOf<MaterieelInzet>()
                        result.data?.payload?.stops?.forEach { stop ->
                            if (StopStatusType.fromValue(stop.status) == StopStatusType.PASSING) {
                                return@forEach
                            }
                            if (stop.kind != null) {
                                if (StopKindType.fromValue(stop.kind) == StopKindType.DEPARTURE) {
                                    stopOpRoute = true
                                    if (stop.departures.getOrNull(0)?.cancelled == true) {
                                        treinRit.opgeheven = true
                                    }
                                    treinRit.aantalTreinDelen = stop.actualStock?.numberOfParts
                                        ?: stop.plannedStock?.numberOfParts ?: 0
                                    treinRit.aantalZitplaatsen = stop.actualStock?.numberOfSeats
                                        ?: stop.plannedStock?.numberOfSeats ?: 0
                                    treinRit.materieelType =
                                        stop.actualStock?.trainType ?: stop.plannedStock?.trainType
                                                ?: ""
                                    if (stop.actualStock != null) {
                                        stop.actualStock.trainParts.forEach { part ->
                                            ingezetMaterieel.add(
                                                MaterieelInzet(
                                                    treinNummer = part.stockIdentifier,
                                                    eindBestemmingTreindeel = part.destination?.name
                                                        ?: stop.destination
                                                )
                                            )
                                        }
                                    } else {
                                        stop.plannedStock?.trainParts?.forEach { part ->
                                            ingezetMaterieel.add(
                                                MaterieelInzet(
                                                    treinNummer = part.stockIdentifier,
                                                    eindBestemmingTreindeel = part.destination?.name
                                                        ?: stop.destination
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            treinRit.materieelInzet = ingezetMaterieel
                            if (!stopOpRoute) {
                                return@forEach
                            }
                            val departure = stop.departures.getOrNull(0)
                            val arrival = stop.arrivals.getOrNull(0)

                            val materieelNummer = mutableListOf<String>()
                            if (stop.actualStock != null || stop.plannedStock != null) {
                                var materieel = stop.actualStock ?: stop.plannedStock
                                materieel?.trainParts?.forEach { part ->
                                    materieelNummer.add(part.stockIdentifier)
                                }
                            }
                            if (stop.actualStock?.hasSignificantChange == true) {
                                treinRit.ingekort = true
                            }

                            treinStops.add(
                                StopOpRoute(
                                    stationNaam = stop.stop.name,
                                    spoor = departure?.actualTrack ?: departure?.plannedTrack
                                    ?: arrival?.actualTrack ?: arrival?.plannedTrack,
                                    actueleAankomstTijd = formatTime(arrival?.actualTime),
                                    geplandeAankomstTijd = formatTime(arrival?.plannedTime),
                                    aankomstVertraging = calculateDelay(
                                        arrival?.delayInSeconds?.toLong() ?: 0
                                    ),
                                    actueleVertrekTijd = formatTime(departure?.actualTime),
                                    geplandeVertrektTijd = formatTime(departure?.plannedTime),
                                    vertrekVertraging = calculateDelay(
                                        departure?.delayInSeconds?.toLong() ?: 0
                                    ),
                                    drukte = DrukteIndicatorFormatter(
                                        stop.departures.getOrNull(0)?.crowdForecast,
                                        compactLayout = true
                                    ),
                                    punctualiteit = arrival?.punctuality?.toString() ?: "0",
                                    opgeheven = arrival?.cancelled ?: departure?.cancelled ?: false,
                                    status = StopStatusType.fromValue(stop.status)
                                )
                            )
                            if (stop.kind != null) {
                                if (StopKindType.fromValue(stop.kind) == StopKindType.ARRIVAL) {
                                    stopOpRoute = false
                                    if (stop.arrivals.getOrNull(0)?.cancelled == true) {
                                        treinRit.opgeheven = true
                                    }
                                }
                            }
                        }
                        _viewState.value = ViewStateRitDetail.Success(treinRit)
                    }

                    is Resource.Loading -> {
                        _viewState.value = ViewStateRitDetail.Loading
                    }

                    is Resource.Error -> {
                        _viewState.value = ViewStateRitDetail.Problem(result.state)
                    }
                }

            }
        }
    }
}