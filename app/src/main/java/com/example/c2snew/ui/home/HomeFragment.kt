package com.example.c2snew.ui.home

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import co.yml.charts.common.model.Point
import com.example.c2snew.CameraViewModel
import com.example.c2snew.R
import com.example.c2snew.databinding.FragmentHomeBinding
import com.example.c2snew.ui.dashboard.DashboardViewModel
import com.example.c2snew.ui.page.MainPage
import com.example.c2snew.ui.page.SettingPage
import com.example.c2snew.ui.theme.Material3Theme
import com.github.mikephil.charting.data.Entry
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.ausbc.callback.IPreviewDataCallBack
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.jiangdg.ausbc.utils.ToastUtils
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.IAspectRatio
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


class HomeFragment : CameraFragment() {
    private lateinit var mViewBinding: FragmentHomeBinding
    private var widthRecord : Int = 1280
    private var heightRecord : Int = 720
    private var toast : Boolean = false
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var cameraViewModel: CameraViewModel
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private var chartData: List<Point> = listOf(Point(0f,0f))
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = mViewBinding.root

        // 在其他 Fragment 中获取共享的 ViewModel 实例
        dashboardViewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
        cameraViewModel = ViewModelProvider(requireActivity())[CameraViewModel::class.java]
        val composeView = root.findViewById<ComposeView>(R.id.composeView)
        composeView.setContent {
            Material3Theme {
                val navList = listOf(
                    Pair("Main", R.drawable.baseline_camera_24),
                    Pair("Raw", R.drawable.baseline_image_24),
                    Pair("Main", R.drawable.baseline_ssid_chart_24),
                    Pair("Setting", R.drawable.baseline_settings_24),
                )
                var navIndex by remember {
                    mutableIntStateOf(0)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    navList[navIndex].first,
                                    maxLines = 1,
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { /* doSomething() */ }) {
                                    Icon(
                                        painter = painterResource(id = navList[navIndex].second),
                                        contentDescription = navList[navIndex].first
                                    )
                                }
                            },
                            actions = {
                                if (navIndex < 3) {
                                    IconButton(onClick = {
                                        val dataPoint = (0..1000).map {
                                            Point(it.toFloat(), Random.nextFloat() * 255f)
                                        }
                                        cameraViewModel.setData(dataPoint)
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.play),
                                            contentDescription = "Play"
                                        )
                                    }
                                    IconButton(onClick = { /* doSomething() */ }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                                            contentDescription = "Camera"
                                        )
                                    }
                                    IconButton(onClick = { /* doSomething() */ }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.save),
                                            contentDescription = "Save"
                                        )
                                    }
                                }
                            }
                        )
                    },
                    content = { innerPadding->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            MainPage(navIndex==0, cameraViewModel)
                            SettingPage(navIndex==3)
                        }
                    },
                    bottomBar = {
                        NavigationBar {
                            navList.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = { Icon(painter = painterResource(id = item.second), contentDescription = item.first) },
                                    label = { Text(item.first) },
                                    selected = index == navIndex,
                                    onClick = {navIndex = index }
                                )
                            }
                        }
                    }
                )
            }
        }
        return root
    }
    override fun initView() {
        // 直接在数据回调中更新数据会导致崩溃，估计是死循环了，所以用计时器异步更新
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 30) {
            override fun onTick(millisUntilFinished: Long) {
                cameraViewModel.setData(chartData)
            }
            override fun onFinish() {
            }
        }
        super.initView()
    }

    override fun onCameraState(
        self: MultiCameraClient.ICamera,
        code: ICameraStateCallBack.State,
        msg: String?
    ) {
        when (code) {
            ICameraStateCallBack.State.OPENED -> handleCameraOpened()
            ICameraStateCallBack.State.CLOSED -> handleCameraClosed()
            ICameraStateCallBack.State.ERROR -> handleCameraError(msg)
        }
    }
    private fun handleCameraError(msg: String?) {
        ToastUtils.show("camera opened error: $msg")
    }

    private fun handleCameraClosed() {
        Toast.makeText(context, "USB Camera Closed", Toast.LENGTH_SHORT).show()
        if (isTimerRunning) {
            isTimerRunning = false
            countDownTimer.cancel()
        }
    }
    override fun getCameraRequest(): CameraRequest {
        return CameraRequest.Builder()
            .setPreviewWidth(640)  // initial camera preview width
            .setPreviewHeight(480) // initial camera preview height
            .create()
    }
    private fun handleCameraOpened() {
        if (!isTimerRunning) {
            Toast.makeText(context, "USB Camera Open", Toast.LENGTH_SHORT).show()
            isTimerRunning = true
            countDownTimer.start()
        }
        getCurrentCamera()?.addPreviewDataCallBack( object : IPreviewDataCallBack {
            override fun onPreviewData(
                data: ByteArray?,
                width: Int,
                height: Int,
                format: IPreviewDataCallBack.DataFormat
            ) {
                if (data != null) {
                    // 更新成实际相机的宽度
                    if (widthRecord != width) {
                        Toast.makeText(context, "width:${width},height:${height}", Toast.LENGTH_SHORT).show()
                        widthRecord = width
                    }
//                    if (heightRecord != height) {
//                        heightRecord = height
//                    }

                    val averagedData = processData(data,width,height,format)
//                    cameraViewModel.setData(averagedData)
                    chartData = averagedData

                } else {
                    // 如果 data 为空，执行相应的处理
                }
            }
        })
    }

    override fun getCameraView(): IAspectRatio {
        return AspectRatioTextureView(requireContext())
    }

    override fun getCameraViewContainer(): ViewGroup {
        return mViewBinding.cameraViewContainer
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        mViewBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mViewBinding.root
    }

    override fun getGravity(): Int = Gravity.TOP
    // 图表
    private fun processData(byteArray: ByteArray, imageWidth: Int, imageHeight: Int,format: IPreviewDataCallBack.DataFormat): List<Point> {
        val averagedData = calculateAverageBrightnessValues(byteArray, imageWidth, imageHeight, format)
        var dataPoints = listOf<Point>()
        for (x in averagedData.indices ) {
            dataPoints = dataPoints+Point(x.toFloat(),averagedData[x])
        }
        return dataPoints;
    }
    private fun calculateAverageBrightnessValues(data: ByteArray, imageWidth: Int, imageHeight: Int,format: IPreviewDataCallBack.DataFormat): FloatArray {
        val averages = FloatArray(imageWidth) // 用于存储每个 X 轴上的平均亮度值
//        val height = (dashboardViewModel.getHeight() ?: "0").toInt()
//        val width = (dashboardViewModel.getWidth() ?: "0").toInt()
        val height = 0
        val width = 0
//        val min = max(height - width, 0)
//        val max = min(height + width, imageHeight)
        val min = imageHeight / 2 - 20
        val max = imageHeight / 2 + 20
        if (format == IPreviewDataCallBack.DataFormat.RGBA) {
            val bytesPerPixel = 4

            for (x in 0 until imageWidth) {
                var sum = 0f // 使用浮点数类型
                for (y in min until max) {
                    // 计算像素在数组中的索引
                    val index = (x + y * imageWidth) * bytesPerPixel

                    // 提取像素的RGBA值
                    val r = data[index].toInt() and 0xFF
                    val g = data[index + 1].toInt() and 0xFF
                    val b = data[index + 2].toInt() and 0xFF

                    // 计算亮度值
                    val brightness = 0.299 * r + 0.587 * g + 0.114 * b

                    // 累加亮度值
                    sum += brightness.toFloat() // 使用浮点数类型
                }
                // 计算平均亮度值
                val average = sum / (max - min)
                averages[x] = average
            }
        } else {
            val bytesPerPixel = 2
            for (x in 0 until imageWidth) {
                var sum = 0f // 使用浮点数类型
                for (y in min until max) {
                    // 计算像素在数组中的索引
                    val index = (x + y * imageWidth) * bytesPerPixel

                    // 提取亮度
                    val y = data[index].toInt() and 0xFF

                    // 累加亮度值
                    sum += y.toFloat() // 使用浮点数类型
                }
                // 计算平均亮度值
                val average = sum / (max - min)
                averages[x] = average
            }
        }


        return averages
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
