package com.yungert.treinplanner.ui.tile

import androidx.core.content.ContextCompat
import androidx.wear.tiles.ColorBuilders.argb
import androidx.wear.tiles.DimensionBuilders.degrees
import androidx.wear.tiles.DimensionBuilders.dp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.ARC_ANCHOR_START
import androidx.wear.tiles.LayoutElementBuilders.Arc
import androidx.wear.tiles.LayoutElementBuilders.ArcLine
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
import com.yungert.treinplanner.presentation.utils.formatTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future


private const val RESOURCES_VERSION = "1"
private val PROGRESS_BAR_THICKNESS = dp(6f)
private const val ARC_TOTAL_DEGREES = 360f

class ReisTile : TileService() {
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val sharedPreferencesRepository: SharedPreferencesRepository =
        SharedPreferencesRepository()

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

    override fun onResourcesRequest(requestParams: RequestBuilders.ResourcesRequest) =
        serviceScope.future {
            ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build()
        }


    private suspend fun tileLayout(): LayoutElementBuilders.LayoutElement {
        var data = "default waarde"
        var adies = ""
        val reis = getReisadvies()
        reis.forEach { rit ->
            val vertrektijd = rit.origin.actualDateTime ?: rit.origin.plannedDateTime
            adies += rit.product.operatorName + " "
            adies += rit.product.categoryCode + " naar:\n"
            adies += rit.destination.name + "\n"
            adies += formatTime(vertrektijd) + " SP " + (rit.origin.actualTrack
                ?: rit.origin.plannedTrack) + "\n\n"
        }
        data = adies

        val text = data
        return LayoutElementBuilders.Text.Builder()
            .setText(text).setMaxLines(30)
            .build()
    }

    private suspend fun getReisadvies(): List<Leg> {
        val id = sharedPreferencesRepository.getReisadviesId(
            context = applicationContext,
            "reisadviesId"
        )
        if (id == "" && id != null) {
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

    private fun progressArc(percentage: Float) = Arc.Builder()
        .addContent(
            ArcLine.Builder()
                .setLength(degrees(percentage * ARC_TOTAL_DEGREES))
                .setColor(
                    argb(
                        ContextCompat.getColor(
                            this,
                            androidx.appcompat.R.color.primary_material_light
                        )
                    )
                )
                .setThickness(PROGRESS_BAR_THICKNESS)
                .build()
        )
        .setAnchorAngle(degrees(0.0f))
        .setAnchorType(ARC_ANCHOR_START)
        .build()
}