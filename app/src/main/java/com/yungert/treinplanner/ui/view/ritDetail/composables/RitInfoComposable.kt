package com.yungert.treinplanner.ui.view.ritDetail.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.R

@Composable
fun RitInfoCompoasble(ritNummer: String, eindbestemmingTrein: String) {
    Text(
        text = stringResource(id = R.string.label_rit) + " " + ritNummer + " " + stringResource(
            id = R.string.label_eindbestemming_trein
        ) + " " + eindbestemmingTrein,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}