package com.yungert.treinplanner.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardTab
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.Tram
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Timer
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
import com.yungert.treinplanner.presentation.ui.ViewModel.DetailReisAdviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateDetailReisAdvies
import com.yungert.treinplanner.presentation.ui.model.DetailReisAdvies
import com.yungert.treinplanner.presentation.utils.Foutmelding
import com.yungert.treinplanner.presentation.utils.LoadingScreen
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize
import com.yungert.treinplanner.presentation.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.utils.minimaleHoogteTouchControls
import kotlinx.coroutines.launch

private var indexRit = 0
@Composable
fun ShowDetailReisAdvies(
    reisAdviesId: String,
    viewModel: DetailReisAdviesViewModel,
    navController: NavController,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val ritDetailViewModel = viewModel
    when (val result = ritDetailViewModel.reisavies.collectAsState().value) {
        is ViewStateDetailReisAdvies.Success -> {
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
                is ViewStateDetailReisAdvies.Loading -> LoadingScreen(
                    loadingText = stringResource(
                        id = R.string.laadt_text_rit_gegevens
                    )
                )

                is ViewStateDetailReisAdvies.Problem -> {
                    Foutmelding(
                        errorState = response.exception,
                        onClick = {
                            viewModel.getReisadviesDetail(
                                reisAdviesId = reisAdviesId,
                                context = context
                            )
                        })
                }

                is ViewStateDetailReisAdvies.Success -> {
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
    treinRit: DetailReisAdvies,
    navController: NavController,
    viewModel: DetailReisAdviesViewModel,
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
            is ViewStateDetailReisAdvies.Success -> {
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
                item {
                    if (treinRit.hoofdBericht != null) {
                        extraItem++
                        Card(
                            onClick = {},
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Icon",
                                        tint = Color.Yellow,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = treinRit.hoofdBericht,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left,
                                    )
                                }
                                if(treinRit.eindTijdVerstoring != ""){
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
                                            text = stringResource(id = R.string.label_verwachte_eindtijd) + ": " +  treinRit.eindTijdVerstoring,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Left,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (opgeheven){
                    extraItem++
                    item {
                        Card(
                            onClick = {},
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
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
                        }
                    }
                }

                trips.rit.forEachIndexed { index, reis ->
                    extraItem++
                    item {
                        if (index > 0 && reis.overstapTijd != "") {

                            Card(
                                onClick = {
                                },
                                modifier = Modifier
                                    .padding(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        if (reis.alternatiefVervoer) {
                                            Text(
                                                text = reis.overstapTijd + " " + stringResource(id = R.string.text_tijd_overstap_op_alternatief_vervoer),
                                                style = fontsizeLabelCard,
                                                textAlign = TextAlign.Center
                                            )
                                        } else {
                                            Text(
                                                text = reis.overstapTijd + " " + stringResource(id = R.string.text_tijd_overstap_op_andere_trein) + " " + reis.vertrekSpoor,
                                                style = fontsizeLabelCard,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (index > 0 && reis.overstapTijd == ""){
                            Card(
                                onClick = {
                                },
                                modifier = Modifier
                                    .padding(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {

                                        Text(
                                            text = stringResource(id = R.string.label_fout_berekenen_overstap),
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Center
                                        )

                                    }
                                }
                            }
                        }
                    }
                    item {
                        Card(
                            onClick = {
                                navController.navigate(
                                    Screen.RitDetail.withArguments(
                                        reis.vertrekStationUicCode,
                                        reis.aankomstStationUicCode,
                                        reis.ritId,
                                        reis.datum
                                    )
                                )
                                indexRit = index + extraItem
                            },
                            modifier = if (index == treinRit.rit.size - 1) Modifier.padding(bottom = 40.dp) else Modifier
                                .padding(
                                    bottom = 0.dp,
                                )
                                .defaultMinSize(
                                    minWidth = minimaleBreedteTouchControls,
                                    minHeight = minimaleHoogteTouchControls
                                )

                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Text(
                                        text = reis.treinOperator + " " + reis.treinOperatorType + " " + reis.ritNummer + " " + stringResource(
                                            id = R.string.label_eindbestemming_trein
                                        ) + ":",
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Text(
                                        text = reis.eindbestemmingTrein,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                if(reis.opgeheven){
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
                                            maxLines = 2,
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Start,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = reis.naamVertrekStation,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = reis.geplandeVertrektijd,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        text = if(reis.vertrekVertraging.trim() == "0" || reis.vertrekVertraging.trim() == "") "" else "+" + reis.vertrekVertraging,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left,
                                        color = Color.Red,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )

                                    if (reis.vertrekSpoor != "") {
                                        Icon(
                                            imageVector = Icons.Default.Tram,
                                            contentDescription = "Icon",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(iconSize)
                                        )
                                        Text(
                                            text = reis.vertrekSpoor,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Left
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardTab,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = reis.naamAankomstStation,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = reis.geplandeAankomsttijd,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left
                                    )

                                    Text(
                                        text = if(reis.aankomstVertraging.trim() == "0" || reis.aankomstVertraging.trim() == "") "" else "+" + reis.aankomstVertraging,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Center,
                                        color = Color.Red,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )

                                    if (reis.aankomstSpoor != "") {
                                        Icon(
                                            imageVector = Icons.Default.Tram,
                                            contentDescription = "Icon",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(iconSize),
                                        )
                                        Text(
                                            text = reis.aankomstSpoor!!,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Left,
                                        )
                                    }
                                    
                                    Icon(
                                        imageVector = Icons.Outlined.Timer,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize),

                                        )
                                    Text(
                                        text = if(reis.punctualiteit > 0.0)reis.punctualiteit.toString() + "%" else "?",
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Left
                                    )

                                }

                                reis.berichten.forEach { bericht ->
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
                                            text = bericht.text,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Left
                                        )
                                    }
                                }
                                if (reis.kortereTreinDanGepland) {
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
                                            text = stringResource(id = R.string.label_kortere_trein_waarschuwing),
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
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
}



