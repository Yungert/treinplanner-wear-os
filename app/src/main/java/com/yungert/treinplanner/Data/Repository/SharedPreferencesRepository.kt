package com.yungert.treinplanner.presentation.Data.Repository

import android.content.Context
import androidx.annotation.Keep
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yungert.treinplanner.presentation.ui.dataStore
import kotlinx.coroutines.flow.first
@Keep
class SharedPreferencesRepository {
    suspend fun getFavouriteStation(context: Context, key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preference = context.dataStore.data.first()
        return preference[dataStoreKey]
    }

    suspend fun editFavouriteStation(context: Context, key: String, value: String) {
        val exist = (getFavouriteStation(context, key) != null)
        val dataStoreKey = stringPreferencesKey(key)
        if (!exist) {
            context.dataStore.edit { settings ->
                settings[dataStoreKey] = value
            }
        } else {
            context.dataStore.edit { settings ->
                settings.remove(dataStoreKey)
            }
        }
    }

    suspend fun getLastRoute(context: Context, key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preference = context.dataStore.data.first()
        return preference[dataStoreKey]
    }

    suspend fun editLastRoute(context: Context, key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }
}