package com.yungert.treinplanner.ui.view.detailReisadvies.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardTab
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tram
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.presentation.ui.model.RitDetail
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize

@Composable
fun AankomstStationComposable(reis: RitDetail) {
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
            text = if (reis.aankomstVertraging.trim() == "0" || reis.aankomstVertraging.trim() == "") "" else "+" + reis.aankomstVertraging,
            style = fontsizeLabelCard,
            textAlign = TextAlign.Center,
            color = Color.Red,
            modifier = Modifier.padding(horizontal = 2.dp)
        )

        if (!reis.alternatiefVervoer) {
            Icon(
                imageVector = Icons.Default.Tram,
                contentDescription = "Icon",
                tint = Color.White,
                modifier = Modifier
                    .size(iconSize),
            )
            Text(
                text = reis.actueelAankomstspoor ?: reis.geplandAankomstSpoor,
                style = fontsizeLabelCard,
                textAlign = TextAlign.Left,
                color = if (reis.actueelAankomstspoor != reis.geplandAankomstSpoor) Color.Red else Color.White,
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
            text = if (reis.punctualiteit > 0.0) reis.punctualiteit.toString() + "%" else "?",
            style = fontsizeLabelCard,
            textAlign = TextAlign.Left
        )

    }
}