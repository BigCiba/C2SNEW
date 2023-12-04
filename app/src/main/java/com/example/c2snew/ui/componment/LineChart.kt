package com.example.c2snew.ui.componment

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.yml.charts.common.model.Point

@Composable
fun LineChart(
    lines: List<List<Point>> = emptyList(),
    xAxis: List<String>,
    yAxis: List<String>,
    xTitle: String,
    yTitle: String,
) {
    val textMeasurer = rememberTextMeasurer()
    var canvasScale by remember { mutableFloatStateOf(1f) }
    var canvasOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .zIndex(-1f)
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                val newScale = canvasScale * zoom
                canvasScale = when {
                    newScale > 2f -> 2f
                    newScale < 1f -> 1f
                    else -> newScale
                }

                val scaledChartWidth = (size.width - 200f) * canvasScale
                val scaledChartHeight = (size.height - 200f) * canvasScale

                canvasOffset = if (canvasScale > 1) {
                    // 限制平移范围在原有显示内容的边界内
                    Offset(
                        x = (canvasOffset.x + pan.x * canvasScale).coerceIn(-(scaledChartWidth - (size.width - 200f)), 0f),
                        y = (canvasOffset.y + pan.y * canvasScale).coerceIn(-(scaledChartHeight - (size.height - 200f)), 0f)
                    )
                } else {
                    Offset(0f, 0f)
                }
            }
        }) {
        drawIntoCanvas { canvas ->
            canvas.scale(canvasScale)
            canvas.translate(canvasOffset.x, canvasOffset.y)

            val offset = Offset(140f, 50f)
        val chartSize = Size(size.width - 200f,size.height - 200f)

        drawRect(
            color = Color.Gray,
            topLeft = offset,
            size = chartSize,
            style = Stroke(width = 2.dp.toPx())
        )
        rotate(degrees = -90F, pivot = Offset(10f, offset.y + chartSize.height * 0.5f + 40f)) {
            drawText(
                textMeasurer,
                yTitle,
                style = TextStyle(textAlign = TextAlign.Center),
                topLeft = Offset(10f, offset.y + chartSize.height * 0.5f + 40f)
            )
        }
        drawText(
            textMeasurer,
            xTitle,
            size = Size(400f,60f),
            style = TextStyle(textAlign = TextAlign.Center),
            topLeft = Offset(offset.x + chartSize.width * 0.5f - 200f, offset.y + chartSize.height + 100f )
        )

        for ((index, item) in yAxis.withIndex()) {
            val startY = offset.y + index * chartSize.height / (yAxis.size - 1)  // 调整起始点的 Y 轴位置
            drawLine(
                color = Color.Gray,
                start = Offset(offset.x-20f, startY),
                end = Offset(offset.x, startY),
                strokeWidth = 2.dp.toPx(),
            )
            drawText(
                textMeasurer,
                item,
                size = Size(80f,40f),
                style = TextStyle(textAlign = TextAlign.Center),
                topLeft = Offset(offset.x - 100f, startY - 20f)
            )
        }
        for ((index, item) in xAxis.withIndex()) {
            val startX = offset.x + index * chartSize.width / (xAxis.size - 1)  // 调整起始点的 Y 轴位置
            drawLine(
                color = Color.Gray,
                start = Offset(startX, chartSize.height + offset.y),
                end = Offset(startX, chartSize.height + offset.y + 20f),
                strokeWidth = 2.dp.toPx(),
            )
            drawText(
                textMeasurer,
                item,
                size = Size(100f,40f),
                style = TextStyle(textAlign = TextAlign.Center),
                topLeft = Offset(startX - 50f, chartSize.height + offset.y + 40f)
            )
        }
        // 图例
        if (lines.size > 1) {
            lines.forEachIndexed {lineIndex, points ->
                drawText(
                    textMeasurer,
                    "L${lineIndex + 1}",
                    topLeft = Offset(offset.x +chartSize.width - 140f, offset.y + 20f + 42f * lineIndex)
                )
                drawLine(
                    color = getLineColor(lineIndex),
                    start = Offset(offset.x +chartSize.width - 80f, offset.y + 40f + 42f * lineIndex),
                    end = Offset(offset.x +chartSize.width - 20f, offset.y + 40f + 42f * lineIndex),
                    strokeWidth = 2.dp.toPx(),
                )
            }
        }
        lines.forEachIndexed {lineIndex, points ->
            if (points.size >= 2) {
                val path = Path()
                val scaleX = chartSize.width / 1280f
                val scaleY = chartSize.height / 255f
                points.forEachIndexed { index, point ->
                    val x = point.x * scaleX + offset.x
                    val y = chartSize.height - point.y * scaleY + offset.y

                    // 第一个点使用 moveTo，其余的点使用 lineTo 连接
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                    }

                    // 画线
                    drawPath(path = path, color = getLineColor(lineIndex), style = Stroke(width = 2.dp.toPx()))
                }
            }
        }


    }
}

fun getLineColor(index: Int): Color {
    val colors = listOf(
        Color.Gray,
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