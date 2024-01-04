package com.example.c2snew.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import co.yml.charts.common.model.Point
import com.example.c2snew.CameraViewModel
import com.example.c2snew.R
import com.example.c2snew.SettingViewModel
import com.example.c2snew.databinding.FragmentHomeBinding
import com.example.c2snew.ui.componment.SaveDialog
import com.example.c2snew.ui.dashboard.DashboardViewModel
import com.example.c2snew.ui.page.MainPage
import com.example.c2snew.ui.page.Raw
import com.example.c2snew.ui.page.SettingPage
import com.example.c2snew.ui.page.Spectrum
import com.example.c2snew.ui.theme.Material3Theme
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.ausbc.callback.ICaptureCallBack
import com.jiangdg.ausbc.callback.IPreviewDataCallBack
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.jiangdg.ausbc.utils.ToastUtils
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.IAspectRatio
import com.patrykandpatrick.vico.core.extension.setFieldValue
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


class HomeFragment : CameraFragment() {
    private lateinit var mViewBinding: FragmentHomeBinding
    private var widthRecord : Int = 1280
    private var heightRecord : Int = 800
    private var toast : Boolean = false
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var cameraViewModel: CameraViewModel
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private var chartData: List<Point> = listOf(Point(0f,0f))
    private lateinit var bitmapData: ImageBitmap
    private lateinit var previewCallback: IPreviewDataCallBack

    private var playing: Boolean = false
    private lateinit var cameraContainer: FrameLayout

    private var accumulateData: ArrayList<List<Point>> = ArrayList()
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = mViewBinding.root
        cameraContainer = mViewBinding.cameraViewContainer

        // 相机预览数据回调
        previewCallback = object : IPreviewDataCallBack {
            override fun onPreviewData(
                data: ByteArray?,
                width: Int,
                height: Int,
                format: IPreviewDataCallBack.DataFormat
            ) {
                if (data != null) {
                    val byteBuffer = ByteBuffer.allocateDirect(width * height * 4)
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
                    byteBuffer.put(data.copyOfRange(0, width * height * 4))
                    byteBuffer.position(0)
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    bitmap?.copyPixelsFromBuffer(byteBuffer)
                    cameraViewModel.setBitmap(bitmap)
                    val averagedData = processData(data, width, height, format)
                    chartData = averagedData
                }
            }
        }

