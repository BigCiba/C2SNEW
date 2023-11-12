package com.example.c2snew

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.yml.charts.common.model.Point

class CameraViewModel : ViewModel(){
    private val _chartPointList = MutableLiveData<List<Point>>()
    private val _historyList = MutableLiveData<List<List<Point>>>().apply {
        value = emptyList() // 初始值设为空列表
    }

    val chartPointList: LiveData<List<Point>>
        get() = _chartPointList

    fun setData(data: List<Point>) {
        _chartPointList.value = data
    }

    fun saveHistory(data: List<Point>) {
        val currentList = _historyList.value.orEmpty() // 获取当前列表，如果为null则返回空列表
        val newList = currentList.toMutableList()

        if (newList.size >= 8) {
            newList.removeAt(0) // 删除最前面的元素
        }

        newList.add(data)
        _historyList.value = newList
    }

    val historyList: LiveData<List<List<Point>>>
        get() = _historyList
}