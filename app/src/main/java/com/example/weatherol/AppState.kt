package com.example.weatherol

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color

// 全局唯一状态，所有页面共享
object AppState {
    // 温度单位
    val isCelsius = mutableStateOf(true)

    // 主题
    val themeColor = mutableStateOf(Color(0xFF2196F3))
    val isDarkTheme = mutableStateOf(false)

    // 当前选中城市（首页/预报页用）
    val currentLat = mutableStateOf(39.9042)
    val currentLon = mutableStateOf(116.4074)
    val currentCityName = mutableStateOf("北京")

    // ✅ 全局城市列表（切换页面不会重置）
    val cityList = mutableStateListOf(
        City(1, "北京", 39.9042, 116.4074, "晴天"),
        City(2, "上海", 31.2304, 121.4737, "多云"),
        City(3, "广州", 23.1291, 113.2644, "小雨"),
        City(4, "深圳", 22.5431, 114.0579, "晴"),
        City(5, "杭州", 30.2741, 120.1551, "阴"),
        City(6, "成都", 30.5723, 104.0665, "雾"),
        City(7, "重庆", 29.5630, 106.5516, "多云"),
    )
}

// 城市数据类
data class City(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val weatherText: String = "多云"
)