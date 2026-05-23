package com.example.weatherol.ui.forecast

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherol.AppState

// 带温度字段的模拟数据类
data class HourlyForecast(
    val time: String,
    val tempC: Double,
    val iconRes: Int
)

data class DailyForecast(
    val day: String,
    val highTempC: Double,
    val lowTempC: Double,
    val iconRes: Int,
    val weatherDesc: String
)

@Composable
fun ForecastScreen() {
    val isCelsius = AppState.isCelsius.value

    // 模拟数据
    val hourlyList = listOf(
        HourlyForecast("现在", 26.0, android.R.drawable.ic_menu_compass),
        HourlyForecast("14:00", 27.0, android.R.drawable.ic_menu_compass),
        HourlyForecast("15:00", 28.0, android.R.drawable.ic_menu_compass),
        HourlyForecast("16:00", 27.0, android.R.drawable.ic_menu_compass),
        HourlyForecast("17:00", 25.0, android.R.drawable.ic_menu_compass),
        HourlyForecast("18:00", 23.0, android.R.drawable.ic_menu_compass),
        HourlyForecast("19:00", 22.0, android.R.drawable.ic_menu_compass),
        HourlyForecast("20:00", 21.0, android.R.drawable.ic_menu_compass)
    )

    val dailyList = listOf(
        DailyForecast("今天", 28.0, 22.0, android.R.drawable.ic_menu_compass, "晴"),
        DailyForecast("周二", 27.0, 21.0, android.R.drawable.ic_menu_compass, "多云"),
        DailyForecast("周三", 25.0, 20.0, android.R.drawable.ic_menu_compass, "小雨"),
        DailyForecast("周四", 24.0, 19.0, android.R.drawable.ic_menu_compass, "阴"),
        DailyForecast("周五", 26.0, 20.0, android.R.drawable.ic_menu_compass, "晴"),
        DailyForecast("周六", 27.0, 21.0, android.R.drawable.ic_menu_compass, "晴转多云"),
        DailyForecast("周日", 28.0, 22.0, android.R.drawable.ic_menu_compass, "晴")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "未来预报",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A202C),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        HourlyForecastSection(hourlyList, isCelsius)
        Spacer(modifier = Modifier.height(24.dp))
        DailyForecastSection(dailyList, isCelsius)
    }
}

@Composable
fun HourlyForecastSection(hourlyList: List<HourlyForecast>, isCelsius: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "24小时预报",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3748),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(hourlyList) { forecast ->
                    val tempF = forecast.tempC * 9 / 5 + 32
                    val displayTemp = if (isCelsius) "%.1f°C" else "%.1f°F"
                    val finalText = displayTemp.format(if (isCelsius) forecast.tempC else tempF)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(text = forecast.time, fontSize = 14.sp, color = Color(0xFF718096))
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(id = forecast.iconRes),
                            contentDescription = "天气图标",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = finalText, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2D3748))
                    }
                }
            }
        }
    }
}

@Composable
fun DailyForecastSection(dailyList: List<DailyForecast>, isCelsius: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "7天预报",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3748),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            dailyList.forEachIndexed { index, forecast ->
                val highF = forecast.highTempC * 9 / 5 + 32
                val lowF = forecast.lowTempC * 9 / 5 + 32

                val high = if (isCelsius) "%.1f°C" else "%.1f°F"
                val low = if (isCelsius) "%.1f°C" else "%.1f°F"

                val highText = high.format(if (isCelsius) forecast.highTempC else highF)
                val lowText = low.format(if (isCelsius) forecast.lowTempC else lowF)

                DailyForecastItem(forecast.day, forecast.weatherDesc, highText, lowText)

                if (index != dailyList.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE2E8F0))
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(day: String, desc: String, high: String, low: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = day, fontSize = 14.sp, color = Color(0xFF2D3748), modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(android.R.drawable.ic_menu_compass),
                contentDescription = "天气图标",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = desc, fontSize = 14.sp, color = Color(0xFF718096))
        }
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
            Text(text = high, fontSize = 14.sp, color = Color(0xFF2D3748), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = low, fontSize = 14.sp, color = Color(0xFF718096))
        }
    }
}