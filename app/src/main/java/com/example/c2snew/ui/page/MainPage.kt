package com.example.c2snew.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import co.yml.charts.common.model.Point
import com.example.c2snew.CameraViewModel
import com.example.c2snew.SettingViewModel
import com.example.c2snew.ui.componment.LineChart
import kotlin.math.pow

@SuppressLint("RememberReturnType")
@Composable
fun MainPage(visible:Boolean, viewModel: CameraViewModel,settingModel:SettingViewModel) {
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
        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(8f / 5f)
            .background(Color.Black)
        )
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
        LineChart(
            modifier = Modifier
                .fillMaxSize(),
            xAxis = xList,
            yAxis = listOf("255", "204", "153", "102", "51", "0"),
            xTitle = xTitle,
            yTitle = "",
            lines = combinedData
        )
    }
}

fun wavelengthCalibration(pixelList: List<String>,a0:Float,a1:Float,a2:Float,a3:Float): List<String> {
    // 计算波长列表
    val wavelengthList = pixelList.map { pixel ->
        val p = pixel.toDouble() // 假设 pixelList 中的元素是可以转换为 Double 的
        val wavelength = a0 + a1 * p + a2 * p.pow(2) + a3 * p.pow(3)
        wavelength.toInt().toString() // 将结果转换为字符串，如果需要可以调整为其他类型
    }

    return wavelengthList
}
