package com.yungert.treinplanner.ui.view.ritDetail.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Tram
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.presentation.ui.model.StopOpRoute
import com.yungert.treinplanner.presentation.utils.DrukteIndicatorComposable
import com.yungert.treinplanner.presentation.utils.StopStatusType
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard

@Composable
fun StopsComposable(index: Int, stop: StopOpRoute, tekstKleur: Color, aantalStops: Int) {
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
        if (stop.status == StopStatusType.COMBINE) {
            TreinCombinerenComposable()
        }
        if (stop.status == StopStatusType.SPLIT) {
            TreinSplitsenComposable()
        }
        if (stop.opgeheven) {
            RitOpgehevenComposable()
        }
        if (index == aantalStops - 1) {
            LaatsteStopComposable()
        }
    }
}