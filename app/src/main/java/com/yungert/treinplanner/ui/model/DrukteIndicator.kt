package com.yungert.treinplanner.presentation.ui.model

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Keep
data class DrukteIndicator(
    var icon: ImageVector,
    val color: Color,
    val aantalIconen: Int,
)
