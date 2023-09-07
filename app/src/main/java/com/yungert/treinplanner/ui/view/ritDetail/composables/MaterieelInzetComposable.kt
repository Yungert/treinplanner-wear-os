package com.yungert.treinplanner.ui.view.ritDetail.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.Train
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.model.TreinRitDetail
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize

@Composable
fun MaterieelInzetComposable(ritDetail: TreinRitDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                text = ritDetail.materieelType + "-" + ritDetail.aantalTreinDelen + " ",
                style = fontsizeLabelCard
            )


            Icon(
                imageVector = Icons.Default.AirlineSeatReclineNormal,
                contentDescription = "Icon",
                tint = Color.White,
                modifier = Modifier
                    .size(iconSize)
                    .padding(vertical = 2.dp)
            )
            Text(
                text = if (ritDetail.aantalZitplaatsen.toString() != "") ritDetail.aantalZitplaatsen.toString() else stringResource(
                    id = R.string.label_onbekend
                ),
                style = fontsizeLabelCard
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Train,
                contentDescription = "Icon",
                tint = Color.White,
                modifier = Modifier
                    .size(iconSize)
                    .padding(vertical = 2.dp)
            )
            ritDetail.materieelInzet.forEachIndexed { index, materieel ->
                Text(
                    text = materieel.treinNummer,
                    style = fontsizeLabelCard
                )
                if (index < (ritDetail.materieelInzet.size?.minus(1) ?: 0)) {
                    Text(
                        text = ", ",
                        style = fontsizeLabelCard
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ritDetail.materieelInzet.forEach { materieelInzet ->
                if (materieelInzet.eindBestemmingTreindeel != ritDetail.eindbestemmingTrein) {
                    Text(
                        text = stringResource(id = R.string.label_treinstel) + " " + materieelInzet.treinNummer + " " + stringResource(
                            id = R.string.label_rijdt_tot
                        ) + " " + materieelInzet.eindBestemmingTreindeel,
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}