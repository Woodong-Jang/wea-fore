package com.example.weatherol.ui.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherol.AppState
import com.example.weatherol.data.common.DataResult
import com.example.weatherol.data.remote.model.WeatherResponse
import com.example.weatherol.data.repository.WeatherRepository

@Composable
fun ForecastScreen() {
    val repo = remember { WeatherRepository() }
    val state = remember { mutableStateOf<DataResult<WeatherResponse>?>(null) }
    val isCelsius = AppState.isCelsius.value

    // 从 AppState 读取当前城市的经纬度
    val lat = AppState.currentLat.value
    val lon = AppState.currentLon.value

    // 监听经纬度变化，城市切换时自动刷新
    LaunchedEffect(lat, lon) {
        state.value = repo.fetchWeather(lat, lon)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "未来预报",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(20.dp))

        when (val current = state.value) {
            is DataResult.Success -> {
                val data = current.data
                val hourly = data.hourly
                val daily = data.daily

                // 24小时预报
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("24小时预报", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            items(8) { i ->
                                val time = hourly?.time?.getOrNull(i)?.takeLast(5) ?: ""
                                val temp = hourly?.temperature2m?.getOrNull(i) ?: 0.0
                                val tempStr = if (isCelsius) "${temp.toInt()}℃" else "${(temp * 1.8 + 32).toInt()}℉"

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(time, fontSize = 13.sp, color = Color.Gray)
                                    Spacer(Modifier.height(8.dp))
                                    Image(
                                        painterResource(android.R.drawable.ic_menu_compass),
                                        null,
                                        Modifier.size(32.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(tempStr, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                // 7天预报
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("7天预报", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        val days = listOf("今天", "明天", "周三", "周四", "周五", "周六", "周日")
                        days.forEachIndexed { i, day ->
                            val max = daily?.temperature2mMax?.getOrNull(i) ?: 0.0
                            val min = daily?.temperature2mMin?.getOrNull(i) ?: 0.0
                            val maxStr = if (isCelsius) "${max.toInt()}℃" else "${(max * 1.8 + 32).toInt()}℉"
                            val minStr = if (isCelsius) "${min.toInt()}℃" else "${(min * 1.8 + 32).toInt()}℉"

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                Arrangement.SpaceBetween,
                                Alignment.CenterVertically
                            ) {
                                Text(day, fontWeight = FontWeight.Medium)
                                Text("$maxStr / $minStr")
                            }
                            if (i != days.lastIndex) {
                                Divider(color = Color.LightGray, thickness = 1.dp)
                            }
                        }
                    }
                }
            }

            is DataResult.Error -> {
                Text("错误: ${current.message}", color = Color.Red)
            }

            else -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppState.themeColor.value)
                }
            }
        }
    }
}