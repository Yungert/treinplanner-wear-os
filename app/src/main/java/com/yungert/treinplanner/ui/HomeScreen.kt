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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.yungert.treinplanner.presentation.ui.ViewModel.HomeScreenViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateHomeScreen
import com.yungert.treinplanner.presentation.ui.model.Route
import com.yungert.treinplanner.presentation.utils.Foutmelding
import com.yungert.treinplanner.presentation.utils.LoadingScreen
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize
import com.yungert.treinplanner.presentation.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.utils.minimaleHoogteTouchControls
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    navController: NavController,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val context = LocalContext.current
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getLaatstGeplandeReis(context = context)
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when (val response = viewModel.route.collectAsState().value) {
        is ViewStateHomeScreen.Loading -> LoadingScreen()
        is ViewStateHomeScreen.Problem -> {
            Foutmelding(onClick = {
                viewModel.getLaatstGeplandeReis(context = context)
            })
        }

        is ViewStateHomeScreen.Success -> {
            DisplayHomeScreen(
                navController = navController,
                route = response.details
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DisplayHomeScreen(navController: NavController, route: Route?) {
    val focusRequester = remember { FocusRequester() }
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch {
        listState.scrollToItem(2)
    }
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
                        text = stringResource(id = R.string.label_header_homescreen),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = minimaleBreedteTouchControls,
                            minHeight = minimaleHoogteTouchControls
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1.0f)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                onClick = {
                                    navController.navigate(
                                        Screen.StationVanKiezen.withArguments(
                                            "false"
                                        )
                                    )
                                },

                                ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sort,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 1.dp)
                                            .size(iconSize),
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1.0f)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                onClick = {
                                    navController.navigate(Screen.GpsPermission.route)
                                },
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 1.dp)
                                            .size(iconSize)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (route?.vertrekStation != "" && route?.aankomstStation != "") {
                item {
                    Text(
                        text = stringResource(id = R.string.label_laatst_geplande_reis),
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    Screen.Reisadvies.withArguments(
                                        route!!.vertrekStation,
                                        route.aankomstStation
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
                                    text = route!!.vertrekStation,
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
                                    text = route!!.aankomstStation,
                                    style = fontsizeLabelCard,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                }
            }
        }
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    TimeText()
}