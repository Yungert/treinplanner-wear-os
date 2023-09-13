package com.yungert.treinplanner.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.ui.graphics.vector.ImageVector
import com.yungert.treinplanner.R

enum class ErrorState(val txt: Int, val icon: ImageVector) {
    NO_CONNECTION(R.string.error_geen_internet, Icons.Default.WifiOff),
    SERVER_ERROR(R.string.label_server_error, Icons.Default.Error),
    UNKNOWN(R.string.text_onbekende_fout, Icons.Default.Warning),
}