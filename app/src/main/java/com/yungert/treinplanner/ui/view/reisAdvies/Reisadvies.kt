package com.yungert.treinplanner.presentation.ui


import android.annotation.SuppressLint
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.Navigation.Screen
import com.yungert.treinplanner.presentation.ui.ViewModel.ReisAdviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateReisadvies
import com.yungert.treinplanner.presentation.ui.model.Reisadvies
import com.yungert.treinplanner.presentation.utils.Foutmelding
import com.yungert.treinplanner.presentation.utils.LoadingScreen
import com.yungert.treinplanner.presentation.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.utils.minimaleHoogteTouchControls
import com.yungert.treinplanner.ui.view.reisAdvies.composables.ReisAdviesCardComposable
import com.yungert.treinplanner.ui.view.reisAdvies.composables.RouteComposable
import kotlinx.coroutines.launch

private var indexReisadvies = 0

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ShowReisAdvies(
    vertrekStation: String,
    eindStation: String,
    viewModel: ReisAdviesViewModel,
    navController: NavController,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val reisAdviesViewModel = viewModel
    when (val result = reisAdviesViewModel.reisavies.collectAsState().value) {
        is ViewStateReisadvies.Success -> {
            DisplayReisadvies(
                reisAdvies = result.details,
                navController = navController,
                vanStation = vertrekStation,
                naarStation = eindStation,
                viewModel = viewModel
            )
        }

        else -> {
            indexReisadvies = 0
            val context = LocalContext.current
            DisposableEffect(lifeCycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        reisAdviesViewModel.getReisadviezen(
                            startStation = vertrekStation,
                            eindStation = eindStation,
                            context = context,
                        )
                    }
                }
                lifeCycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifeCycleOwner.lifecycle.removeObserver(observer)
                }
            }

            when (val response = reisAdviesViewModel.reisavies.collectAsState().value) {
                is ViewStateReisadvies.Loading -> LoadingScreen(loadingText = stringResource(id = R.string.laadt_resiadviezen))
                is ViewStateReisadvies.Problem -> {
                    Foutmelding(
                        errorState = response.exception,
                        onClick = {
                            viewModel.getReisadviezen(
                                startStation = vertrekStation,
                                eindStation = eindStation,
                                context = context,
                            )
                        })
                }

                is ViewStateReisadvies.Success -> {
                    DisplayReisadvies(
                        reisAdvies = response.details,
                        navController = navController,
                        vanStation = vertrekStation,
                        naarStation = eindStation,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DisplayReisadvies(
    reisAdvies: Reisadvies,
    navController: NavController,
    vanStation: String,
    naarStation: String,
    viewModel: ReisAdviesViewModel
) {
    val INDEX_OFFSET = 2
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()
    coroutineScope.launch {
        listState.scrollToItem(indexReisadvies + INDEX_OFFSET)
    }
    var reisAdviezen = reisAdvies
    val context = LocalContext.current
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        viewModel.getReisadviezen(
            startStation = vanStation,
            eindStation = naarStation,
            context = context
        )
        refreshing = true
        when (val response = viewModel.reisavies.value) {
            is ViewStateReisadvies.Success -> {
                reisAdviezen = response.details
                refreshing = false
            }

            else -> {}
        }

    }

    val state = rememberPullRefreshState(refreshing, ::refresh)
    val stateDismiss = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = stateDismiss,
        onDismissed = {
            navController.popBackStack()
        },
    ) {
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
                        Card(
                            onClick = {
                                navController.navigate(Screen.HomeScreen.route)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(
                                    minWidth = minimaleBreedteTouchControls,
                                    minHeight = minimaleHoogteTouchControls
                                ),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.label_opniew_plannen),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                    item {
                        ListHeader {
                            Text(
                                text = stringResource(id = R.string.label_reis_advies),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    item {
                        RouteComposable(
                            navController = navController,
                            vanStation = vanStation,
                            naarStation = naarStation,
                            reisAdvies = reisAdvies
                        )
                    }

                    if (reisAdviezen.advies.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(id = R.string.label_geen_reisadviezen_gevonden),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    reisAdviezen.advies.forEachIndexed { index, advies ->
                        item {
                            ReisAdviesCardComposable(
                                navController = navController,
                                advies = advies,
                                index = index,
                                onClick = {
                                    indexReisadvies = it + INDEX_OFFSET
                                }
                            )
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier.padding(bottom = 20.dp)
                        ) {}
                    }
                }
            }
            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = refreshing,
                state = state,
            )
        }
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    TimeText()
}



