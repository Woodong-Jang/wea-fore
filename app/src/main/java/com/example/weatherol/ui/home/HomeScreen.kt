package com.example.weatherol.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.weatherol.utils.ActivityRecommendation
import com.example.weatherol.utils.WeatherActivityHelper

@Composable
fun HomeScreen() {
    val weatherRepository = remember { WeatherRepository() }
    var weatherResult by remember { mutableStateOf<DataResult<WeatherResponse>?>(null) }
    val isCelsius by AppState.isCelsius

    // 👇 从全局获取经纬度（切换城市自动变化）
    val lat = AppState.currentLat.value
    val lon = AppState.currentLon.value
    val cityName = AppState.currentCityName.value

    // 👇 城市变化 → 自动重新请求天气
    LaunchedEffect(lat, lon) {
        weatherResult = weatherRepository.fetchWeather(lat, lon)
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
            Icon(Icons.Default.LocationOn, null, tint = AppState.themeColor.value)
            Spacer(Modifier.width(6.dp))
            // 👇 显示当前城市名
            Text("$cityName 天气", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(30.dp))

        when (weatherResult) {
            is DataResult.Success -> {
                val weather = (weatherResult as DataResult.Success<WeatherResponse>).data
                val current = weather.current

                val tempC = current?.temperature2m ?: 0.0
                val tempF = tempC * 9 / 5 + 32
                val displayTemp = if (isCelsius) {
                    "%.1f°C".format(tempC)
                } else {
                    "%.1f°F".format(tempF)
                }

                Text(
                    text = displayTemp,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppState.themeColor.value
                )

                Spacer(Modifier.height(10.dp))
                Text(getWeatherText(current?.weatherCode), fontSize = 24.sp, color = Color.Gray)
                Spacer(Modifier.height(40.dp))

                val recommendation = WeatherActivityHelper.getRecommendation(
                    current?.weatherCode,
                    current?.temperature2m ?: 0.0
                )

                ActivityRecommendationCard(recommendation)

                Spacer(Modifier.height(24.dp))

                // 只保留真实的湿度
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    WeatherInfoCard(title = "湿度", value = "${current?.relativeHumidity2m}%")
                }
            }
            is DataResult.Error -> {
                Text(
                    text = "加载失败: ${(weatherResult as DataResult.Error).message}",
                    color = Color.Red
                )
            }
            else -> {
                CircularProgressIndicator(
                    color = AppState.themeColor.value
                )
            }
        }
    }
}

fun getWeatherText(code: Int?): String {
    return when (code) {
        0 -> "晴天"
        1, 2, 3 -> "多云"
        45, 48 -> "雾"
        51, 53, 55 -> "小雨"
        61, 63, 65 -> "雨"
        71, 73, 75 -> "雪"
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

@Composable
fun ActivityRecommendationCard(recommendation: ActivityRecommendation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F0FE)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = recommendation.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2980B9)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = recommendation.description,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "💡 ${recommendation.tips}",
                fontSize = 13.sp,
                color = Color(0xFF666666)
            )
        }
    }
}