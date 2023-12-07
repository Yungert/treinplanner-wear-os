package com.yungert.treinplanner.ui.view.detailReisadvies.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import com.yungert.treinplanner.presentation.ui.model.RitDetail
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize

@Composable
fun OverstapComposable(reis: RitDetail) {
    if (!reis.overstapMogelijk) {
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
                        text = stringResource(id = R.string.label_overstap_niet_mogelijk),
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Left,
                    )
                }
            }
        }
        return
    }

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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                if (reis.alternatiefVervoer) {
                    Text(
                        text = reis.overstapTijd + " " + stringResource(id = R.string.text_tijd_overstap_op_alternatief_vervoer),
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = reis.overstapTijd + " " + stringResource(id = R.string.text_tijd_overstap_op_andere_trein) + " " + (reis.actueelVertrekSpoor
                            ?: reis.geplandVertrekSpoor),
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Left
                    )
                }
            }

            if (reis.crossPlatform) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = stringResource(id = R.string.label_cross_platform_overstap),
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Left
                    )
                }
            }

        }
    }
}