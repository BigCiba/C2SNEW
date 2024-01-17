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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.yml.charts.common.model.Point
import kotlin.math.abs

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
    var perpendicular by remember { mutableFloatStateOf(0f) }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .zIndex(-1f)
        .pointerInput(Unit) {
            detectTransformGestures { offset, pan, zoom, _ ->
                perpendicular = offset.x
                val newScale = canvasScale * zoom
                canvasScale = when {
                    newScale > 2f -> 2f
                    newScale < 1f -> 1f
                    else -> newScale
                }

                val scaledChartWidth = (size.width - 400f) * canvasScale
                val scaledChartHeight = (size.height - 400f) * canvasScale

                canvasOffset = if (canvasScale > 1) {
                    // 限制平移范围在原有显示内容的边界内
                    Offset(
                        x = (canvasOffset.x + pan.x * canvasScale).coerceIn(
                            -(scaledChartWidth - (size.width - 400f)),
                            0f
                        ),
                        y = (canvasOffset.y + pan.y * canvasScale).coerceIn(
                            -(scaledChartHeight - (size.height - 400f)),
                            0f
                        )
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
            var offsetX =  offset.x.coerceAtLeast(perpendicular).coerceAtMost(offset.x+ chartSize.width)

            // 虚线
            val pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(10f, 10f), // 设置虚线的样式和间隔
                phase = 0f // 设置虚线的起始偏移量
            )
            drawLine(
                color = Color.Red,
                start = Offset(offsetX, offset.y),
                end = Offset(offsetX, offset.y + chartSize.height),
                strokeWidth = 2.dp.toPx(),
                pathEffect = pathEffect
            )

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

            val gridHeight = chartSize.height / (yAxis.size - 1)
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
                if (index <  yAxis.size - 1) {
                    for (i in 1..3) {
                        val gridY = startY + (gridHeight / 4) * i
                        drawLine(
                            color = Color.Gray,
                            start = Offset(offset.x-10f, gridY),
                            end = Offset(offset.x, gridY),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
                }
            }
            // 横坐标分割
            val gridWidth = chartSize.width / (xAxis.size - 1)
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
                if (index <  xAxis.size - 1) {
                    for (i in 1..3) {
                        val gridX = startX + (gridWidth / 4) * i
                        drawLine(
                            color = Color.Gray,
                            start = Offset(gridX, chartSize.height + offset.y),
                            end = Offset(gridX, chartSize.height + offset.y + 10f),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
                }
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
            // 显示垂线
            var closePoints = mutableListOf<Offset>()
            lines.forEachIndexed {lineIndex, points ->
                if (points.size >= 2) {
                    val path = Path()
                    val scaleX = chartSize.width / 1280f
                    val scaleY = chartSize.height / 255f

                    var distance = 2000f
                    var closestX = 0f
                    var closestY = 0f
                    points.forEachIndexed { index, point ->
                        val x = point.x * scaleX + offset.x
                        val y = chartSize.height - point.y * scaleY + offset.y

                        if (abs(x - offsetX)  < distance) {
                            distance = abs(x - offsetX)
                            closestX = x
                            closestY = y
                        }
                        // 第一个点使用 moveTo，其余的点使用 lineTo 连接
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }
                    closePoints.add(Offset(closestX,closestY))

                    // 画线
                    drawPath(path = path, color = getLineColor(lineIndex), style = Stroke(width = 2.dp.toPx()))
                }
            }
            for ((index, offset) in closePoints.withIndex()) {
                if (offset.x != 2000f) {
                    drawText(
                        textMeasurer,
                        String.format("%.2f", ((1f - (offset.y - 50f) / chartSize.height) * 10000f)),
                        topLeft = Offset(offsetX, offset.y)
                    )
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