package com.yungert.treinplanner.presentation.utils

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RailwayAlert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.Data.models.Message
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.DrukteIndicator
import kotlinx.coroutines.launch


@Composable
fun LoadingScreen(loadingText: String? = null) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text((loadingText ?: stringResource(id = R.string.label_loading)) + "...")
    }
}


@Composable
fun ShowMessage(msg: List<Message?>) {
    if (msg == null) {
        return
    }

    msg.forEach { bericht ->
        if (bericht == null) {
            return
        }
        val color = when (WarningType.fromValue(bericht.type)) {
            WarningType.WARNING -> Color.Yellow
            WarningType.ERROR -> Color.Red
            WarningType.MAINTENANCE -> Color.Yellow
            else -> Color.White
        }
        val icon = when (WarningType.fromValue(bericht.type)) {
            WarningType.WARNING -> Icons.Default.Warning
            WarningType.ERROR -> Icons.Default.Info
            WarningType.DISRUPTION -> Icons.Default.RailwayAlert
            WarningType.MAINTENANCE -> Icons.Default.Construction
            else -> Icons.Default.Warning
        }
        Card(
            onClick = {},
            modifier = Modifier.padding(2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Icon",
                        tint = color,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(iconSize)
                    )
                    Text(
                        text = bericht.text,
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun DrukteIndicatorComposable(aantalIconen: Int, icon: ImageVector, color: Color) {
    repeat(aantalIconen) {
        Icon(
            imageVector = icon,
            contentDescription = "Icon",
            tint = color,
            modifier = Modifier
                .size(iconSize)
        )
    }
}

@Composable
fun Foutmelding(errorState: ErrorState? = ErrorState.UNKNOWN, onClick: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
                        text = stringResource(id = R.string.header_error_screen),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = errorState?.icon ?: Icons.Default.Warning,
                            contentDescription = "Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(40.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(
                                id = errorState?.txt ?: R.string.text_onbekende_fout
                            ),
                            style = fontsizeLabelCard,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Card(
                            onClick = onClick,
                            modifier = Modifier
                                .defaultMinSize(
                                    minWidth = minimaleBreedteTouchControls,
                                    minHeight = minimaleHoogteTouchControls
                                )
                                .padding(bottom = 30.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.label_opnieuw_proberen),
                                style = fontsizeLabelCard,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

    }
}

fun DrukteIndicatorFormatter(forecast: String?, compactLayout: Boolean? = false): DrukteIndicator {
    var icon = Icons.Default.GroupOff
    var color = Color.Gray
    var aantal = 1
    when (forecast) {
        CrowdForecast.LOW.value -> {
            icon = Icons.Default.Person
            color = Color.Green
        }

        CrowdForecast.MEDIUM.value -> {
            icon = Icons.Default.Person
            color = Color.Yellow
            aantal = 2
        }

        CrowdForecast.HIGH.value -> {
            icon = Icons.Default.Person
            color = Color.Red
            aantal = 3
        }
    }
    return DrukteIndicator(
        icon = icon,
        color = color,
        aantalIconen = if (!compactLayout!!) aantal else 1,
    )
}

