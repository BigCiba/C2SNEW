package com.example.c2snew.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun Spectrum(visible:Boolean, viewModel: CameraViewModel) {
    val pointsData by viewModel.chartPointList.observeAsState(initial = listOf(Point(0f,0f)))
    val steps = 1
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
        LineChart(
            modifier = Modifier
                .fillMaxWidth(),
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