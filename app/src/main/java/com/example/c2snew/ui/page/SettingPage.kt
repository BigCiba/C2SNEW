package com.example.c2snew.ui.page

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.c2snew.SettingViewModel


// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
@Composable
fun SettingPage(visible:Boolean,settingViewModel:SettingViewModel) {
    var saveImage by rememberSaveable { mutableStateOf(settingViewModel.getToggleValue("SaveImage") ?: true) }
    var averageTime by rememberSaveable { mutableStateOf(settingViewModel.getAverageTime() ?: 0.5f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .graphicsLayer(
                alpha = if (visible) 1f else 0f,
                translationX = if (visible) 0f else 1000f
            ),
//                                verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                SettingTag("Spectrum")
                Row {
                    SettingInput("Center", settingViewModel, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(20.dp))
                    SettingInput("Width", settingViewModel, modifier = Modifier.weight(1f))
                }
            }

            item {
                SettingTag("Wavelength calibration")
                Row {
                    SettingInput("a0", settingViewModel, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(20.dp))
                    SettingInput("a1", settingViewModel, modifier = Modifier.weight(1f))
                }
                Row {
                    SettingInput("a2", settingViewModel, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(20.dp))
                    SettingInput("a3", settingViewModel, modifier = Modifier.weight(1f))
                }
            }
            item {
                SettingTag("Average time")
//                Row {
//                    SettingInput("Exposure", settingViewModel, modifier = Modifier.weight(1f))
//                    Spacer(modifier = Modifier.width(20.dp))
//                    SettingInput("FPS", settingViewModel, modifier = Modifier.weight(1f))
//                }
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                )  {
                    RadioButton(
                        selected = averageTime == 0f,
                        onClick = {
                            settingViewModel.setAverageTime(0f)
                            averageTime = 0f
                        }
                    )
                    Text(
                        text = "0s",
                        fontSize = TextUnit(20f, TextUnitType.Sp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    RadioButton(
                        selected = averageTime == 0.5f,
                        onClick = {
                            settingViewModel.setAverageTime(0.5f)
                            averageTime = 0.5f
                        }
                    )
                    Text(
                        text = "0.5s",
                        fontSize = TextUnit(20f, TextUnitType.Sp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    RadioButton(
                        selected = averageTime == 1f,
                        onClick = {
                            settingViewModel.setAverageTime(1f)
                            averageTime = 1f
                        }
                    )
                    Text(
                        text = "1s",
                        fontSize = TextUnit(20f, TextUnitType.Sp),
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                )  {
                    RadioButton(
                        selected = averageTime == 2f,
                        onClick = {
                            settingViewModel.setAverageTime(2f)
                            averageTime = 2f
                        }
                    )
                    Text(
                        text = "2s",
                        fontSize = TextUnit(20f, TextUnitType.Sp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    RadioButton(
                        selected = averageTime == 5f,
                        onClick = {
                            settingViewModel.setAverageTime(5f)
                            averageTime = 5f
                        }
                    )
                    Text(
                        text = "5s",
                        fontSize = TextUnit(20f, TextUnitType.Sp),
                    )
                }
            }
            item {
                SettingTag("Other")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = saveImage,
                        onCheckedChange = {
                            settingViewModel.toggleValue("SaveImage", !saveImage)
                            saveImage = !saveImage
                        }
                    )
                    Text(
                        text = "Save raw image",
                        fontSize = TextUnit(20f, TextUnitType.Sp),
                    )
                }
            }
        }
    }
}
@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun SettingInput(name: String,settingViewModel:SettingViewModel,modifier: Modifier) {
    // 在组件范围内持有 ViewModel
    var text by rememberSaveable { mutableStateOf(settingViewModel.getValue(name)) }
    text?.let { it ->
        OutlinedTextField(
            modifier=modifier,
            value = it,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                settingViewModel.setValue(name, it)
                text = it
            },
            label = {
                if (name == "Center") {
                    Text("$name(0~800)")
                } else {
                    Text(name)
                }
            }
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