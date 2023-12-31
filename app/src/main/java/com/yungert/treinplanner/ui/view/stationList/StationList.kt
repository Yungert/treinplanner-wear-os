package com.yungert.treinplanner.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.ViewModel.StationPickerViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateStationPicker
import com.yungert.treinplanner.presentation.ui.model.StationNamen
import com.yungert.treinplanner.presentation.utils.Foutmelding
import com.yungert.treinplanner.presentation.utils.LoadingScreen
import com.yungert.treinplanner.ui.view.stationList.composable.StationCardComposable
import kotlinx.coroutines.launch


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favourites")
private var stationZoekenOPdracht = ""

@Composable
fun ComposeStaions(
    vanStation: String?,
    navController: NavController,
    viewModel: StationPickerViewModel,
    metGps: String?,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    stationZoekenOPdracht = ""
    val stationViewModel = viewModel
    when (val result = stationViewModel.stations.collectAsState().value) {
        is ViewStateStationPicker.Success -> {
            ShowStations(
                stations = result.details,
                vanStation = vanStation,
                navController = navController,
                viewModel = viewModel
            )
        }

        else -> {
            val context = LocalContext.current
            DisposableEffect(lifeCycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        if (metGps == "true") {
                            viewModel.getStationsMetGps(vanStation = vanStation, context = context)
                        } else {
                            viewModel.getStationsZonderGps(
                                vanStation = vanStation,
                                context = context
                            )
                        }
                    }
                }
                lifeCycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifeCycleOwner.lifecycle.removeObserver(observer)
                }
            }
            val loadingText =
                if (metGps == "true" && vanStation == null) stringResource(id = R.string.label_dichtbijzijnde_stations_ophalen) else stringResource(
                    id = R.string.laadt_text_station_ophalen
                )
            when (val response = viewModel.stations.collectAsState().value) {
                is ViewStateStationPicker.Loading -> LoadingScreen(loadingText)
                is ViewStateStationPicker.Problem -> {
                    Foutmelding(
                        errorState = response.exception,
                        onClick = {
                            viewModel.getStationsMetGps(vanStation = vanStation, context = context)
                        })
                }

                is ViewStateStationPicker.Success -> {
                    ShowStations(
                        stations = response.details,
                        vanStation = vanStation,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShowStations(
    stations: List<StationNamen>,
    vanStation: String?,
    navController: NavController,
    viewModel: StationPickerViewModel
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf(TextFieldValue()) }
    if (searchText.text != "") {
        stationZoekenOPdracht = searchText.text
    }
    val filteredItems = if (stationZoekenOPdracht.isEmpty()) {
        stations
    } else {
        stations.filter { it.displayValue.lowercase().contains(stationZoekenOPdracht.lowercase()) }
    }
    coroutineScope.launch {
        listState.scrollToItem(1)
    }
    val stateDismiss = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = stateDismiss,
        onDismissed = {
            navController.popBackStack()
        },
    ) {
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
                            text = if (vanStation != null) stringResource(id = R.string.kies_aankomst_station) else stringResource(
                                id = R.string.kies_vertrek_station
                            ),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                item {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = {
                            Text(
                                text = stringResource(id = R.string.text_zoek_station),
                                color = Color.White
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                            cursorColor = Color.White,
                            unfocusedIndicatorColor = Color.White,
                            focusedIndicatorColor = Color.White
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Icon",
                                tint = Color.White,
                            )
                        },
                    )
                }

                filteredItems.forEach { station ->
                    item {
                        StationCardComposable(
                            item = station,
                            navController = navController,
                            context = context,
                            vanStation = vanStation,
                            viewmodel = viewModel
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    TimeText()
}





