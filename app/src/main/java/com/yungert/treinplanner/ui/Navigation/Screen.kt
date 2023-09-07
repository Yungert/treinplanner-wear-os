package com.yungert.treinplanner.presentation.ui.Navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home")
    object StationVanKiezen : Screen("station_van_kiezen")
    object StationNaarKiezen : Screen("station_naar_kiezen")
    object Reisadvies : Screen("reisadvies_kiezen")
    object ReisadviesDetail : Screen("reisadvies_detail")
    object RitDetail : Screen("rit_detail")
    object GpsPermission : Screen("gps_permission")

    fun withArguments(vararg arguments: String): String {
        return buildString {
            append(route)
            arguments.forEach { argument ->
                append("/$argument")
            }
        }
    }
}