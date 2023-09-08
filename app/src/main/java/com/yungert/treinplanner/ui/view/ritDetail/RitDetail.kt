package com.yungert.treinplanner.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.ViewModel.RitDetailViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateRitDetail
import com.yungert.treinplanner.presentation.ui.model.TreinRitDetail
import com.yungert.treinplanner.presentation.utils.Foutmelding
import com.yungert.treinplanner.presentation.utils.LoadingScreen
import com.yungert.treinplanner.ui.view.ritDetail.composables.MaterieelInzetComposable
import com.yungert.treinplanner.ui.view.ritDetail.composables.RitInfoCompoasble
import com.yungert.treinplanner.ui.view.ritDetail.composables.RitOpgehevenComposable
import com.yungert.treinplanner.ui.view.ritDetail.composables.ShowNotesComposable
import com.yungert.treinplanner.ui.view.ritDetail.composables.StopsComposable
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
                        RitInfoCompoasble(
                            ritNummer = ritDetail.ritNummer,
                            eindbestemmingTrein = ritDetail.eindbestemmingTrein
                        )
                    }
                }

                if (ritDetail.opgeheven) {
                    item {
                        RitOpgehevenComposable()
                    }
                }

                item {
                    MaterieelInzetComposable(ritDetail)
                }

                item {
                    ShowNotesComposable(ritDetail.note)
                }

                treinStops.stops.forEachIndexed { index, stop ->
                    val tekstKleur = if (stop.opgeheven) Color.Red else Color.White
                    item {
                        StopsComposable(
                            index = index,
                            stop = stop,
                            tekstKleur = tekstKleur,
                            aantalStops = ritDetail.stops.size
                        )
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
