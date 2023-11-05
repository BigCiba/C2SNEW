package com.example.c2snew

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.yml.charts.common.model.Point
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import java.util.Random

class CameraViewModel : ViewModel(){
    private val _dataLiveData = MutableLiveData<List<Point>>()

    val dataLiveData: LiveData<List<Point>>
        get() = _dataLiveData

    fun setData(data: List<Point>) {
        _dataLiveData.value = data
    }
    val center = MutableLiveData<Int>(1)
    val chartEntryModelProducer = ChartEntryModelProducer(List(4) { entryOf(it, kotlin.random.Random(1).nextFloat() * 16f) })
    val image = MutableLiveData<ImageBitmap?>(null)
    private val _chartData = MutableLiveData<List<FloatEntry>>()
    fun setChartData(chartData: List<FloatEntry>) {
        _chartData.value = chartData
    }
    fun getChartData(): List<FloatEntry>? {
        return _chartData.value
    }
}