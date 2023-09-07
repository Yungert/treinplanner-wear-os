package com.yungert.treinplanner.presentation.ui.model

import androidx.annotation.Keep

@Keep
data class Route(
    val vertrekStation: String,
    val aankomstStation: String,
)
