package com.example.c2snew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingViewModel : ViewModel() {
    private val _center = MutableLiveData<String>("")
    private val _width = MutableLiveData<String>("20")
    private val _a0 = MutableLiveData<String>("")
    private val _a1 = MutableLiveData<String>("")
    private val _a2 = MutableLiveData<String>("")
    private val _a3 = MutableLiveData<String>("")
    private val _exposure = MutableLiveData<String>("")
    private val _fps = MutableLiveData<String>("")
    private val _gain = MutableLiveData<String>("")
    private val _saveImage = MutableLiveData<Boolean>( true)

    fun setValue(key: String, value: String) {
        when (key) {
            "Center" -> {
                _center.value = value
            }
            "Width" -> {
                _width.value = value
            }
            "a0" -> {
                _a0.value = value
            }
            "a1" -> {
                _a1.value = value
            }
            "a2" -> {
                _a2.value = value
            }
            "a3" -> {
                _a3.value = value
            }
            "Exposure" -> {
                _exposure.value = value
            }
            "FPS" -> {
                _fps.value = value
            }
            "Gain" -> {
                _gain.value = value
            }
        }
    }
    fun getValue(key: String): String? {
        when (key) {
            "Center" -> {
                return _center.value
            }
            "Width" -> {
                return _width.value
            }
            "a0" -> {
                return _a0.value
            }
            "a1" -> {
                return _a1.value
            }
            "a2" -> {
                return _a2.value
            }
            "a3" -> {
                return _a3.value
            }
            "Exposure" -> {
                return _exposure.value
            }
            "FPS" -> {
                return _fps.value
            }
            "Gain" -> {
                return _gain.value
            }
        }
        return ""
    }
    fun toggleValue(key: String, value: Boolean) {
        when (key) {
            "SaveImage" -> {
                _saveImage.value = value
            }
        }
    }
    fun getToggleValue(key:String): Boolean? {
        when (key) {
            "SaveImage" -> {
                return _saveImage.value
            }
        }
        return false
    }
}