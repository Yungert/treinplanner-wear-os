package com.yungert.treinplanner.presentation.ui.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.Data.Repository.NsApiRepository
import com.yungert.treinplanner.presentation.Data.Repository.SharedPreferencesRepository
import com.yungert.treinplanner.presentation.Data.api.NSApiClient
import com.yungert.treinplanner.presentation.Data.api.Resource
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.AlternatiefVervoer
import com.yungert.treinplanner.presentation.ui.model.DataEindbestemmingStation
import com.yungert.treinplanner.presentation.ui.model.DetailReisadvies
import com.yungert.treinplanner.presentation.ui.model.OvFiets
import com.yungert.treinplanner.presentation.ui.model.RitDetail
import com.yungert.treinplanner.presentation.utils.ShorterStockClassificationType
import com.yungert.treinplanner.presentation.utils.TransferType
import com.yungert.treinplanner.presentation.utils.TripStatus
import com.yungert.treinplanner.presentation.utils.calculateTimeDiff
import com.yungert.treinplanner.presentation.utils.formatTime
import com.yungert.treinplanner.presentation.utils.hasInternetConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ViewStateDetailReisadvies {
    object Loading : ViewStateDetailReisadvies()
    data class Success(val details: DetailReisadvies) : ViewStateDetailReisadvies()
    data class Problem(val exception: ErrorState?) : ViewStateDetailReisadvies()
}

