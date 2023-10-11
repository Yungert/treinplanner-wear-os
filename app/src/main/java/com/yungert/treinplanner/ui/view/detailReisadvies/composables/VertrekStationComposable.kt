package com.yungert.treinplanner.ui.view.detailReisadvies.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.Tram
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
fun VertrekStationComposable(reis: RitDetail) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Icon(
            imageVector = Icons.Default.Start,
            contentDescription = "Icon",
            tint = Color.White,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(iconSize)
        )
        Text(
            text = reis.naamVertrekStation,
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
            imageVector = Icons.Default.Schedule,
            contentDescription = "Icon",
            tint = Color.White,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(iconSize)
        )
        Text(
            text = reis.geplandeVertrektijd,
            style = fontsizeLabelCard,
            textAlign = TextAlign.Left
        )
        Text(
            text = if (reis.vertrekVertraging.trim() == "0" || reis.vertrekVertraging.trim() == "") "" else "+" + reis.vertrekVertraging,
            style = fontsizeLabelCard,
            textAlign = TextAlign.Left,
            color = Color.Red,
            modifier = Modifier.padding(horizontal = 2.dp)
        )

        if(!reis.alternatiefVervoer) {
            Icon(
                imageVector = Icons.Default.Tram,
                contentDescription = "Icon",
                tint = Color.White,
                modifier = Modifier
                    .size(iconSize)
            )
            Text(
                text = reis.actueelVertrekSpoor ?: reis.geplandVertrekSpoor,
                style = fontsizeLabelCard,
                textAlign = TextAlign.Left,
                color = if(reis.actueelVertrekSpoor != reis.geplandVertrekSpoor) Color.Red else Color.White,
            )
        }

    }
}