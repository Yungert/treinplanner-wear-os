package com.yungert.treinplanner.ui.view.reisAdvies.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.presentation.ui.Navigation.Screen
import com.yungert.treinplanner.presentation.ui.model.Adviezen
import com.yungert.treinplanner.presentation.utils.DrukteIndicatorComposable
import com.yungert.treinplanner.presentation.utils.MessageType
import com.yungert.treinplanner.presentation.utils.PrimaryMessageType
import com.yungert.treinplanner.presentation.utils.TripStatus
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize
import com.yungert.treinplanner.presentation.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.utils.minimaleHoogteTouchControls

@Composable
fun ReisAdviesCardComposable(
    navController: NavController,
    advies: Adviezen,
    index: Int,
    onClick: (clickIndex: Int) -> Unit
) {
    Card(
        onClick = {
            navController.navigate(Screen.Reisadvies.withArguments(advies.reinadviesId))
            onClick(index)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .defaultMinSize(
                minWidth = minimaleBreedteTouchControls,
                minHeight = minimaleHoogteTouchControls
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = advies.geplandeVertrekTijd,
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Center
                    )
                    if (advies.vertrekVertraging != "0" && advies.vertrekVertraging != "") {
                        Text(
                            text = "+" + advies.vertrekVertraging,
                            style = fontsizeLabelCard,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 1.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.East,
                            contentDescription = "Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .size(iconSize)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.East,
                            contentDescription = "Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .size(iconSize)
                                .padding(horizontal = 1.dp)
                        )
                    }
                    Text(
                        text = advies.geplandeAankomstTijd,
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Center
                    )
                    if (advies.aankomstVertraging != "0" && advies.aankomstVertraging != "") {
                        Text(
                            text = "+" + advies.aankomstVertraging,
                            style = fontsizeLabelCard,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 1.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Icon",
                        tint = Color.White,
                        modifier = Modifier
                            .size(iconSize)
                    )
                    if (advies.actueleReistijd == "0:00") {
                        Text(
                            text = advies.geplandeReistijd,
                            style = fontsizeLabelCard,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    } else {
                        Text(
                            text = advies.actueleReistijd,
                            style = fontsizeLabelCard,
                            color = if (advies.actueleReistijd != advies.geplandeReistijd) Color.Red else Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = "Icon",
                        tint = Color.White,
                        modifier = Modifier
                            .size(iconSize)
                    )
                    Text(
                        text = (advies.aantalTransfers.toString() + "x"),
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    Text(
                        text = advies.treinSoortenOpRit,
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DrukteIndicatorComposable(
                        aantalIconen = advies.drukte.aantalIconen,
                        icon = advies.drukte.icon,
                        color = advies.drukte.color
                    )
                }
            }
            if (advies.alternatiefVervoer) {
                AlternatiefVervoerComposable()
            }
            if (advies.status == TripStatus.CANCELLED) {
                ReisadviesNietMogelijkComposable()
            }

            if (advies.primaryMessage != null) {
                if (advies.primaryMessage.type?.let { PrimaryMessageType.fromValue(it) } != PrimaryMessageType.LEG_TRANSFER_IMPOSSIBLE) {
                    PrimaryMessageComposaBle(advies.primaryMessage)
                } else {
                    TransferNietmogelijkComposable()
                }
            }

            if (MessageType.fromValue(advies.primaryMessage?.message?.type) == MessageType.DISRUPTION && advies.eindTijdverstoring != "") {
                DisruptionEindTijdComposable(eindTijdverstoring = advies.eindTijdverstoring!!)
            }

            if (advies.aandachtsPunten != null && advies.aandachtsPunten != (advies.primaryMessage?.message?.text
                    ?: advies.primaryMessage?.title)
            ) {
                AandachtsPuntenComposable(
                    status = advies.status,
                    aandachtsPunten = advies.aandachtsPunten
                )
            }
        }
    }
}