package com.example.c2snew.ui.page

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.YuvImage
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
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
import java.io.ByteArrayOutputStream

@SuppressLint("RememberReturnType")
@Composable
fun MainPage(visible:Boolean, viewModel: CameraViewModel) {
    val titles = listOf("Pixel", "W1", "W2")
    var state by remember { mutableIntStateOf(0) }

    val pointsData by viewModel.dataLiveData.observeAsState(initial = listOf(Point(0f,0f)))
//    val bitmapData by viewModel.bitmapLiveData.observeAsState(initial = ImageBitmap(width = 20, height = 20))
    val bitmapData = ImageBitmap(width = 20, height = 20)

    val buffer = IntArray(200 * 200)
    for (y in 0 until 100) {
        for (x in 0 until 100) {
            val index = x + y * 4
            buffer[index] = 255
            buffer[index + 1] = 255
            buffer[index + 2] = 255
            buffer[index + 3] = 255
        }
    }
    bitmapData.readPixels(buffer,0,0,20,20)

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
//            .background(Color.Black)
        ) {
            Image(modifier = Modifier
                .fillMaxWidth(),
                painter = BitmapPainter(bitmapData),
                contentDescription = "Image")
        }
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f),
            lineChartData = LineChartData(
                linePlotData = LinePlotData(
                    lines = listOf(
                        Line(
                            dataPoints = pointsData,
                            LineStyle(
                                color = MaterialTheme.colorScheme.secondary,
                                width = 4f
                            ),
//                            IntersectionPoint(),
//                            SelectionHighlightPoint(),
//                            ShadowUnderLine(),
//                            SelectionHighlightPopUp()

                            shadowUnderLine = ShadowUnderLine()
                        )
                    ),
                ),
                xAxisData = xAxisData,
                yAxisData = yAxisData,
                gridLines = GridLines(
                    color = MaterialTheme.colorScheme.outlineVariant
                ),
                backgroundColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}