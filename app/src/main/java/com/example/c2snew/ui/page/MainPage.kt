package com.example.c2snew.ui.page

import android.annotation.SuppressLint
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.c2snew.CameraViewModel
import com.patrykandpatrick.vico.compose.axis.axisTickComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.ChartScrollState
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entriesOf
import com.patrykandpatrick.vico.core.entry.entryModelOf

@SuppressLint("RememberReturnType")
@Composable
fun MainPage(visible:Boolean, viewModel: CameraViewModel) {
    val titles = listOf("Pixel", "W1", "W2")
    var state by remember { mutableIntStateOf(0) }

    val pointsData by viewModel.dataLiveData.observeAsState(initial = listOf(Point(0f,0f)))

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

//    LaunchedEffect(data) {  updateData() }
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
            .background(Color.Black)) {
//            bitmap?.let { BitmapPainter(it, IntOffset.Zero, IntSize.Zero) }?.let {
//                Image(
//                    painter = it,
//                    contentDescription = "Image"
//                )
//            }
        }

//        Chart(
//            chart = lineChart(
//                axisValuesOverrider = AxisValuesOverrider.fixed(maxY = 1000f, minY = 0f),
//            ),
//            chartModelProducer = chartEntryModelProducer,
//            startAxis = rememberStartAxis(),
//            bottomAxis = rememberBottomAxis(),
//            isZoomEnabled = false,
//        )
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
                                color = MaterialTheme.colorScheme.secondary
                            ),
//                            IntersectionPoint(),
//                            SelectionHighlightPoint(),
//                            ShadowUnderLine(),
//                            SelectionHighlightPopUp()
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