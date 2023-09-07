package com.yungert.treinplanner.presentation.ui


import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
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
import androidx.wear.compose.material.Card
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
import com.yungert.treinplanner.presentation.ui.Navigation.Screen
import com.yungert.treinplanner.presentation.ui.ViewModel.ReisAdviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateReisAdvies
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.utils.DrukteIndicatorComposable
import com.yungert.treinplanner.presentation.utils.Foutmelding
import com.yungert.treinplanner.presentation.utils.LoadingScreen
import com.yungert.treinplanner.presentation.utils.MessageType
import com.yungert.treinplanner.presentation.utils.TripStatus
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize
import com.yungert.treinplanner.presentation.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.utils.minimaleHoogteTouchControls
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
        is ViewStateReisAdvies.Success -> {
            DisplayReisAdvies(
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
                is ViewStateReisAdvies.Loading -> LoadingScreen(loadingText = stringResource(id = R.string.laadt_resiadviezen))
                is ViewStateReisAdvies.Problem -> {
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

                is ViewStateReisAdvies.Success -> {
                    DisplayReisAdvies(
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
fun DisplayReisAdvies(
    reisAdvies: ReisAdvies,
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
            is ViewStateReisAdvies.Success -> {
                reisAdviezen = response.details
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    Screen.Reisadvies.withArguments(
                                        naarStation,
                                        vanStation
                                    )
                                )
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(0.25f)
                                    .fillMaxSize(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    text = stringResource(id = R.string.label_van_reisadvies) + ": ",
                                    style = fontsizeLabelCard,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(0.75f)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = reisAdvies.verstrekStation,
                                    style = fontsizeLabelCard,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(0.25f)
                                    .fillMaxSize(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    text = stringResource(id = R.string.label_naar_reisadvies) + ": ",
                                    style = fontsizeLabelCard,
                                    maxLines = 1
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(0.75f)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = reisAdvies.aankomstStation,
                                    style = fontsizeLabelCard,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                reisAdviezen.advies.forEachIndexed { index, advies ->
                    item {
                        Card(
                            onClick = {
                                navController.navigate(Screen.Reisadvies.withArguments(advies.reinadviesId))
                                indexReisadvies = index + INDEX_OFFSET
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                                .defaultMinSize(
                                    minWidth = minimaleBreedteTouchControls,
                                    minHeight = minimaleHoogteTouchControls
                                ),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = advies.geplandeVertrekTijd,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Center
                                        )
                                        if (advies.vertrekVertraging != "0" && advies.vertrekVertraging != "") {
                                            Text(
                                                text = "+" + advies.vertrekVertraging,
                                                style = fontsizeLabelCard,
                                                color = Color.Red,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 1.dp)
                                            )

                                            Icon(
                                                imageVector = Icons.Default.East,
                                                contentDescription = "Icon",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(iconSize)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.East,
                                                contentDescription = "Icon",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(iconSize)
                                                    .padding(horizontal = 1.dp)
                                            )
                                        }
                                        Text(
                                            text = advies.geplandeAankomstTijd,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Center
                                        )
                                        if (advies.aankomstVertraging != "0" && advies.aankomstVertraging != "") {
                                            Text(
                                                text = "+" + advies.aankomstVertraging,
                                                style = fontsizeLabelCard,
                                                color = Color.Red,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 1.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = "Icon",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(iconSize)
                                        )
                                        if (advies.actueleReistijd == "0:00") {
                                            Text(
                                                text = advies.geplandeReistijd,
                                                style = fontsizeLabelCard,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 2.dp)
                                            )
                                        } else {
                                            Text(
                                                text = advies.actueleReistijd,
                                                style = fontsizeLabelCard,
                                                color = if (advies.actueleReistijd != advies.geplandeReistijd) Color.Red else Color.White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CompareArrows,
                                            contentDescription = "Icon",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(iconSize)
                                        )
                                        Text(
                                            text = (advies.aantalTransfers.toString() + "x"),
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 2.dp)
                                        )
                                        Text(
                                            text = advies.treinSoortenOpRit,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 2.dp)
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        DrukteIndicatorComposable(
                                            aantalIconen = advies.drukte.aantalIconen,
                                            icon = advies.drukte.icon,
                                            color = advies.drukte.color
                                        )
                                    }
                                }
                                if (advies.alternatiefVervoer) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Icon",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .size(iconSize)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.alternatief_vervoer_bericht),
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Left,
                                            maxLines = 2,
                                        )

                                    }
                                }
                                if (advies.status == TripStatus.CANCELLED) {
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
                                            text = stringResource(id = R.string.label_vervallen_reisadvies),
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Left,
                                            maxLines = 2,
                                        )
                                    }
                                }

                                if (advies.primaryMessage != null) {
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
                                            text = advies.primaryMessage.message?.text ?: advies.primaryMessage.title,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Left,
                                        )


                                    }
                                }

                                if(MessageType.fromValue(advies.primaryMessage?.message?.type) == MessageType.DISRUPTION && advies.eindTijdverstoring != ""){
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Icon",
                                            tint = Color.Yellow,
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .size(iconSize)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.label_verwachte_eindtijd) + ": " + advies.eindTijdverstoring,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Left,
                                        )
                                    }
                                }

                                if (advies.aandachtsPunten != null && advies.aandachtsPunten != (advies.primaryMessage?.message?.text ?: advies.primaryMessage?.title)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Icon",
                                            tint = if (advies.status == TripStatus.CANCELLED) Color.Red else Color.Yellow,
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .size(iconSize)
                                        )
                                        Text(
                                            text = advies.aandachtsPunten,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Left,
                                        )
                                    }
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



