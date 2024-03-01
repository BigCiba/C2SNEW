package com.example.c2snew

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.yml.charts.common.model.Point

class CameraViewModel : ViewModel(){
    // 位图
    private val _bitmapData = MutableLiveData<Bitmap?>()
    val bitmapData: LiveData<Bitmap?>
        get() = _bitmapData

    fun setBitmap(bitmap: Bitmap?) {
        _bitmapData.postValue(bitmap)
    }

    // 图表数据
    private val _chartPointList = MutableLiveData<List<Point>>()
    private val _totalChartPointList = MutableLiveData<List<Point>>()
    // 图表历史数据
    private val _historyList = MutableLiveData<List<List<Point>>>().apply {
        value = emptyList() // 初始值设为空列表
    }
    private val _imageList = MutableLiveData<List<Bitmap>>().apply {
        value = emptyList() // 初始值设为空列表
    }

    val chartPointList: LiveData<List<Point>>
        get() = _chartPointList

    fun setData(data: List<Point>) {
        _chartPointList.value = data
    }
    val totalChartPointList: LiveData<List<Point>>
        get() = _totalChartPointList

    fun setTotalData(data: List<Point>) {
        _totalChartPointList.value = data
    }

    fun saveHistory(data: List<Point>) {
        val currentList = _historyList.value.orEmpty() // 获取当前列表，如果为null则返回空列表
        val newList = currentList.toMutableList()

        if (newList.size >= 7) {
            newList.removeAt(0) // 删除最前面的元素
        }

        newList.add(data)
        _historyList.value = newList

        val currentImageList = _imageList.value.orEmpty() // 获取当前列表，如果为null则返回空列表
        val newImageList = currentImageList.toMutableList()

        if (newImageList.size >= 7) {
            newImageList.removeAt(0) // 删除最前面的元素
        }

        _bitmapData.value?.let { newImageList.add(it) }
        _imageList.value = newImageList
    }
    fun clearHistory() {
        _historyList.value = emptyList()
        _imageList.value = emptyList()
    }

    val historyList: LiveData<List<List<Point>>>
        get() = _historyList

    val imageList: LiveData<List<Bitmap>>
        get() = _imageList
}