class DetailReisadviesViewModel : ViewModel() {
    private val _viewState =
        MutableStateFlow<ViewStateDetailReisadvies>(ViewStateDetailReisadvies.Loading)
    val reisavies = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)
    private val sharedPreferencesRepository: SharedPreferencesRepository =
        SharedPreferencesRepository()

    fun getReisadviesDetail(reisAdviesId: String, context: Context) {
        if (!hasInternetConnection(context)) {
            _viewState.value = ViewStateDetailReisadvies.Problem(ErrorState.NO_CONNECTION)
            return
        }
        viewModelScope.launch {
            sharedPreferencesRepository.editReisadviesId(
                context = context,
                key = "reisadviesId",
                value = reisAdviesId
            )
            nsApiRepository.fetchSingleTripById(reisadviesId = reisAdviesId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        var ritten = mutableListOf<RitDetail>()
                        var eindTijd = ""

                        var ovFiets = mutableListOf<OvFiets>()
                        var dataEindbestemmingStation = DataEindbestemmingStation(
                            ovFiets = ovFiets,
                            ritPrijsInEuro = if (result.data?.productFare?.priceInCents == null) "-" else String.format(
                                "%.2f",
                                result.data.productFare.priceInCents / 100.0
                            )
                        )

                        nsApiRepository.fetchOvFietsByStationId(
                            stationId = result.data?.legs?.getOrNull(
                                result.data.legs.size - 1
                            )?.destination?.stationCode ?: ""
                        ).collect { result ->
                            result.data?.payload?.forEach { place ->
                                place.locations.forEach { location ->
                                    ovFiets.add(
                                        OvFiets(
                                            aantalOvFietsen = location.extra.rentalBikes,
                                            locatieFietsStalling = if (location.street?.trim() != null && location.houseNumber != null) location.street.trim() + " " + location.houseNumber else location.description
                                        )
                                    )
                                }
                            }
                            dataEindbestemmingStation.ovFiets = ovFiets
                        }


                        var detailReisAdvies = DetailReisadvies(
                            opgeheven = result.data?.status?.let { TripStatus.fromValue(it) } == TripStatus.CANCELLED,
                            redenOpheffen = result.data?.primaryMessage?.title,
                            rit = ritten,
                            hoofdBericht = result.data?.primaryMessage?.message?.text,
                            eindTijdVerstoring = eindTijd,
                            dataEindStation = dataEindbestemmingStation,
                            dataAlternatiefVervoer = AlternatiefVervoer(
                                advies = null,
                                soortVervoer = null,
                                vertrekLocatieStation = null,
                                minumimExtraReistijd = null,
                                maximumExtraReistijd = null
                            )
                        )
                        result.data?.primaryMessage?.message?.id?.let { id ->
                            result.data.primaryMessage.message.type.let { type ->
                                nsApiRepository.fetchDisruptionById(id, type).collect { res ->
                                    detailReisAdvies.eindTijdVerstoring =
                                        formatTime(
                                            res.data?.expectedDuration?.endTime ?: res.data?.end,
                                            rekeningHoudenMetDag = true
                                        )

                                    res.data?.timespans?.forEach { span ->
                                        detailReisAdvies.dataAlternatiefVervoer?.soortVervoer =
                                            span.alternativeTransport?.shortLabel
                                        span.advices.forEach { advice ->
                                            detailReisAdvies.dataAlternatiefVervoer?.advies =
                                                advice.trim()
                                        }
                                        detailReisAdvies.dataAlternatiefVervoer?.maximumExtraReistijd =
                                            span.additionalTravelTime?.maximumDurationInMinutes?.toString()
                                        detailReisAdvies.dataAlternatiefVervoer?.minumimExtraReistijd =
                                            span.additionalTravelTime?.minimumDurationInMinutes?.toString()

                                    }
                                    res.data?.alternativeTransportTimespans?.forEach { time ->
                                        val locatie = time.alternativeTransport.location.find {
                                            it.station.name == result.data.legs[result.data.legs.size - 1].origin.name
                                        }
                                        if (locatie?.description == null) {
                                            return@forEach
                                        }
                                        detailReisAdvies.dataAlternatiefVervoer?.vertrekLocatieStation =
                                            locatie.description
                                    }
                                }
                            }
                        }




                        result.data?.legs?.forEachIndexed { index, advies ->
                            var ritDetail: RitDetail? = null
                            var overstap = ""
                            val alternatiefVervoerInzet = advies.alternativeTransport
                            if (index > 0) {
                                var aankomstVorigeTrein =
                                    result.data.legs[index - 1].destination.actualDateTime
                                        ?: result.data.legs[index - 1].destination.plannedDateTime
                                overstap =
                                    calculateTimeDiff(
                                        aankomstVorigeTrein,
                                        advies.origin.actualDateTime
                                            ?: advies.origin.plannedDateTime
                                    )
                            }
                            var overstapCrossPlatform = false
                            var overstapMogelijkheid = true

                            advies.transferMessages?.forEach { transfer ->
                                if (TransferType.fromValue(transfer.type) == TransferType.CROSS_PLATFORM) {
                                    overstapCrossPlatform = true
                                }
                                if (TransferType.fromValue(transfer.type) == TransferType.IMPOSSIBLE_TRANSFER) {
                                    overstapMogelijkheid = false
                                }
                            }
                            ritDetail = RitDetail(
                                treinOperator = advies.product.operatorName,
                                treinOperatorType = if (!alternatiefVervoerInzet) advies.product.categoryCode else advies.product.longCategoryName.trim(),
                                ritNummer = if (!alternatiefVervoerInzet) advies.product.number else "",
                                eindbestemmingTrein = advies.direction,
                                naamVertrekStation = advies.origin.name,
                                geplandeVertrektijd = formatTime(advies.origin.plannedDateTime),
                                geplandVertrekSpoor = if (alternatiefVervoerInzet) "" else advies.origin.plannedTrack,
                                actueelVertrekSpoor = if (alternatiefVervoerInzet) "" else advies.origin.actualTrack,
                                naamAankomstStation = advies.destination.name,
                                geplandeAankomsttijd = formatTime(
                                    advies.destination.actualDateTime
                                        ?: advies.destination.plannedDateTime
                                ),
                                geplandAankomstSpoor = if (alternatiefVervoerInzet) "" else advies.destination.plannedTrack,
                                actueelAankomstspoor = if (alternatiefVervoerInzet) "" else advies.destination.plannedTrack,
                                vertrekVertraging = calculateTimeDiff(
                                    advies.origin.plannedDateTime,
                                    advies.origin.actualDateTime
                                ),
                                aankomstVertraging = calculateTimeDiff(
                                    advies.destination.plannedDateTime,
                                    advies.destination.actualDateTime
                                ),
                                berichten = advies.messages,
                                transferBericht = advies.transferMessages,
                                alternatiefVervoer = alternatiefVervoerInzet,
                                ritId = advies.journeyDetailRef,
                                vertrekStationUicCode = advies.origin.uicCode,
                                aankomstStationUicCode = advies.destination.uicCode,
                                datum = advies.origin.plannedDateTime,
                                overstapTijd = overstap,
                                kortereTreinDanGepland = advies.shorterStockClassification?.let {
                                    ShorterStockClassificationType.fromValue(
                                        it
                                    )
                                } ?: ShorterStockClassificationType.FALSE,
                                opgeheven = advies.cancelled,
                                punctualiteit = advies.punctuality ?: 0.0,
                                crossPlatform = overstapCrossPlatform,
                                overstapMogelijk = overstapMogelijkheid,
                            )

                            ritDetail.let { ritten.add(it) }
                        }
                        detailReisAdvies.rit = ritten
                        _viewState.value = ViewStateDetailReisadvies.Success(detailReisAdvies)
                    }

                    is Resource.Loading -> {
                        _viewState.value = ViewStateDetailReisadvies.Loading
                    }

                    is Resource.Error -> {
                        _viewState.value = ViewStateDetailReisadvies.Problem(result.state)
                    }
                }

            }
        }
    }
}