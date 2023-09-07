package com.yungert.treinplanner.presentation.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yungert.treinplanner.presentation.ui.ComposeStaions
import com.yungert.treinplanner.presentation.ui.HomeScreen
import com.yungert.treinplanner.presentation.ui.ShowDetailReisadvies
import com.yungert.treinplanner.presentation.ui.ShowGpsPermisson
import com.yungert.treinplanner.presentation.ui.ShowReisAdvies
import com.yungert.treinplanner.presentation.ui.ShowRitDetail
import com.yungert.treinplanner.presentation.ui.ViewModel.DetailReisadviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.HomeScreenViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ReisAdviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.RitDetailViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.StationPickerViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route) {
            var viewmodel: HomeScreenViewModel = viewModel()
            HomeScreen(navController = navController, viewModel = viewmodel)
        }
        composable(route = Screen.GpsPermission.route) {
            ShowGpsPermisson(navController = navController)
        }

        composable(route = Screen.StationVanKiezen.route + "/{metGps}",
            arguments = listOf(
                navArgument("metGps") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { entry ->
            var viewmodel: StationPickerViewModel = viewModel()
            ComposeStaions(
                null,
                navController = navController,
                viewModel = viewmodel,
                metGps = entry.arguments?.getString("metGps") ?: ""
            )
        }

        composable(route = Screen.StationNaarKiezen.route + "/{vanstation}",
            arguments = listOf(
                navArgument("vanstation") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { entry ->
            var viewmodel: StationPickerViewModel = viewModel()
            ComposeStaions(
                vanStation = entry.arguments?.getString("vanstation") ?: "",
                navController = navController,
                viewModel = viewmodel,
                metGps = null
            )
        }
        composable(route = Screen.Reisadvies.route + "/{vanstation}/{naarstation}",
            arguments = listOf(
                navArgument("vanstation") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("naarstation") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { entry ->
            var viewmodel: ReisAdviesViewModel = viewModel()
            ShowReisAdvies(
                vertrekStation = entry.arguments?.getString("vanstation") ?: "",
                eindStation = entry.arguments?.getString("naarstation") ?: "",
                viewModel = viewmodel,
                navController = navController
            )
        }
        composable(route = Screen.Reisadvies.route + "/{reisadviesId}",
            arguments = listOf(
                navArgument("reisadviesId") {
                    type = NavType.StringType
                    nullable = false
                }

            )
        ) { entry ->
            var viewmodel: DetailReisadviesViewModel = viewModel()
            ShowDetailReisadvies(
                reisAdviesId = entry.arguments?.getString("reisadviesId") ?: "",
                viewModel = viewmodel,
                navController = navController
            )
        }
        composable(route = Screen.RitDetail.route + "/{uicCodeVertrek}/{uicCodeAankomst}/{ritId}/{datum}",
            arguments = listOf(
                navArgument("uicCodeVertrek") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("uicCodeAankomst") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("ritId") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("datum") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { entry ->
            var viewmodel: RitDetailViewModel = viewModel()
            ShowRitDetail(
                depatureUicCode = entry.arguments?.getString("uicCodeVertrek") ?: "",
                arrivalUicCode = entry.arguments?.getString("uicCodeAankomst") ?: "",
                reisId = entry.arguments?.getString("ritId") ?: "",
                dateTime = entry.arguments?.getString("datum") ?: "",
                viewModel = viewmodel,
                navController = navController
            )
        }
    }
}