package com.example.c2snew.ui.page

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import co.yml.charts.common.model.Point
import com.example.c2snew.CameraViewModel
import com.example.c2snew.SettingViewModel
import com.example.c2snew.ui.componment.LineChart
import kotlin.math.pow

@SuppressLint("RememberReturnType")
@Composable
fun MainPage(visible:Boolean, viewModel: CameraViewModel,settingModel:SettingViewModel) {
    val titles = listOf("Pixel", "W1", "W2", "T")
    var state by remember { mutableIntStateOf(0) }

    val pointsData by viewModel.chartPointList.observeAsState(initial = listOf(Point(0f,0f)))
    val totalPintsData by viewModel.totalChartPointList.observeAsState(initial = listOf(Point(0f,0f)))
    val historyData by viewModel.historyList.observeAsState(initial = emptyList())
    val bitmapData by viewModel.bitmapData.observeAsState()

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
        bitmapData?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(8f / 5f)
                    .background(Color.Black),
            )
        } ?: Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(8f / 5f)
            .background(Color.Black)
        )
        val combinedData: List<List<Point>> = mutableListOf<List<Point>>().apply {
            if (state == 3) {
                if (totalPintsData.isNotEmpty()) {
                    add(totalPintsData)
                }
            } else {
                if (pointsData.isNotEmpty()) {
                    add(pointsData)
                }
                if (historyData.isNotEmpty()) {
                    addAll(historyData)
                }
            }
        }
        var xTitle = when(state) {
            0-> "Pixel"
            1-> "Wavelength"
            2-> "Wavelength"
            3-> "Pixel"
            else -> "Pixel"
        }
        val width = try {
            settingModel.getValue("Width")?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            0
        }
        val totalList = mutableListOf<String>().apply {
            add((10 * width + 1).toString() + "k")
            add((8 * width + 1).toString() + "k")
            add((6 * width + 1).toString() + "k")
            add((4 * width + 1).toString() + "k")
            add((2 * width + 1).toString() + "k")
            add("0")
        }
        var yAxis = when(state) {
            3-> totalList
            else -> listOf("10k", "8k", "6k", "4k", "2k", "0")
        }
        var ySize = when(state) {
            3-> 255f * (2 * width + 1)
            else -> 255f
        }
        var yScale = when(state) {
            3-> (2 * width + 1).toFloat()
            else -> 1f
        }
        LineChart(
            xAxis = xList,
//            yAxis = listOf("255", "204", "153", "102", "51", "0"),
            yAxis = yAxis,
            xTitle = xTitle,
            yTitle = "",
            lines = combinedData,
            ySize = ySize,
            yScale = yScale
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
