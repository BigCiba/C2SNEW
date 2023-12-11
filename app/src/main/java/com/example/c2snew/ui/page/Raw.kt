package com.example.c2snew.ui.page

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.Point
import com.example.c2snew.CameraViewModel
import com.example.c2snew.SettingViewModel
import com.example.c2snew.ui.componment.LineChart
import kotlin.math.pow

@SuppressLint("RememberReturnType")
@Composable
fun Raw(visible:Boolean, viewModel: CameraViewModel,settingModel:SettingViewModel) {
    val bitmapData by viewModel.bitmapData.observeAsState()
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density
    val dpValue = 16 // 以 dp 为单位的值
    val pxValue = (dpValue * density).toInt() // 计算对应的 px 值
    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                alpha = if (visible) 1f else 0f,
                translationX = if (visible) 0f else 1000f
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        bitmapData?.let {
//            Image(
//                bitmap = it.asImageBitmap(),
//                contentDescription = null,
//                modifier = Modifier
//                    .height(configuration.screenWidthDp.dp)
//                    .width(configuration.screenWidthDp.dp * 8f / 5f)
//                    .rotate(90f)
////                    .aspectRatio(5f / 8f)
//            )
//        } ?: Box(modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//            .rotate(90f)
//        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
        ) {
            bitmapData?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .width(1280.dp)
                        .height(800.dp)
                        .rotate(90f)
                        .scale(1.5f)
    //                    .aspectRatio(5f / 8f)
                )
            }
        }
    }
}