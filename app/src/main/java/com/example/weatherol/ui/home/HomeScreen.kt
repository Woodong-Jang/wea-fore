package com.example.weatherol.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherol.AppState
import com.example.weatherol.data.common.DataResult
import com.example.weatherol.data.remote.model.WeatherResponse
import com.example.weatherol.data.repository.WeatherRepository

@Composable
fun HomeScreen(
    latitude: Double = 39.9042,
    longitude: Double = 116.4074
) {
    val weatherRepository = remember { WeatherRepository() }//创建仓库对象
    var weatherState by remember { mutableStateOf<DataResult<WeatherResponse>?>(null) }//定义状态，接收返回结果
    //调用仓库
    LaunchedEffect(latitude, longitude) {
        weatherState = weatherRepository.fetchWeather(latitude, longitude)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            // 主题颜色自动跟着设置变
            Icon(Icons.Default.LocationOn, null, tint = AppState.themeColor.value)
            Spacer(Modifier.width(6.dp))
            Text("当前天气", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(30.dp))

        when (val state = weatherState) {
            is DataResult.Success -> {
                val current = state.data.current
                val temp = current?.temperature2m ?: 0.0//温度

                //温度自动根据全局单位切换
                val displayTemp = if (AppState.isCelsius.value) {
                    "${temp.toInt()} ℃"
                } else {
                    "${(temp * 1.8 + 32).toInt()} ℉"
                }

                //温度颜色自动跟随主题
                Text(
                    displayTemp,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppState.themeColor.value
                )

                Spacer(Modifier.height(10.dp))
                Text(getWeatherText(current?.weatherCode), fontSize = 24.sp, color = Color.Gray)//天气状态
                Spacer(Modifier.height(40.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                    WeatherInfoCard("湿度", "${current?.relativeHumidity2m}%")//湿度
                    WeatherInfoCard("气压", "——")
                }
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                    WeatherInfoCard("风速", "——")
                    WeatherInfoCard("能见度", "——")
                }
            }
            is DataResult.Error -> Text("加载失败: ${state.message}", color = Color.Red)
            else -> CircularProgressIndicator(
                // 加载圈也变色
                color = AppState.themeColor.value
            )
        }
    }
}

fun getWeatherText(code: Int?): String {
    return when (code) {
        0 -> "晴天"
        1,2,3 -> "多云"
        45,48 -> "雾"
        51,53,55 -> "小雨"
        61,63,65 -> "雨"
        71,73,75 -> "雪"
        else -> "未知"
    }
}

@Composable
fun WeatherInfoCard(title: String, value: String) {
    Card(
        modifier = Modifier.size(width = 150.dp, height = 90.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}