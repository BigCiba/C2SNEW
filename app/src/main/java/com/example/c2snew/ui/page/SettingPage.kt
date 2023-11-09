package com.example.c2snew.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.c2snew.SettingViewModel
import com.example.c2snew.ui.home.LineView

@Composable
fun SettingPage(visible:Boolean,settingViewModel:SettingViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                alpha = if (visible) 1f else 0f,
                translationX = if (visible) 0f else 1000f
            ),
//                                verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                SettingTag("Spectrum")

                SettingInput("Center", settingViewModel)
                SettingInput("Width", settingViewModel)
            }

            item {
                SettingTag("Wavelength calibration")

                SettingInput("a0", settingViewModel)
                SettingInput("a1", settingViewModel)
                SettingInput("a2", settingViewModel)
                SettingInput("a3", settingViewModel)
            }
            item {
                SettingTag("Camera")
                SettingInput("Exposure", settingViewModel)
                SettingInput("FPS", settingViewModel)
                SettingInput("Gain", settingViewModel)
            }
        }
    }
}
@Composable
fun SettingInput(name: String,settingViewModel:SettingViewModel) {
    // 在组件范围内持有 ViewModel
    var text by rememberSaveable { mutableStateOf(settingViewModel.getValue(name)) }
    text?.let { it ->
        OutlinedTextField(
            value = it,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                settingViewModel.setValue(name, it)
                text = it
            },
            label = { Text(name) }
        )
    }
}
@Composable
fun SettingTag(name: String) {
    Text(
        text = name,
        fontSize = TextUnit(20f, TextUnitType.Sp),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp)
    )
}