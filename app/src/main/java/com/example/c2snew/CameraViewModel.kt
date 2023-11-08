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
    private val _bitmapLiveData = MutableLiveData<ImageBitmap>()

    val dataLiveData: LiveData<List<Point>>
        get() = _dataLiveData

    fun setData(data: List<Point>) {
        _dataLiveData.value = data
    }


    val bitmapLiveData: LiveData<ImageBitmap>
        get() = _bitmapLiveData

    fun setBitMapData(data: ImageBitmap) {
        _bitmapLiveData.value = data
    }
}