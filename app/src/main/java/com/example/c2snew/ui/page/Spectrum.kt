package com.example.c2snew.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import co.yml.charts.common.model.Point
import com.example.c2snew.CameraViewModel
import com.example.c2snew.SettingViewModel
import com.example.c2snew.ui.componment.HorizontalLineChart

@SuppressLint("RememberReturnType")
@Composable
fun Spectrum(visible:Boolean, viewModel: CameraViewModel,settingModel: SettingViewModel) {
    val titles = listOf("Pixel", "W1", "W2")
    var state by remember { mutableIntStateOf(0) }
    val pointsData by viewModel.chartPointList.observeAsState(initial = listOf(Point(0f,0f)))
    val historyData by viewModel.historyList.observeAsState(initial = emptyList())

    var xList = listOf("0", "256", "512", "768", "1024", "1280")
    val a0 = settingModel.getValue("a0")
    val a1 = settingModel.getValue("a1")
    val a2 = settingModel.getValue("a2")
    val a3 = settingModel.getValue("a3")
    if (a0 != "" && a1 != "" && a2 != "" && a3 != "" && a0 != null && a1 != null && a2 != null && a3 != null && state > 0) {
        xList = wavelengthCalibration(xList, a0.toFloat(),a1.toFloat(),a2.toFloat(),a3.toFloat(),)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                alpha = if (visible) 1f else 0f,
                translationX = if (visible) 0f else 1000f
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryTabRow(selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    text = { Text(text = title, maxLines = 1) }
                )
            }
        }
        val combinedData: List<List<Point>> = mutableListOf<List<Point>>().apply {
            if (pointsData.isNotEmpty()) {
                add(pointsData)
            }
            if (historyData.isNotEmpty()) {
                addAll(historyData)
            }
        }
        var xTitle = when(state) {
            0-> "Pixel"
            1-> "Wavelength"
            2-> "Wavelength"
            else -> "Pixel"
        }
        HorizontalLineChart(
            xAxis = xList,
            yAxis = listOf("10k", "8k", "6k", "4k", "2k", "0"),
            xTitle = xTitle,
            yTitle = "",
            lines = combinedData
        )
    }
}