package com.yungert.treinplanner.ui.tile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.wear.tiles.DimensionBuilders
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.tiles.TimelineBuilders
import com.yungert.treinplanner.presentation.Data.Repository.NsApiRepository
import com.yungert.treinplanner.presentation.Data.Repository.SharedPreferencesRepository
import com.yungert.treinplanner.presentation.Data.api.NSApiClient
import com.yungert.treinplanner.presentation.Data.api.Resource
import com.yungert.treinplanner.presentation.Data.models.Leg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future

private const val RESOURCES_VERSION = "1"

class ReisTile: TileService() {
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val sharedPreferencesRepository: SharedPreferencesRepository = SharedPreferencesRepository()

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest) = serviceScope.future {

        val singleTileTimeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(tileLayout())
                            .build()
                    )
                    .build()
            )
            .build()

        return@future TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTimeline(singleTileTimeline)
            .build()
    }

    override fun onResourcesRequest(requestParams: RequestBuilders.ResourcesRequest) = serviceScope.future {
        ResourceBuilders.Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            .build()
    }


    private suspend fun tileLayout(): LayoutElementBuilders.LayoutElement {
        var data = "default waarde"

        var adies = ""
        val reis = getReisadvies()
        reis.forEach { rit ->
            adies+= rit.product.number
        }
        data = adies

        val text = data
        return LayoutElementBuilders.Box.Builder()
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(text)
                    .build()
            )
            .build()
    }

    private suspend fun getReisadvies(): List<Leg>{
        val id = sharedPreferencesRepository.getReisadviesId(context = applicationContext, "reisadviesId")
        if (id == "" && id != null){
            return emptyList()
        }
        var data = emptyList<Leg>()
        nsApiRepository.fetchSingleTripById(reisadviesId = id!!).collect { result ->
            when (result) {
                is Resource.Success -> {
                    data = result.data?.legs!!
                }
                else -> {

                }
            }
        }
        return data
    }
}