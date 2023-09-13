package com.yungert.treinplanner.ui.view.detailReisadvies.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.Data.models.Eindbestemming
import com.yungert.treinplanner.presentation.ui.model.DataEindbestemmingStation
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize

@Composable
fun DataEindStationComposable(dataEindStation: DataEindbestemmingStation, eindbestemming: String) {
    Card(
        onClick = {
        },
        modifier = Modifier
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Icon(
                    imageVector = Icons.Default.Euro,
                    contentDescription = "Icon",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(iconSize)
                )
                Text(
                    text = dataEindStation.ritPrijsInEuro,
                    style = fontsizeLabelCard,
                    textAlign = TextAlign.Left
                )
            }
        }
        if (dataEindStation.ovFiets?.isEmpty() == true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.label_geen_ov_fiets),
                    style = fontsizeLabelCard,
                    textAlign = TextAlign.Left
                )
            }
            return@Card
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.label_ov_fiets) + " " + stringResource(id = R.string.label_bij_station) + " " + eindbestemming ,
                style = fontsizeLabelCard,
                textAlign = TextAlign.Left
            )
        }
        dataEindStation.ovFiets?.forEach { ovFiets ->
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Icon",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(iconSize)
                    )
                    Text(
                        text = ovFiets.locatieFietsStalling,
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
                        imageVector = Icons.Default.PedalBike,
                        contentDescription = "Icon",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(iconSize)
                    )
                    Text(
                        text = ovFiets.aantalOvFietsen,
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Left
                    )
                }
            }
        }
    }
}