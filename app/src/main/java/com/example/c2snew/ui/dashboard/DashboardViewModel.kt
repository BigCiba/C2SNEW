package com.example.c2snew.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {
    private val _height = MutableLiveData<String>()
    private val _width = MutableLiveData<String>()

    fun setHeight(value: String) {
        _height.value = value
    }
    fun getHeight(): String? {
        return _height.value
    }
    fun setWidth(value: String) {
        _width.value = value
    }
    fun getWidth(): String? {
        return _width.value
    }
}