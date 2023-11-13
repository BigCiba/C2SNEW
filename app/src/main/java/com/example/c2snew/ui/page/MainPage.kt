package com.example.c2snew.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.c2snew.CameraViewModel

@SuppressLint("RememberReturnType")
@Composable
fun MainPage(visible:Boolean, viewModel: CameraViewModel) {
    val titles = listOf("Pixel", "W1", "W2")
    var state by remember { mutableIntStateOf(0) }

    val pointsData by viewModel.chartPointList.observeAsState(initial = listOf(Point(0f,0f)))
    val historyData by viewModel.historyList.observeAsState(initial = emptyList())

    val steps = 1
//    val pointsData: List<Point> = listOf(Point(0f, 40f), Point(1f, 90f), Point(2f, 0f), Point(3f, 60f), Point(4f, 10f))
    val xAxisData = AxisData.Builder()
        .axisStepSize(0.3.dp)
        .backgroundColor(Color.Transparent)
        .steps(4)
        .labelData { i -> i.toString() }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.secondary)
        .axisLabelColor(MaterialTheme.colorScheme.secondary)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Transparent)
//        .labelAndAxisLinePadding(51.dp)
        .axisLineColor(MaterialTheme.colorScheme.secondary)
        .axisLabelColor(MaterialTheme.colorScheme.secondary)
        .labelData { i ->
            ((255 / steps) * i).toString()
        }.build()


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
            .aspectRatio(4f / 3f)
            .background(Color.Black)
        )
        Canvas(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)) {
            drawRect(
                color = Color.Gray,
                topLeft = Offset(100f, 50f),
                size = Size(size.width - 150f,size.height-200f),
                style = Stroke(width = 2.dp.toPx())
            )
            // 使用 for 循环创建多个 drawLine
            for (i in 0 until 6) {
                val startY = 50f + i * (size.height-200f) / 5  // 调整起始点的 Y 轴位置
                drawLine(
                    color = Color.Gray,
                    start = Offset(80f, startY),
                    end = Offset(100f, startY),
                    strokeWidth = 2.dp.toPx(),
                )
            }
            // 使用 for 循环创建多个 drawLine
            for (i in 0 until 6) {
                val startX = 100f + i * (size.width - 150f) / 5  // 调整起始点的 Y 轴位置
                drawLine(
                    color = Color.Gray,
                    start = Offset(startX, size.height-150f),
                    end = Offset(startX, size.height-130f),
                    strokeWidth = 2.dp.toPx(),
                )
            }
        }
//        LineChart(
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(4f / 3f),
//            lineChartData = LineChartData(
//                linePlotData = LinePlotData(
//                    lines = mutableListOf<Line>().apply {
//                        // 添加主要数据集
//                        add(
//                            Line(
//                                dataPoints = pointsData,
//                                LineStyle(
//                                    color = MaterialTheme.colorScheme.primary,
//                                    width = 4f
//                                ),
////                                shadowUnderLine = ShadowUnderLine()
//                            )
//                        )
//
//                        // 添加历史数据集
//                        historyData.forEachIndexed { index, data ->
//                            add(
//                                Line(
//                                    dataPoints = data,
//                                    LineStyle(
//                                        color = getLineColor(index),
//                                        width = 4f
//                                    ),
////                                    shadowUnderLine = ShadowUnderLine()
//                                )
//                            )
//                        }
//                    }
//                ),
//                xAxisData = xAxisData,
//                yAxisData = yAxisData,
//                gridLines = GridLines(
//                    color = MaterialTheme.colorScheme.outlineVariant
//                ),
//                backgroundColor = MaterialTheme.colorScheme.surface
//            )
//        )
    }
}

// 辅助函数，获取不同历史数据集的颜色
fun getLineColor(index: Int): Color {
    val colors = listOf(
        Color.Blue,
        Color.Cyan,
        Color.Green,
        Color(0xFF8BC34A),
        Color.Red,
        Color(0xFFFF9800),
        Color(0xFF9C27B0),
        Color(0xFF9C27B0)
        // 可以根据需要添加更多颜色
    )
    return colors.getOrElse(index % colors.size) { Color.Gray }
}