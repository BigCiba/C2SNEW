package com.example.c2snew.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
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
import com.example.c2snew.R
import com.example.c2snew.databinding.FragmentHomeBinding
import com.example.c2snew.databinding.FragmentNotificationsBinding
import com.example.c2snew.ui.dashboard.DashboardViewModel
import com.example.c2snew.ui.notifications.NotificationsViewModel
import com.example.c2snew.ui.page.MainPage
import com.example.c2snew.ui.page.SettingPage
import com.example.c2snew.ui.theme.Material3Theme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.ausbc.callback.IPreviewDataCallBack
import com.jiangdg.ausbc.utils.ToastUtils
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.IAspectRatio
import java.util.Random
import kotlin.math.max
import kotlin.math.min

class HomeFragment : CameraFragment() {
    private lateinit var mViewBinding: FragmentHomeBinding
    private var widthRecord : Int = 1280
    private var heightRecord : Int = 720
    private var toast : Boolean = false
    private lateinit var dashboardViewModel: DashboardViewModel
//    private  lateinit var markChart: LineChart
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
                                    IconButton(onClick = { /* doSomething() */ }) {
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
                            MainPage(navIndex==0)
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
        val root: View = mViewBinding.root
        val lineView = root.findViewById<LineView>(R.id.LineView)
        val height = (dashboardViewModel.getHeight() ?: "0").toFloat()
        lineView.setLineCoordinates(height)
        super.initView()
    }

    override fun initData() {
        super.initData()
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
        ToastUtils.show("camera closed success")
    }

    private fun handleCameraOpened() {
        ToastUtils.show("camera opened success")

        initChart(mViewBinding.root)
//        initMarkChart(mViewBinding.root)
//        drawLine()
        getCurrentCamera()?.addPreviewDataCallBack( object : IPreviewDataCallBack {
            override fun onPreviewData(
                data: ByteArray?,
                width: Int,
                height: Int,
                format: IPreviewDataCallBack.DataFormat
            ) {
                if (data != null) {
                    val root = mViewBinding.root
                    val lineChart = root.findViewById<LineChart>(R.id.lineChart)
                    // 更新成实际相机的宽度
                    if (widthRecord != width) {
                        Toast.makeText(context, "width:${width},height:${height},format:${format},length:${data.size}", Toast.LENGTH_SHORT).show()
                        widthRecord = width
                        val xAxis = lineChart.xAxis
                        xAxis.axisMaximum = width.toFloat()
                    }
//                    if (heightRecord != height) {
//                        heightRecord = height
//                    }
                    if (!toast) {
                        toast = true
                        // 创建并显示一个短暂的 Toast 消息
//                        Toast.makeText(context, "width:${width},height:${height}", Toast.LENGTH_SHORT).show()
                    }
                    refreshChart(lineChart, processData(data,width,height,format))

                } else {
                    // 如果 data 为空，执行相应的处理
                    ToastUtils.show("onPreviewData: Data is null")
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
    private fun processData(byteArray: ByteArray, imageWidth: Int, imageHeight: Int,format: IPreviewDataCallBack.DataFormat):  ArrayList<Entry> {

        // 将 ByteArray 转换为平均值数据
        val averagedData = calculateAverageBrightnessValues(byteArray,imageWidth,imageHeight,format)

        // 创建一个 LineDataSet，并设置数据点和样式
        val dataPoints = ArrayList<Entry>()
        for (x in averagedData.indices ) {
            dataPoints.add(Entry(x.toFloat(), averagedData[x]))
        }
        return dataPoints;
    }
    private fun calculateAverageBrightnessValues(data: ByteArray, imageWidth: Int, imageHeight: Int,format: IPreviewDataCallBack.DataFormat): FloatArray {
        val averages = FloatArray(imageWidth) // 用于存储每个 X 轴上的平均亮度值
        val height = (dashboardViewModel.getHeight() ?: "0").toInt()
        val width = (dashboardViewModel.getWidth() ?: "0").toInt()
        val min = max(height - width, 0)
        val max = min(height + width, imageHeight)
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

    private  fun initMarkChart(root:View) {
//        val lineChart = root.findViewById<LineChart>(R.id.markChart)
//        val xAxis = lineChart.xAxis
//        val yAxis = lineChart.axisLeft
//        val rightYAxis = lineChart.axisRight
//        yAxis.isEnabled = false
//        xAxis.isEnabled = false
//        rightYAxis.isEnabled = false
//
//        xAxis.axisMinimum = 0f
//        xAxis.axisMaximum = 100f
//
//        yAxis.axisMinimum = 0f
//        yAxis.axisMaximum = heightRecord.toFloat()
//
//        lineChart.setDrawBorders(false)
//
//        lineChart.description.isEnabled = false
//        val legend = lineChart.legend
//        legend.isEnabled = false
//        markChart = lineChart
    }
    private fun drawLine() {
//        val height = (dashboardViewModel.getHeight() ?: "0").toFloat()
//        val width = (dashboardViewModel.getWidth() ?: "0").toFloat()
//        val centerDataPoints = ArrayList<Entry>()
//        centerDataPoints.add(Entry(0f, height))
//        centerDataPoints.add(Entry(100f, height))
//        val centerDataSet = LineDataSet(centerDataPoints, "")
//        centerDataSet.color = Color.RED
//        centerDataSet.lineWidth = 1f
//        centerDataSet.setDrawCircles(false)
//        centerDataSet.setDrawValues(false)
//
//        val topDataPoints = ArrayList<Entry>()
//        topDataPoints.add(Entry(0f, height + width))
//        topDataPoints.add(Entry(100f, height + width))
//        val topDataSet = LineDataSet(topDataPoints, "")
//        topDataSet.color = Color.BLUE
//        topDataSet.lineWidth = 0.5f
//        topDataSet.setDrawCircles(false)
//        topDataSet.setDrawValues(false)
//
//        val bottomDataPoints = ArrayList<Entry>()
//        bottomDataPoints.add(Entry(0f, height - width))
//        bottomDataPoints.add(Entry(100f, height - width))
//        val bottomDataSet = LineDataSet(bottomDataPoints, "")
//        bottomDataSet.color = Color.BLUE
//        bottomDataSet.lineWidth = 0.5f
//        bottomDataSet.setDrawCircles(false)
//        bottomDataSet.setDrawValues(false)
//
//        val lineData = LineData(centerDataSet, topDataSet,bottomDataSet)
//        markChart.data = lineData
//        markChart.invalidate()
    }

    private fun initChart(root: View): LineChart {
        val lineChart = root.findViewById<LineChart>(R.id.lineChart)
        val xAxis = lineChart.xAxis
        val yAxis = lineChart.axisLeft

        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = widthRecord.toFloat()

        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 255f

        val rightYAxis = lineChart.axisRight

        rightYAxis.isEnabled = false
        lineChart.setDrawBorders(true)

        lineChart.setBorderColor(Color.BLACK)
        lineChart.setBorderWidth(1f)

        lineChart.description.isEnabled = false
        val legend = lineChart.legend
        legend.isEnabled = false

        return lineChart
    }

    private fun refreshChart(lineChart: LineChart, dataPoints: List<Entry>) {
        if (dataPoints.isEmpty()) {
            return
        }

        val dataSet = LineDataSet(dataPoints, "")

        dataSet.color = Color.BLUE
        dataSet.lineWidth = 2f
//        dataSet.setCircleColor(Color.RED)
//        dataSet.setCircleRadius(5f)
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)

        lineChart.data = lineData
        lineChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}