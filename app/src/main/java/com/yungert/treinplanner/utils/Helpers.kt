package com.yungert.treinplanner.presentation.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yungert.treinplanner.R
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private var lastFormattedTime: String? = null
fun formatTime(time: String?): String {
    if (time == null || time == "") {
        return ""
    }
    val offsetIndex = time.indexOf('+')

    val modifiedTimestamp = StringBuilder(time).insert(offsetIndex + 3, ':').toString()

    val offsetDateTime =
        OffsetDateTime.parse(modifiedTimestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME)


    val localTime = offsetDateTime.toLocalTime()

    val formattedTime = localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    lastFormattedTime = formattedTime
    return formattedTime
}

fun formatTravelTime(duratinInMinutes: Int): String {
    val uur = duratinInMinutes / 60
    var stringReistijd = ""
    if (uur > 0) {
        stringReistijd = uur.toString() + ":"
    } else {
        stringReistijd = "0:"
    }
    if (duratinInMinutes % 60 < 10) {
        stringReistijd = stringReistijd + "0" + (duratinInMinutes % 60).toString()
    } else {
        stringReistijd += (duratinInMinutes % 60).toString()
    }
    return stringReistijd
}

fun calculateDelay(delayInSeconds: Long?): String {

    if (delayInSeconds == null) {
        return "(-)"
    }

    if (delayInSeconds == 0.toLong()) {
        return ""
    }

    var minuten = delayInSeconds / 60
    var seconden = delayInSeconds % 60

    if (seconden > 30) {
        minuten++
    }

    if (minuten > 0) {
        return "+" + minuten.toString()
    }

    if (seconden > 30 && seconden < 60) {
        return "+1"
    }
    return ""
}

fun calculateTimeDiff(startTime: String?, endTime: String?): String {
    if (startTime == null || endTime == null) {
        return ""
    }
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxx")

    var start = OffsetDateTime.parse(startTime, format)
    var end = OffsetDateTime.parse(endTime, format)

    val duration = Duration.between(start, end)

    return duration.toMinutes().toString()
}

fun convertMeterNaarKilometer(afstandInMeters: Double): String {
    if (afstandInMeters > 1000) {
        val kilometers = afstandInMeters / 1000
        return "${"%.1f".format(kilometers)} km"
    }
    return "${"%.1f".format(afstandInMeters)} m"
}

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val currentNetwork = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(currentNetwork)
    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
}
@Composable
fun getUitvalRitText():String{
    return stringResource(id = R.string.label_vervallen_reisadvies)
}