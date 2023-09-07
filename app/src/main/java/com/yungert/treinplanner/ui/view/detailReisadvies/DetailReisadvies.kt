package com.yungert.treinplanner.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.ViewModel.DetailReisadviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateDetailReisadvies
import com.yungert.treinplanner.presentation.ui.model.DetailReisadvies
import com.yungert.treinplanner.presentation.utils.Foutmelding
import com.yungert.treinplanner.presentation.utils.LoadingScreen
import com.yungert.treinplanner.ui.view.detailReisadvies.composables.DataEindStationComposable
import com.yungert.treinplanner.ui.view.detailReisadvies.composables.OverstapComposable
import com.yungert.treinplanner.ui.view.detailReisadvies.composables.OverstapOnbekendComposable
import com.yungert.treinplanner.ui.view.detailReisadvies.composables.PrimaryMessageComposable
import com.yungert.treinplanner.ui.view.detailReisadvies.composables.ReisadviesVervaltComposable
import com.yungert.treinplanner.ui.view.detailReisadvies.composables.RitComposable
import kotlinx.coroutines.launch

private var indexRit = 0

@Composable
fun ShowDetailReisadvies(
    reisAdviesId: String,
    viewModel: DetailReisadviesViewModel,
    navController: NavController,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val ritDetailViewModel = viewModel
    when (val result = ritDetailViewModel.reisavies.collectAsState().value) {
        is ViewStateDetailReisadvies.Success -> {
            DisplayDetailReisAdvies(
                treinRit = result.details,
                navController = navController,
                viewModel = viewModel,
                reisAdviesId = reisAdviesId
            )
        }

        else -> {
            indexRit = 0
            val context = LocalContext.current
            DisposableEffect(lifeCycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        viewModel.getReisadviesDetail(
                            reisAdviesId = reisAdviesId,
                            context = context
                        )
                    }
                }
                lifeCycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifeCycleOwner.lifecycle.removeObserver(observer)
                }
            }

            when (val response = viewModel.reisavies.collectAsState().value) {
                is ViewStateDetailReisadvies.Loading -> LoadingScreen(
                    loadingText = stringResource(
                        id = R.string.laadt_text_rit_gegevens
                    )
                )

                is ViewStateDetailReisadvies.Problem -> {
                    Foutmelding(
                        errorState = response.exception,
                        onClick = {
                            viewModel.getReisadviesDetail(
                                reisAdviesId = reisAdviesId,
                                context = context
                            )
                        })
                }

                is ViewStateDetailReisadvies.Success -> {
                    DisplayDetailReisAdvies(
                        treinRit = response.details,
                        navController = navController,
                        viewModel = viewModel,
                        reisAdviesId = reisAdviesId
                    )
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DisplayDetailReisAdvies(
    treinRit: DetailReisadvies,
    navController: NavController,
    viewModel: DetailReisadviesViewModel,
    reisAdviesId: String
) {
    var INDEX_OFFSET = 1
    var extraItem = 0
    val focusRequester = remember { FocusRequester() }
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var trips = treinRit
    val context = LocalContext.current
    val opgeheven = treinRit.opgeheven

    coroutineScope.launch {
        listState.scrollToItem(indexRit + INDEX_OFFSET)
    }
    fun refresh() = refreshScope.launch {
        viewModel.getReisadviesDetail(
            reisAdviesId = reisAdviesId,
            context = context
        )
        refreshing = true
        when (val response = viewModel.reisavies.value) {
            is ViewStateDetailReisadvies.Success -> {
                trips = response.details
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
                            text = stringResource(id = R.string.label_jouw_reis_naar) + " " + treinRit.rit[treinRit.rit.size - 1].naamAankomstStation,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (treinRit.hoofdBericht != null) {
                    item {
                        extraItem++
                        PrimaryMessageComposable(
                            hoofdBericht = treinRit.hoofdBericht,
                            eindTijdVerstoring = treinRit.eindTijdVerstoring
                        )
                    }
                }

                if (opgeheven) {
                    extraItem++
                    item {
                        ReisadviesVervaltComposable()
                    }
                }

                trips.rit.forEachIndexed { index, reis ->
                    extraItem++
                    item {
                        if (index > 0 && reis.overstapTijd != "") {
                            OverstapComposable(reis = reis)
                        }

                        if (index > 0 && reis.overstapTijd == "") {
                            OverstapOnbekendComposable()
                        }
                    }
                    item {
                        RitComposable(
                            reis = reis,
                            navController = navController,
                            index = index,
                            extraItem = extraItem,
                            aantalRitten = treinRit.rit.size,
                            onClick = {
                                indexRit = it
                            }
                        )
                    }

                }
                item {
                    treinRit.dataEindStation?.let { DataEindStationComposable(it) }
                }
                item {
                    Column(
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {}
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
}



