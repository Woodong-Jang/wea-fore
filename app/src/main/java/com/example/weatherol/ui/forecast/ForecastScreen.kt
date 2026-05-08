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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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

data class HourlyItem(val time: String, val temp: String)  // 每小时天气预报的数据模型：时间 + 温度
data class DailyItem(val day: String, val max: String, val min: String)  // 每日天气预报的数据模型：星期几 + 最高温 + 最低温

// 预报页面主组件（默认坐标是北京）
@Composable
fun ForecastScreen(lat: Double = 39.9042, lon: Double = 116.4074) {
    val repo = remember { WeatherRepository() }  // 记住仓库实例，避免重复创建
    val state = remember { androidx.compose.runtime.mutableStateOf<DataResult<WeatherResponse>?>(null) }  // 定义页面状态：存储网络请求结果（成功/失败/加载中）

    // 协程：经纬度变化时重新请求天气数据
    LaunchedEffect(lat, lon) {
        state.value = repo.fetchWeather(lat, lon)
    }

    // 页面根布局：垂直列 + 全屏 + 背景色 + 可滚动 + 内边距
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // 页面大标题
        Text("未来预报", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        // 根据网络请求状态显示不同UI
        when (val current = state.value) {
            // 请求成功 → 展示数据
            is DataResult.Success -> {
                val data = current.data  //完整数据
                val hourly = data.hourly  //小时数居
                val daily = data.daily  //每日数据

                // 构造8条未来小时预报数据
                val hList = mutableListOf<HourlyItem>()
                repeat(8) { i ->
                    val t = hourly?.time?.getOrNull(i)?.takeLast(5) ?: ""  // 截取时间后5位（例：14:30）
                    val temp = hourly?.temperature2m?.getOrNull(i) ?: 0.0  // 获取温度值
                    // 根据全局设置显示℃或℉
                    val s = if (AppState.isCelsius.value) "${temp.toInt()}℃" else "${(temp*1.8+32).toInt()}℉"
                    hList.add(HourlyItem(t, s))
                }

                // 24小时预报标题
                Text("24小时预报", fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))

                // 横向滚动列表：展示未来8小时天气
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 遍历小时数据列表
                    items(hList) {
                        // 每小时的卡片
                        Card(
                            modifier = Modifier.width(80.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            // 卡片内部：时间 + 温度
                            Column(
                                Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(it.time, fontSize = 13.sp, color = Color.Gray)
                                Spacer(Modifier.height(8.dp))
                                Text(it.temp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))
                // 7天预报标题
                Text("7天预报", fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))

                // 星期名称
                val days = listOf("今天","明天","周三","周四","周五","周六","周日")
                // 循环显示7天预报
                for (i in days.indices) {
                    // 获取当天最高/最低温
                    val max = daily?.temperature2mMax?.getOrNull(i) ?: 0.0
                    val min = daily?.temperature2mMin?.getOrNull(i) ?: 0.0
                    // 温度单位转换
                    val maxStr = if (AppState.isCelsius.value) "${max.toInt()}℃" else "${(max*1.8+32).toInt()}℉"
                    val minStr = if (AppState.isCelsius.value) "${min.toInt()}℃" else "${(min*1.8+32).toInt()}℉"

                    // 每日天气卡片
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(Color.White)
                    ) {
                        // 卡片内：星期 + 温度范围
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(days[i], fontWeight = FontWeight.Medium)
                            Text("$maxStr / $minStr")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            // 请求失败 → 显示错误信息
            is DataResult.Error -> Text("错误: ${current.message}", color = Color.Red)
            // 加载中 → 显示转圈进度条
            else -> CircularProgressIndicator(color = AppState.themeColor.value)
        }
    }
}