        // 在其他 Fragment 中获取共享的 ViewModel 实例
        dashboardViewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
        cameraViewModel = ViewModelProvider(requireActivity())[CameraViewModel::class.java]
        settingViewModel = ViewModelProvider(requireActivity())[SettingViewModel::class.java]
        val lineView = root.findViewById<LineView>(R.id.LineView)
        val composeView = root.findViewById<ComposeView>(R.id.composeView)
        // 嵌入compose布局
        composeView.setContent {
            Material3Theme {
                val navList = listOf(
                    Pair("Main", R.drawable.baseline_camera_24),
                    Pair("Raw", R.drawable.baseline_image_24),
                    Pair("Spectrum", R.drawable.baseline_ssid_chart_24),
                    Pair("Setting", R.drawable.baseline_settings_24),
                )
                var navIndex by remember {
                    mutableIntStateOf(0)
                }
                var play by remember {
                    mutableStateOf<Boolean>(playing)
                }
                val snackbarHostState = remember { SnackbarHostState() }
                val openAlertDialog = remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
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
                                        Play()
                                        play = playing
                                    }) {
                                        Icon(
                                            painter = painterResource(id = if (play) R.drawable.pause else R.drawable.play),
                                            contentDescription = "Play"
                                        )
                                    }
                                    IconButton(onClick = {
//                                        测试用
//                                        val testHeight = Random.nextFloat() * 255f
//                                        val dataPoint = (0..1000).map {
//                                            Point(it.toFloat(), testHeight)
//                                        }
                                        cameraViewModel.saveHistory(chartData)
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                                            contentDescription = "Camera"
                                        )
                                    }
                                    IconButton(onClick = {
                                        openAlertDialog.value = true
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.save),
                                            contentDescription = "Save"
                                        )
                                    }
                                    IconButton(onClick = {
                                        cameraViewModel.clearHistory()
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.delete),
                                            contentDescription = "Save"
                                        )
                                    }
                                }
                            }
                        )
                    },
                    content = { innerPadding->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            MainPage(navIndex==0, cameraViewModel,settingViewModel)
                            Raw(navIndex==1, cameraViewModel,settingViewModel)
                            Spectrum(navIndex==2, cameraViewModel,settingViewModel)
                            SettingPage(navIndex==3,settingViewModel)
                        }
                        when {
                            openAlertDialog.value -> {
                                SaveDialog(
                                    onDismissRequest = { openAlertDialog.value = false },
                                    onConfirmation = {
                                        openAlertDialog.value = false
                                        // 指定保存文件的路径
                                        val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                        val currentTime = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                                        val filePath = File(downloadFolder, "spectrochip/${it}-${currentTime}.csv").absolutePath
                                        val pointsData = (cameraViewModel.chartPointList.value)
                                        val historyData = cameraViewModel.historyList.value
                                        val imageData: Bitmap? = cameraViewModel.bitmapData.value
                                        val imageListData: List<Bitmap?> = cameraViewModel.imageList.value ?: emptyList()
                                        val imageSaveList = listOf(imageData) + imageListData


                                        val combinedData: List<List<Point>> = mutableListOf<List<Point>>().apply {
                                            if (pointsData != null) {
                                                if (pointsData.isNotEmpty()) {
                                                    add(expandList(pointsData,1280))
                                                }
                                            }
                                            if (historyData != null) {
                                                if (historyData.isNotEmpty()) {
                                                    addAll(historyData.map {pointList->
                                                        expandList(pointList,1280)
                                                    })
                                                }
                                            }
                                        }
                                        if (combinedData.isNotEmpty()) {
                                            val sa0 = settingViewModel.getValue("a0")
                                            val sa1 = settingViewModel.getValue("a1")
                                            val sa2 = settingViewModel.getValue("a2")
                                            val sa3 = settingViewModel.getValue("a3")
                                            var a0 = 0f
                                            var a1 = 0f
                                            var a2 = 0f
                                            var a3 = 0f
                                            if (sa0 != "" && sa1 != "" && sa2 != "" && sa3 != "" && sa0 != null && sa1 != null && sa2 != null && sa3 != null ) {
                                                a0 = sa0.toFloat()
                                                a1 = sa1.toFloat()
                                                a2 = sa2.toFloat()
                                                a3 = sa3.toFloat()
                                            }
                                            val csvContent = StringBuilder()

                                            // 添加 CSV 文件的标题行
                                            csvContent.append("Pixel")
                                            if (a0 != 0f) {
                                                csvContent.append(",Wavelength")
                                            }
                                            // 添加样本的列名
                                            for (i in 1..combinedData.size) {
                                                csvContent.append(",Sample$i")
                                            }
                                            csvContent.appendln()
                                            // 遍历每个点，将其坐标添加到 CSV 内容中
                                            val maxDataSize = combinedData.maxOfOrNull { it.size } ?: 0
                                            for (pixelIndex in 0 until maxDataSize) {
                                                // 添加 Pixel 列数据
                                                var pixel = combinedData.firstOrNull()?.getOrNull(pixelIndex)?.x?.toInt() ?: 0
                                                var wavelength = 0f
                                                csvContent.append(pixel.toString())
                                                if (a0 != 0f) {
                                                    wavelength = a0 + a1 * pixel.toFloat() + a2 * pixel.toFloat().pow(2) + a3 * pixel.toFloat().pow(3)
                                                    wavelength = wavelength * 10000 / 255
                                                    csvContent.append(",$wavelength")
                                                }
                                                // 添加样本列数据
                                                for (sampleIndex in 0 until combinedData.size) {
                                                    val sampleData = combinedData[sampleIndex]
                                                    var yValue = sampleData.getOrNull(pixelIndex)?.y ?: 0f
                                                    yValue = yValue * 10000 / 255
                                                    csvContent.append(", $yValue")
                                                }
                                                csvContent.appendln()
                                            }
                                            val directory = File(filePath).parentFile
                                            if (!directory.exists()) {
                                                directory.mkdirs() // 创建目录及其父目录（如果不存在）
                                            }
                                            // 将 CSV 内容写入文件
                                            File(filePath).writeText(csvContent.toString())


                                            // 存照片
                                            if (settingViewModel.getToggleValue("_saveImage") == true) {
                                                if (imageSaveList != null) {
                                                    if (imageSaveList.isNotEmpty()) {
                                                        for ((index, bitmap) in imageSaveList.withIndex()) {
                                                            val myDir = File(downloadFolder, "spectrochip/${it}-${index + 1}-${currentTime}.png").absolutePath
                                                            val directory = File(filePath).parentFile
                                                            if (!directory.exists()) {
                                                                directory.mkdirs() // 创建目录及其父目录（如果不存在）
                                                            }

                                                            try {
                                                                val out = FileOutputStream(myDir)
                                                                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
                                                                out.flush()
                                                                out.close()
                                                            } catch (e: IOException) {
                                                                e.printStackTrace()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
//                                            getCurrentCamera()?.captureImage(object : ICaptureCallBack {
//                                                override fun onBegin() {}
//
//                                                override fun onError(error: String?) {
//                                                    ToastUtils.show(error ?: "capture image failed")
//                                                }
//
//                                                override fun onComplete(path: String?) {
//                                                    ToastUtils.show(path ?: "capture image success")
//                                                }
//                                            }, File(downloadFolder, "spectrochip/${it}-${currentTime}.png").absolutePath)
                                        }
                                    },
                                    dialogTitle = "Save file",
                                )
                            }
                        }
                    },
                    bottomBar = {
                        NavigationBar {
                            navList.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = { Icon(painter = painterResource(id = item.second), contentDescription = item.first) },
                                    label = { Text(item.first) },
                                    selected = index == navIndex,
                                    onClick = {
                                        navIndex = index
                                        when (navIndex) {
                                            0 -> {
                                                getCurrentCamera()?.setRenderSize(1280,800)
                                                lineView.visibility = View.VISIBLE;

                                                val center = settingViewModel.getValue("Center")
                                                val width = settingViewModel.getValue("Width")
                                                if (center != null && center != "" && width != null && width != "") {
                                                    lineView.setLineCoordinates(center.toFloat(),width.toFloat())
                                                }
                                                val gain = settingViewModel.getValue("Gain")
                                                if (gain != null && gain != "") {
                                                    setGain(gain.toInt())
                                                }
                                            }
                                            1 -> {
                                                lineView.visibility = View.GONE;
                                            }
                                            2 -> {
                                                lineView.visibility = View.GONE;
                                            };
                                            3 -> {
                                                lineView.visibility = View.GONE;
                                            };
                                        }
                                    }
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
        super.initView()
        // 直接在数据回调中更新数据会导致崩溃，估计是死循环了，所以用计时器异步更新
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 20) {
            override fun onTick(millisUntilFinished: Long) {
//                cameraViewModel.setData(chartData)
                accumulateData.add(chartData)
                val ms = settingViewModel.getAverageTime()?.times(1000f)
                var length = 1
                if (ms != null) {
                    length = (ms / 20f).toInt()
                }
                if (accumulateData.size >= length) {
                    cameraViewModel.setData(processAccumulatedData())
//                    accumulateData.removeAt(0)
                    // 获取要删除的子列表
                    val sublistToRemove = accumulateData.subList(0, accumulateData.size-length)
                    // 清除子列表
                    sublistToRemove.clear()
                }
//                cameraViewModel.setBitmap(bitmapData)
//                cameraViewModel.setRawdata(rawData)
            }
            override fun onFinish() {
            }
        }
        getCameraRequest().isCaptureRawImage = true
        getCurrentCamera()?.updateResolution(1280, 800)
    }
    fun Play() {
        if (getCurrentCamera() != null ) {
            if (isTimerRunning) {
                Toast.makeText(context, "Pause", Toast.LENGTH_SHORT).show()
                isTimerRunning = false
                playing = false
                countDownTimer.cancel()
                closeCamera()
            } else {
                Toast.makeText(context, "Play", Toast.LENGTH_SHORT).show()
                isTimerRunning = true
                playing = true
                countDownTimer.start()
                openCamera()
            }
        }
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
            playing = false
            countDownTimer.cancel()
        }
        getCurrentCamera()?.removePreviewDataCallBack(previewCallback)
    }
    override fun getCameraRequest(): CameraRequest {
        return CameraRequest.Builder()
            .setPreviewWidth(1280)  // initial camera preview width
            .setPreviewHeight(800) // initial camera preview height
            .create()
    }
    private fun handleCameraOpened() {
        if (!isTimerRunning) {
            Toast.makeText(context, "USB Camera Open", Toast.LENGTH_SHORT).show()
            isTimerRunning = true
            playing = true
            countDownTimer.start()
        }
        getCurrentCamera()?.setRenderSize(1280,800)
        getCurrentCamera()?.addPreviewDataCallBack(previewCallback)
    }

    override fun getCameraView(): IAspectRatio {
        return AspectRatioTextureView(requireContext())
    }

    override fun getCameraViewContainer(): ViewGroup {
        return cameraContainer
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        mViewBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mViewBinding.root
    }

    override fun getGravity(): Int = Gravity.TOP

    // 获取平均数据
    private fun processAccumulatedData(): List<Point> {
        val numberOfLists = accumulateData.size
        val numberOfPoints = accumulateData[0].size // 假设每个 List<Point> 长度相同

        // 初始化用于存储平均值的列表
        val averagePoints = ArrayList<Point>(numberOfPoints)

        // 计算每个点的平均值
        for (pointIndex in 0 until numberOfPoints) {
            var averageX = 0f
            var averageY = 0f

            // 计算每个 List<Point> 中相同索引的点的平均值
            for (listIndex in 0 until numberOfLists) {
                val currentPoint = accumulateData[listIndex][pointIndex]
                averageX += currentPoint.x
                averageY += currentPoint.y
            }

            // 计算平均值并添加到结果列表
            averageX /= numberOfLists
            averageY /= numberOfLists

            val averagePoint = Point(averageX, averageY)
            averagePoints.add(averagePoint)
        }

        return averagePoints
    }

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
        var height = try {
            settingViewModel.getValue("Center")?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            0
        }
        val width = try {
            settingViewModel.getValue("Width")?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            0
        }
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
                var average = 0f
                if (max - min != 0) {
                    average =  sum / (max - min)
                }
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
                var average = 0f
                if (max - min != 0) {
                    average =  sum / (max - min)
                }
                averages[x] = average
            }
        }


        return averages
    }
    fun linearInterpolation(p1: Point, p2: Point, factor: Float): Point {
        val diffY = p2.y - p1.y
        val interpolatedY = p1.y + (diffY * factor).toInt()
        return Point(p1.x + 1, interpolatedY)
    }

    fun expandList(originalList: List<Point>, targetSize: Int): List<Point> {
        val expandedList = originalList.toMutableList()
        while (expandedList.size < targetSize) {
            for (i in 0 until expandedList.size - 1) {
                val p1 = expandedList[i]
                val p2 = expandedList[i + 1]
                val newPoint = linearInterpolation(p1, p2, 0.5f)
                expandedList.add(i + 1, newPoint)
                if (expandedList.size >= targetSize) break
            }
        }
        return expandedList
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
