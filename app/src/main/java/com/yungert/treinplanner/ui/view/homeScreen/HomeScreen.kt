package com.yungert.treinplanner.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.yungert.treinplanner.presentation.ui.ViewModel.HomeScreenViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateHomeScreen
import com.yungert.treinplanner.presentation.ui.model.Route
import com.yungert.treinplanner.presentation.utils.Foutmelding
import com.yungert.treinplanner.presentation.utils.LoadingScreen
import com.yungert.treinplanner.ui.view.homeScreen.composables.LaatstGeplandeReisComposable
import com.yungert.treinplanner.ui.view.homeScreen.composables.vertrekStationKiezerComposable
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
        listState.scrollToItem(1)
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
                vertrekStationKiezerComposable(navController = navController)
            }

            if (route?.vertrekStation != "" && route?.aankomstStation != "") {
                item {
                    LaatstGeplandeReisComposable(navController = navController, route = route!!)
                }
            }
        }
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    TimeText()
}