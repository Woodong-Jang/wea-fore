package com.example.weatherol

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

object AppState {
    // 温度单位
    val isCelsius = mutableStateOf(true)

    // 主题
    val isDarkTheme = mutableStateOf(false)
}