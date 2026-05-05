package com.example.weatherol.ui.forecast

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherol.data.common.DataResult
import com.example.weatherol.data.remote.model.WeatherResponse
import com.example.weatherol.data.repository.WeatherRepository

// 数据类
data class HourlyForecast(
    val time: String,
    val temp: String,
    val iconRes: Int
)

data class DailyForecast(
    val day: String,
    val highTemp: String,
    val lowTemp: String,
    val iconRes: Int,
    val weatherDesc: String
)

@Composable
fun ForecastScreen(
    latitude: Double = 39.9042,
    longitude: Double = 116.4074
) {
    val repo = remember { WeatherRepository() }
    // 关键修复：去掉 by，改用 =，避免 property delegate 报错
    val state = remember { mutableStateOf<DataResult<WeatherResponse>?>(null) }

    LaunchedEffect(latitude, longitude) {
        Log.d("WeatherLog", "Forecast 开始请求")
        val result = repo.fetchWeather(latitude, longitude)
        state.value = result
        Log.d("WeatherLog", "Forecast 请求完成：${state.value}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F9FF))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "未来预报",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A202C)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val currentState = state.value) {
            is DataResult.Success -> {
                val response = currentState.data
                val hourly = response.hourly
                val daily = response.daily

                val hourlyItems = mutableListOf<HourlyForecast>()
                repeat(8) { i ->
                    val temp = hourly?.temperature2m?.getOrNull(i) ?: 0.0
                    val time = hourly?.time?.getOrNull(i)?.takeLast(5) ?: "$i:00"
                    hourlyItems.add(
                        HourlyForecast(
                            time = time,
                            temp = "${temp.toInt()}°",
                            iconRes = android.R.drawable.ic_menu_compass
                        )
                    )
                }

                val dailyItems = mutableListOf<DailyForecast>()
                val dayNames = listOf("今天", "周二", "周三", "周四", "周五", "周六", "周日")
                repeat(7) { i ->
                    val max = daily?.temperature2mMax?.getOrNull(i) ?: 0.0
                    val min = daily?.temperature2mMin?.getOrNull(i) ?: 0.0
                    dailyItems.add(
                        DailyForecast(
                            day = dayNames[i],
                            highTemp = "${max.toInt()}°",
                            lowTemp = "${min.toInt()}°",
                            iconRes = android.R.drawable.ic_menu_compass,
                            weatherDesc = "晴"
                        )
                    )
                }

                HourlyForecastSection(hourlyItems)
                Spacer(Modifier.height(20.dp))
                DailyForecastSection(dailyItems)
            }

            is DataResult.Error -> {
                Text(
                    text = "加载失败：${currentState.message}",
                    color = Color.Red,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun HourlyForecastSection(hourlyList: List<HourlyForecast>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("24小时预报", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(hourlyList) { HourlyForecastItem(it) }
            }
        }
    }
}

@Composable
fun HourlyForecastItem(forecast: HourlyForecast) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(forecast.time, color = Color.Gray)
        Spacer(Modifier.height(6.dp))
        Image(
            painter = painterResource(forecast.iconRes),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(forecast.temp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DailyForecastSection(dailyList: List<DailyForecast>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("7天预报", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            dailyList.forEachIndexed { index, item ->
                DailyForecastItem(item)
                if (index != dailyList.lastIndex) {
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE2E8F0)))
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(forecast: DailyForecast) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(forecast.day, Modifier.weight(1f))
        Row(Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(forecast.iconRes),
                contentDescription = null,
                Modifier.size(24.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(forecast.weatherDesc, color = Color.Gray)
        }
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
            Text(forecast.highTemp)
            Spacer(Modifier.width(8.dp))
            Text(forecast.lowTemp, color = Color.Gray)
        }
    }
}