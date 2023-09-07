package com.yungert.treinplanner.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Tram
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.ViewModel.RitDetailViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateRitDetail
import com.yungert.treinplanner.presentation.ui.model.TreinRitDetail
import com.yungert.treinplanner.presentation.utils.DrukteIndicatorComposable
import com.yungert.treinplanner.presentation.utils.Foutmelding
import com.yungert.treinplanner.presentation.utils.LoadingScreen
import com.yungert.treinplanner.presentation.utils.StopStatusType
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize
import kotlinx.coroutines.launch

@Composable
fun ShowRitDetail(
    depatureUicCode: String,
    arrivalUicCode: String,
    reisId: String,
    dateTime: String,
    viewModel: RitDetailViewModel,
    navController: NavController,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val ritDetailViewModel = viewModel
    when (val result = ritDetailViewModel.stops.collectAsState().value) {
        is ViewStateRitDetail.Success -> {
            DisplayRitDetail(
                ritDetail = result.details,
                navController = navController,
                viewModel = viewModel,
                depatureUicCode = depatureUicCode,
                arrivalUicCode = arrivalUicCode,
                reisId = reisId,
                dateTime = dateTime
            )
        }

        else -> {
            val context = LocalContext.current
            DisposableEffect(lifeCycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        viewModel.getReisadviezen(
                            depatureUicCode = depatureUicCode,
                            arrivalUicCode = arrivalUicCode,
                            reisId = reisId,
                            dateTime = dateTime,
                            context = context
                        )
                    }
                }
                lifeCycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifeCycleOwner.lifecycle.removeObserver(observer)
                }
            }

            when (val response = viewModel.stops.collectAsState().value) {
                is ViewStateRitDetail.Loading -> LoadingScreen(loadingText = stringResource(id = R.string.laadt_text_rit_gegevens))
                is ViewStateRitDetail.Problem -> {
                    Foutmelding(
                        errorState = response.exception,
                        onClick = {
                            viewModel.getReisadviezen(
                                depatureUicCode = depatureUicCode,
                                arrivalUicCode = arrivalUicCode,
                                reisId = reisId,
                                dateTime = dateTime,
                                context = context
                            )
                        })
                }

                is ViewStateRitDetail.Success -> {
                    DisplayRitDetail(
                        ritDetail = response.details,
                        navController = navController,
                        viewModel = viewModel,
                        depatureUicCode = depatureUicCode,
                        arrivalUicCode = arrivalUicCode,
                        reisId = reisId,
                        dateTime = dateTime
                    )
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DisplayRitDetail(
    ritDetail: TreinRitDetail,
    navController: NavController,
    viewModel: RitDetailViewModel,
    depatureUicCode: String,
    arrivalUicCode: String,
    reisId: String,
    dateTime: String,
) {
    val focusRequester = remember { FocusRequester() }
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var treinStops = ritDetail

    val context = LocalContext.current
    coroutineScope.launch {
        listState.scrollToItem(index = 2)
    }
    fun refresh() = refreshScope.launch {
        viewModel.getReisadviezen(
            depatureUicCode = depatureUicCode,
            arrivalUicCode = arrivalUicCode,
            reisId = reisId,
            dateTime = dateTime,
            context = context
        )
        refreshing = true
        when (val response = viewModel.stops.value) {
            is ViewStateRitDetail.Success -> {
                treinStops = response.details
                refreshing = false
            }

            else -> {}
        }

    }

    val state = rememberPullRefreshState(refreshing, ::refresh)
    Box(modifier = Modifier.pullRefresh(state = state)) {
        Scaffold(
            positionIndicator = {
                PositionIndicator(scalingLazyListState = listState)
            }
        ) {
            ScalingLazyColumn(
                anchorType = ScalingLazyListAnchorType.ItemStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .onRotaryScrollEvent {
                        coroutineScope.launch {
                            listState.scrollBy(it.verticalScrollPixels)
                        }
                        true
                    }
                    .focusRequester(focusRequester)
                    .focusable(),
                state = listState)
            {
                item {
                    ListHeader {
                        Text(
                            text = stringResource(id = R.string.label_rit) + " " + ritDetail.ritNummer + " " + stringResource(
                                id = R.string.label_eindbestemming_trein
                            ) + " " + ritDetail.eindbestemmingTrein,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (ritDetail.opgeheven) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Icon",
                                tint = Color.Red,
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .size(iconSize)
                            )
                            Text(
                                text = stringResource(id = R.string.label_rijdt_niet),
                                style = fontsizeLabelCard,
                                textAlign = TextAlign.Left,
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                //showExtraInfo.value = !showExtraInfo.value
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                                Text(
                                    text = ritDetail.materieelType + "-" + ritDetail.aantalTreinDelen+ " ",
                                    style = fontsizeLabelCard
                                )


                                Icon(
                                    imageVector = Icons.Default.AirlineSeatReclineNormal,
                                    contentDescription = "Icon",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(iconSize)
                                        .padding(vertical = 2.dp)
                                )
                                Text(
                                    text = if(ritDetail.aantalZitplaatsen.toString() != "") ritDetail.aantalZitplaatsen.toString() else stringResource(id = R.string.label_onbekend),
                                    style = fontsizeLabelCard
                                )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Train,
                                contentDescription = "Icon",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(iconSize)
                                    .padding(vertical = 2.dp)
                            )
                            ritDetail.materieelInzet.forEachIndexed { index, materieel ->
                                Text(
                                    text = materieel.treinNummer,
                                    style = fontsizeLabelCard
                                )
                                if (index < (ritDetail.materieelInzet?.size?.minus(1) ?: 0)) {
                                    Text(
                                        text = ", ",
                                        style = fontsizeLabelCard
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ritDetail.materieelInzet.forEach { materieelInzet ->
                                if(materieelInzet.eindBestemmingTreindeel != ritDetail.eindbestemmingTrein){
                                    Text(
                                        text = stringResource(id = R.string.label_treinstel) + " " +  materieelInzet.treinNummer + " " + stringResource(id = R.string.label_rijdt_tot) + " " + materieelInzet.eindBestemmingTreindeel,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
                treinStops.stops.forEachIndexed { index, stop ->
                    val tekstKleur = if(stop.opgeheven) Color.Red else Color.White
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 15.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {

                                Text(
                                    text = if (stop.geplandeAankomstTijd != "") stop.geplandeAankomstTijd else stop.geplandeVertrektTijd,
                                    style = fontsizeLabelCard,
                                    color = tekstKleur
                                )
                                Text(
                                    text = if (stop.geplandeAankomstTijd != "") stop.aankomstVertraging else stop.vertrekVertraging,
                                    style = fontsizeLabelCard,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                )
                                if (stop.geplandeAankomstTijd != "" && stop.geplandeVertrektTijd != "") {
                                    if (stop.geplandeAankomstTijd != stop.geplandeVertrektTijd) {
                                        Icon(
                                            imageVector = Icons.Default.East,
                                            contentDescription = "Icon",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(21.dp)
                                        )
                                        Text(
                                            text = stop.geplandeVertrektTijd,
                                            style = fontsizeLabelCard,
                                            modifier = Modifier.padding(horizontal = 1.dp),
                                            color = tekstKleur
                                        )
                                        Text(
                                            text = stop.vertrekVertraging,
                                            style = fontsizeLabelCard,
                                            color = Color.Red,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                                if (stop.spoor != null) {
                                    Icon(
                                        imageVector = Icons.Default.Tram,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(end = 1.dp)
                                            .padding(start = 2.dp)
                                    )
                                    Text(
                                        text = stop.spoor + " ",
                                        style = fontsizeLabelCard,
                                        color = tekstKleur
                                    )
                                }
                                DrukteIndicatorComposable(
                                    aantalIconen = stop.drukte.aantalIconen,
                                    icon = stop.drukte.icon,
                                    color = stop.drukte.color
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stop.stationNaam,
                                    style = fontsizeLabelCard,
                                    color = tekstKleur
                                )
                            }
                            if(stop.status == StopStatusType.COMBINE){
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.label_trein_gecombineerd),
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left,
                                    )
                                }
                            }
                            if(stop.status == StopStatusType.SPLIT){
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.label_trein_split),
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left,
                                    )
                                }
                            }
                            if(stop.opgeheven){
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Icon",
                                        tint = Color.Red,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.label_rijdt_niet),
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left,
                                    )
                                }
                            }
                            if (index == ritDetail.stops.size - 1) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 60.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "\n" + stringResource(id = R.string.text_eindpunt_van_jouw_reis),
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        PullRefreshIndicator(
            modifier = Modifier.align(alignment = Alignment.TopCenter),
            refreshing = refreshing,
            state = state,
        )
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    TimeText()
}
