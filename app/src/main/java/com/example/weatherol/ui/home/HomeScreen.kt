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
import com.example.weatherol.utils.WeatherActivityHelper
import com.example.weatherol.utils.ActivityRecommendation

private const val BEIJING_LAT = 39.9042
private const val BEIJING_LON = 116.4074

@Composable
fun HomeScreen() {
    val weatherRepository = WeatherRepository()
    var weatherResult by remember { mutableStateOf<DataResult<WeatherResponse>?>(null) }
    val isCelsius = AppState.isCelsius.value

    LaunchedEffect(Unit) {
        weatherResult = weatherRepository.fetchWeather(BEIJING_LAT, BEIJING_LON)
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
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "位置",
                tint = Color(0xFF6495ED)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "北京市",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

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
                    color = Color(0xFF2980B9)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = getWeatherText(current?.weatherCode),
                    fontSize = 24.sp,
                    color = Color.Gray
                )

                val recommendation = WeatherActivityHelper.getRecommendation(
                    current?.weatherCode,
                    current?.temperature2m ?: 0.0
                )

                Spacer(modifier = Modifier.height(16.dp))

                ActivityRecommendationCard(recommendation)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherInfoCard(title = "湿度", value = "${current?.relativeHumidity2m}%")
                    WeatherInfoCard(title = "气压", value = "1012 hPa")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherInfoCard(title = "风速", value = "3.2 m/s")
                    WeatherInfoCard(title = "能见度", value = "10 km")
                }
            }
            is DataResult.Error -> {
                Text(
                    text = "加载失败: ${(weatherResult as DataResult.Error).message}",
                    color = Color.Red
                )
            }
            else -> {
                CircularProgressIndicator()
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F7FA)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recommendation.description,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "💡 ${recommendation.tips}",
                fontSize = 13.sp,
                color = Color(0xFF666666)
            )
        }
    }
}