package com.yungert.treinplanner.ui.view.homeScreen.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import com.yungert.treinplanner.presentation.ui.Navigation.Screen
import com.yungert.treinplanner.presentation.utils.iconSize
import com.yungert.treinplanner.presentation.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.utils.minimaleHoogteTouchControls

@Composable
fun vertrekStationKiezerComposable(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .defaultMinSize(
                minWidth = minimaleBreedteTouchControls,
                minHeight = minimaleHoogteTouchControls
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    onClick = {
                        navController.navigate(
                            Screen.StationVanKiezen.withArguments(
                                "false"
                            )
                        )
                    },

                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .size(iconSize),
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    onClick = {
                        navController.navigate(Screen.GpsPermission.route)
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .size(iconSize)
                        )
                    }
                }
            }
        }
    }
}