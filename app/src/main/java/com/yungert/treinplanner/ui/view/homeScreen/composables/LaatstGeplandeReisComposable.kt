package com.yungert.treinplanner.ui.view.homeScreen.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.Navigation.Screen
import com.yungert.treinplanner.presentation.ui.model.Route
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard

@Composable
fun LaatstGeplandeReisComposable(
    navController: NavController,
    route: Route
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    Screen.Reisadvies.withArguments(
                        route.vertrekStation,
                        route.aankomstStation
                    )
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.label_laatst_geplande_reis),
                textAlign = TextAlign.Center,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(id = R.string.label_van_reisadvies) + ": ",
                    style = fontsizeLabelCard,
                )
            }
            Box(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxSize()
            ) {
                Text(
                    text = route.vertrekStation,
                    style = fontsizeLabelCard,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(id = R.string.label_naar_reisadvies) + ": ",
                    style = fontsizeLabelCard,
                    maxLines = 1
                )
            }
            Box(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxSize()
            ) {
                Text(
                    text = route.aankomstStation,
                    style = fontsizeLabelCard,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}