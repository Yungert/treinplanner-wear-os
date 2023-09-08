package com.yungert.treinplanner.presentation.Data.api

import androidx.annotation.Keep
import com.yungert.treinplanner.presentation.ui.ErrorState

@Keep
sealed class Resource<T>(
    val data: T? = null,
    val state: ErrorState? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(state: ErrorState?, data: T? = null) : Resource<T>(data, state)
    class Loading<T> : Resource<T>()
}
