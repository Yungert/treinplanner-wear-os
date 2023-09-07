package com.yungert.treinplanner.ui.view.detailReisadvies.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Card
import com.yungert.treinplanner.presentation.ui.Navigation.Screen
import com.yungert.treinplanner.presentation.ui.model.RitDetail
import com.yungert.treinplanner.presentation.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.utils.minimaleHoogteTouchControls

@Composable
fun RitComposable(
    reis: RitDetail,
    navController: NavController,
    index: Int,
    extraItem: Int,
    aantalRitten: Int,
    onClick: (index: Int) -> Unit
) {
    Card(
        onClick = {
            navController.navigate(
                Screen.RitDetail.withArguments(
                    reis.vertrekStationUicCode,
                    reis.aankomstStationUicCode,
                    reis.ritId,
                    reis.datum
                )
            )
            onClick(index + extraItem)
        },
        modifier = Modifier
            .padding(
                bottom = 0.dp,
            )
            .defaultMinSize(
                minWidth = minimaleBreedteTouchControls,
                minHeight = minimaleHoogteTouchControls
            )

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp),
        ) {
            TreinRitInfoComposable(reis = reis)
            if (reis.opgeheven) {
                RitOpgehevenCompoasble()
            }
            VertrekStationComposable(reis = reis)

            AankomstStationComposable(reis = reis)

            reis.berichten.forEach { bericht ->
                ReisadviesBerichtenComposable(bericht = bericht)
            }
            if (reis.kortereTreinDanGepland) {
                KortereTreinWarningCompoasble()
            }
        }
    }
}