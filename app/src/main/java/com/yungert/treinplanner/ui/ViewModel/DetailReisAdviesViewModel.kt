package com.yungert.treinplanner.presentation.ui.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.Data.Repository.NsApiRepository
import com.yungert.treinplanner.presentation.Data.api.NSApiClient
import com.yungert.treinplanner.presentation.Data.api.Resource
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.DetailReisAdvies
import com.yungert.treinplanner.presentation.ui.model.RitDetail
import com.yungert.treinplanner.presentation.utils.MessageType
import com.yungert.treinplanner.presentation.utils.TripStatus
import com.yungert.treinplanner.presentation.utils.calculateTimeDiff
import com.yungert.treinplanner.presentation.utils.formatTime
import com.yungert.treinplanner.presentation.utils.hasInternetConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ViewStateDetailReisAdvies {
    object Loading : ViewStateDetailReisAdvies()
    data class Success(val details: DetailReisAdvies) : ViewStateDetailReisAdvies()
    data class Problem(val exception: ErrorState?) : ViewStateDetailReisAdvies()
}

class DetailReisAdviesViewModel : ViewModel() {
    private val _viewState =
        MutableStateFlow<ViewStateDetailReisAdvies>(ViewStateDetailReisAdvies.Loading)
    val reisavies = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)

    fun getReisadviesDetail(reisAdviesId: String, context: Context) {
        if (!hasInternetConnection(context)) {
            _viewState.value = ViewStateDetailReisAdvies.Problem(ErrorState.NO_CONNECTION)
            return
        }
        viewModelScope.launch {
            nsApiRepository.fetchSingleTripById(reisadviesId = reisAdviesId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        var ritten = mutableListOf<RitDetail>()
                        var eindTijd = ""
                        var detailReisAdvies = DetailReisAdvies(
                            opgeheven = result.data?.status?.let { TripStatus.fromValue(it) } == TripStatus.CANCELLED,
                            redenOpheffen = result.data?.primaryMessage?.title,
                            rit = ritten,
                            hoofdBericht = result.data?.primaryMessage?.message?.text,
                            eindTijdVerstoring = eindTijd
                        )
                        if(MessageType.fromValue(result.data?.primaryMessage?.message?.type) == MessageType.DISRUPTION) {
                            result.data?.primaryMessage?.message?.id?.let {
                                nsApiRepository.fetchDisruptionById(it).collect { result ->
                                    detailReisAdvies.eindTijdVerstoring = formatTime(result.data?.expectedDuration?.endTime)

                                }
                            }
                        }
                        result.data?.legs?.forEachIndexed { index, advies ->
                            var ritDetail: RitDetail? = null
                            var overstap = ""
                            val alternatievVervoerInzet = advies.alternativeTransport
                            if (index > 0) {
                                var aankomstVorigeTrein = result.data.legs[index - 1].destination.actualDateTime ?: result.data.legs[index - 1].destination.plannedDateTime
                                overstap =
                                    calculateTimeDiff(
                                        aankomstVorigeTrein,
                                        advies.origin.actualDateTime ?: advies.origin.plannedDateTime
                                    )
                            }
                            ritDetail = RitDetail(
                                treinOperator = advies.product.operatorName,
                                treinOperatorType = if (!alternatievVervoerInzet) advies.product.categoryCode else advies.product.longCategoryName,
                                ritNummer = if (!alternatievVervoerInzet) advies.product.number else "",
                                eindbestemmingTrein = advies.direction,
                                naamVertrekStation = advies.origin.name,
                                geplandeVertrektijd = formatTime(advies.origin.plannedDateTime),
                                vertrekSpoor = if (alternatievVervoerInzet) "" else advies.origin.actualTrack ?: advies.origin.plannedTrack,
                                naamAankomstStation = advies.destination.name,
                                geplandeAankomsttijd = formatTime(advies.destination.actualDateTime ?: advies.destination.plannedDateTime),
                                aankomstSpoor = if (alternatievVervoerInzet) "" else advies.destination.actualTrack
                                    ?: advies.destination.plannedTrack,
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
                                alternatiefVervoer = alternatievVervoerInzet,
                                ritId = advies.journeyDetailRef,
                                vertrekStationUicCode = advies.origin.uicCode,
                                aankomstStationUicCode = advies.destination.uicCode,
                                datum = advies.origin.plannedDateTime,
                                overstapTijd = overstap,
                                kortereTreinDanGepland = advies.shorterStock,
                                opgeheven = advies.cancelled,
                                punctualiteit = advies.punctuality ?: 0.0,
                            )

                            ritDetail.let { ritten.add(it) }
                        }
                        detailReisAdvies.rit = ritten
                        _viewState.value = ViewStateDetailReisAdvies.Success(detailReisAdvies)

                    }

                    is Resource.Loading -> {
                        _viewState.value = ViewStateDetailReisAdvies.Loading
                    }

                    is Resource.Error -> {
                        _viewState.value = ViewStateDetailReisAdvies.Problem(result.state)
                    }
                }

            }
        }
    }
}