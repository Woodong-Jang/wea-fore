package com.example.weatherol.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

// 天气效果类型
enum class WeatherEffect {
    AUTO, SUNNY, RAIN, SNOW, FOG
}

@Composable
fun HomeScreen() {
    val weatherRepository = remember { WeatherRepository() }
    var weatherResult by remember { mutableStateOf<DataResult<WeatherResponse>?>(null) }
    val isCelsius by AppState.isCelsius

    val lat = AppState.currentLat.value
    val lon = AppState.currentLon.value
    val cityName = AppState.currentCityName.value

    LaunchedEffect(lat, lon) {
        weatherResult = weatherRepository.fetchWeather(lat, lon)
    }

    var currentEffect by remember { mutableStateOf(WeatherEffect.AUTO) }

    Box(modifier = Modifier.fillMaxSize()) {
        WeatherParticleBackground(
            currentEffect = currentEffect,
            weatherCode = (weatherResult as? DataResult.Success<WeatherResponse>)?.data?.current?.weatherCode ?: 0
        )

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
                Text("$cityName 天气", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(30.dp))

            when (weatherResult) {
                is DataResult.Success -> {
                    val weather = (weatherResult as DataResult.Success<WeatherResponse>).data
                    val current = weather.current

                    val tempC = current?.temperature2m ?: 0.0
                    val humidity = (current?.relativeHumidity2m ?: 0.0).toDouble()

                    val feelTempC = calculateApparentTemperature(tempC, humidity)
                    val feelTempF = feelTempC * 9/5 + 32

                    val displayTemp = if (isCelsius) "%.1f°C".format(tempC)
                    else "%.1f°F".format(tempC * 9/5 + 32)

                    val displayFeelTemp = if (isCelsius) "%.1f°C".format(feelTempC)
                    else "%.1f°F".format(feelTempF)

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

                    // 湿度 + 体感温度 并排显示
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WeatherInfoCard(title = "湿度", value = "${current?.relativeHumidity2m}%")
                        Spacer(modifier = Modifier.width(16.dp))
                        WeatherInfoCard(title = "体感温度", value = displayFeelTemp)
                    }
                }
                is DataResult.Error -> {
                    Text(
                        text = "加载失败: ${(weatherResult as DataResult.Error).message}",
                        color = Color.Red
                    )
                }
                else -> {
                    CircularProgressIndicator(color = AppState.themeColor.value)
                }
            }
        }

        FloatingActionButton(
            onClick = {
                currentEffect = when (currentEffect) {
                    WeatherEffect.AUTO -> WeatherEffect.SUNNY
                    WeatherEffect.SUNNY -> WeatherEffect.RAIN
                    WeatherEffect.RAIN -> WeatherEffect.SNOW
                    WeatherEffect.SNOW -> WeatherEffect.FOG
                    WeatherEffect.FOG -> WeatherEffect.AUTO
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
            containerColor = AppState.themeColor.value.copy(alpha = 0.9f)
        ) {
            val btnText = when (currentEffect) {
                WeatherEffect.AUTO -> "自动"
                WeatherEffect.SUNNY -> "晴"
                WeatherEffect.RAIN -> "雨"
                WeatherEffect.SNOW -> "雪"
                WeatherEffect.FOG -> "雾"
            }
            Text(btnText, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// 🔥 核心：气象标准 体感温度计算公式
fun calculateApparentTemperature(tempC: Double, humidity: Double): Double {
    val e = humidity * 6.105 * exp((17.27 * tempC) / (237.7 + tempC)) / 100
    return tempC + 0.348 * e - 0.70 * 0 - 4.25
}

@Composable
fun WeatherParticleBackground(
    currentEffect: WeatherEffect,
    weatherCode: Int
) {
    val effect = if (currentEffect == WeatherEffect.AUTO) {
        when (weatherCode) {
            0, 1 -> WeatherEffect.SUNNY
            51, 53, 55, 61, 63, 65 -> WeatherEffect.RAIN
            71, 73, 75 -> WeatherEffect.SNOW
            45, 48 -> WeatherEffect.FOG
            else -> WeatherEffect.SUNNY
        }
    } else {
        currentEffect
    }

    val particles = remember {
        List(80) {
            mutableStateOf(
                Offset(Random.nextFloat(), Random.nextFloat()) to Random.nextFloat()
            )
        }
    }

    var tick by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            tick++
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { pState ->
            val (pos, rnd) = pState.value
            var x = pos.x * w
            var y = pos.y * h

            when (effect) {
                WeatherEffect.SUNNY -> {
                    x += sin((tick * 0.015f + rnd * 6.28f).toDouble()).toFloat() * 0.22f
                    y += cos((tick * 0.012f + rnd * 6.28f).toDouble()).toFloat() * 0.22f

                    if (x < 0) x = w
                    if (x > w) x = 0f
                    if (y < 0) y = h
                    if (y > h) y = 0f

                    drawCircle(
                        color = Color(0xFFFFD700).copy(alpha = 0.4f),
                        radius = 3f + rnd * 1f,
                        center = Offset(x, y)
                    )
                }

                WeatherEffect.RAIN -> {
                    y += 7f + rnd * 2.5f
                    x += 0.6f
                    if (y > h) { y = -25f; x = Random.nextFloat() * w }
                    drawLine(
                        color = Color(0xFF2196F3).copy(alpha = 0.8f),
                        start = Offset(x, y),
                        end = Offset(x - 1.5f, y - 25f),
                        strokeWidth = 2.2f
                    )
                }

                WeatherEffect.SNOW -> {
                    y += 1.2f + rnd * 0.6f
                    x += sin(tick * 0.05f + rnd * 6.28).toFloat() * 1f
                    if (y > h) { y = -8f; x = Random.nextFloat() * w }
                    drawCircle(
                        color = Color.White.copy(alpha = 0.9f),
                        radius = 4.5f + rnd * 2f,
                        center = Offset(x, y)
                    )
                }

                WeatherEffect.FOG -> {
                    x += sin(tick * 0.02f + rnd * 6.28).toFloat() * 0.6f
                    y += 0.15f + rnd * 0.15f
                    if (y > h) { y = -40f; x = Random.nextFloat() * w }
                    drawCircle(
                        color = Color(0xFFB0BEC5).copy(alpha = 0.4f),
                        radius = 35f + rnd * 20f,
                        center = Offset(x, y)
                    )
                }

                else -> {}
            }

            pState.value = Offset(x / w, y / h) to rnd
        }
    }
}

fun getWeatherText(code: Int?): String {
    return when (code) {
        0, 1 -> "晴天"
        2, 3 -> "多云"
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
            Text(text = title, color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActivityRecommendationCard(recommendation: ActivityRecommendation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE)),
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