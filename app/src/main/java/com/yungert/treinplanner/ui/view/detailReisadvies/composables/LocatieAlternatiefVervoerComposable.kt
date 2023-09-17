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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.R

import com.yungert.treinplanner.presentation.ui.model.AlternatiefVervoer
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize

@Composable
fun ShowInformationAlternatiefVervoer(data: AlternatiefVervoer?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            data?.advies?.let {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Icon",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(iconSize)
                )
                Text(
                    text = it,
                    style = fontsizeLabelCard,
                    textAlign = TextAlign.Start,
                    maxLines = 5,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {

            data?.vertrekLocatieStation?.let {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Icon",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(iconSize)
                )

                Text(
                    text = stringResource(id = R.string.label_alternatief_vervoer_locatie) + ": " + it,
                    style = fontsizeLabelCard,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                )
            }
        }
    }
}