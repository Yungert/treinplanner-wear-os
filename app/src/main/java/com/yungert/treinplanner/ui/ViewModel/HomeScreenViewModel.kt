package com.yungert.treinplanner.presentation.ui.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.Data.Repository.SharedPreferencesRepository
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ViewStateHomeScreen {
    object Loading : ViewStateHomeScreen()
    data class Success(val details: Route?) : ViewStateHomeScreen()
    data class Problem(val exception: ErrorState?) : ViewStateHomeScreen()
}

class HomeScreenViewModel : ViewModel() {
    private val sharedPreferencesRepository: SharedPreferencesRepository =
        SharedPreferencesRepository()
    private val _viewState = MutableStateFlow<ViewStateHomeScreen>(ViewStateHomeScreen.Loading)
    val route = _viewState.asStateFlow()
    fun getLaatstGeplandeReis(context: Context) {
        var route: Route? = null
        viewModelScope.launch {
            val vertrek = sharedPreferencesRepository.getLastRoute(context, "vertrekStation")
            val aankomst = sharedPreferencesRepository.getLastRoute(context, "aankomstStation")
            route = Route(
                aankomstStation = aankomst ?: "",
                vertrekStation = vertrek ?: ""
            )
            _viewState.value = ViewStateHomeScreen.Success(route)
        }
    }
}