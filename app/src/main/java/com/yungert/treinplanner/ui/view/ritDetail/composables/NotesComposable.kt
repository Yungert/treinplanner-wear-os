package com.yungert.treinplanner.ui.view.ritDetail.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.presentation.Data.models.JourneyNote
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize

@Composable
fun ShowNotesComposable(note: List<JourneyNote>?) {
    note?.forEach { note ->
        if (note.text != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Icon",
                    tint = Color.Yellow,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(iconSize)
                )
                Text(
                    text = note.text,
                    style = fontsizeLabelCard,
                    textAlign = TextAlign.Left,
                )
            }
        }
    }
}