package com.example.c2snew.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.c2snew.R
import com.example.c2snew.databinding.FragmentDashboardBinding
import com.example.c2snew.databinding.FragmentHomeBinding
import com.example.c2snew.ui.dashboard.DashboardViewModel
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
    override fun initView() {
        // 在其他 Fragment 中获取共享的 ViewModel 实例
        dashboardViewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
        super.initView()
//        val root = mViewBinding.root
//
//        val lineChart = initChart(root)
//
//        refreshChart(lineChart, processData(generateRandomByteArray(1280,720),1280,720))
    }
    private fun generateRandomByteArray(width: Int, height: Int): ByteArray {
        val random = Random()
        val dataSize = width * height
        val data = ByteArray(dataSize)

        for (i in 0 until dataSize) {
            // 生成随机字节值（0 到 255 之间的整数）
            val randomByte = random.nextInt(256).toByte()
            data[i] = randomByte
        }

        return data
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
                        Toast.makeText(context, "widthRecord,width:${width},height:${height}", Toast.LENGTH_SHORT).show()
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
                    refreshChart(lineChart, processData(data,width,height))

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
    private fun processData(byteArray: ByteArray, imageWidth: Int, imageHeight: Int):  ArrayList<Entry> {

        // 将 ByteArray 转换为平均值数据
        val averagedData = calculateAveragePixelValues(byteArray,imageWidth,imageHeight)

        // 创建一个 LineDataSet，并设置数据点和样式
        val dataPoints = ArrayList<Entry>()
        for (x in averagedData.indices step 20) {
            dataPoints.add(Entry(x.toFloat(), averagedData[x]))
        }
        return dataPoints;
    }
    private fun calculateAveragePixelValues(data: ByteArray, imageWidth: Int, imageHeight: Int): FloatArray {
        val averages = FloatArray(imageWidth) // 用于存储每个 X 轴上的平均值
        val height = (dashboardViewModel.getHeight() ?: "0").toInt()
        val width = (dashboardViewModel.getWidth() ?: "0").toInt()
        val min = max(height - width, 0)
        val max = min(height + width, imageHeight)
        for (x in 0 until imageWidth) {
            var sum = 0f // 使用浮点数类型
            for (y in min until max) {
                // 计算像素在数组中的索引
                val index = x + y * imageWidth
                // 提取像素值（假设每个像素占用一个字节）
                val pixelValue = data[index].toInt() and 0xFF
                // 累加像素值
                sum += pixelValue.toFloat() // 使用浮点数类型
            }
            // 计算平均值
            val average = sum / (max - min)
            averages[x] = average
        }

        return averages
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