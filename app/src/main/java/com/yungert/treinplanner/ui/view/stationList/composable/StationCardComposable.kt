package com.yungert.treinplanner.ui.view.stationList.composable

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.presentation.ui.Navigation.Screen
import com.yungert.treinplanner.presentation.ui.ViewModel.StationPickerViewModel
import com.yungert.treinplanner.presentation.ui.model.StationNamen
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.utils.minimaleHoogteTouchControls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun StationCardComposable(
    item: StationNamen,
    navController: NavController,
    context: Context,
    vanStation: String?,
    viewmodel: StationPickerViewModel
) {
    var isFavorite by remember { mutableStateOf(item.favorite) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .defaultMinSize(
                minWidth = minimaleBreedteTouchControls,
                minHeight = minimaleHoogteTouchControls
            ),
        onClick = {
            if (vanStation != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewmodel.setLastPlannedRoute(
                        context = context,
                        key = "vertrekStation",
                        value = vanStation
                    )
                    viewmodel.setLastPlannedRoute(
                        context = context,
                        key = "aankomstStation",
                        value = item.displayValue
                    )
                }
                navController.navigate(
                    Screen.Reisadvies.withArguments(
                        vanStation,
                        item.displayValue
                    )
                )
            } else {
                navController.navigate(Screen.StationNaarKiezen.withArguments(item.displayValue))
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.displayValue,
                style = fontsizeLabelCard, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Icon(
                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .size(18.dp)
                    .clickable {
                        isFavorite = !isFavorite
                        CoroutineScope(Dispatchers.IO).launch {
                            viewmodel.toggleFavouriteStation(
                                context = context,
                                item = item
                            )
                        }
                    }
            )
        }
        if (item.distance > 0.0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = item.afstandTotGebruiker,
                    style = fontsizeLabelCard, fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}