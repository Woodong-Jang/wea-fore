package com.example.weatherol.ui.city

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherol.data.common.DataResult
import com.example.weatherol.data.remote.model.CityGeo
import com.example.weatherol.data.repository.WeatherRepository
import kotlinx.coroutines.launch

enum class WeatherType { SUNNY, CLOUDY, RAINY, SNOWY }

data class LocalCity(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double
)

@Composable
fun CityScreen(
    onCitySelected: (String, Double, Double) -> Unit
) {
    val repo = remember { WeatherRepository() }
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    val cityList = remember {
        mutableStateListOf(
            LocalCity(1, "北京", 39.9042, 116.4074),
            LocalCity(2, "上海", 31.2304, 121.4737),
            LocalCity(3, "广州", 23.1291, 113.2644),
            LocalCity(4, "深圳", 22.5431, 114.0579),
            LocalCity(5, "杭州", 30.2741, 120.1551),
            LocalCity(6, "成都", 30.5723, 104.0665),
            LocalCity(7, "重庆", 29.5630, 106.5516),
            LocalCity(8, "武汉", 30.5928, 114.3055),
            LocalCity(9, "西安", 33.4219, 108.9398),
            LocalCity(10, "南京", 32.0603, 118.7969),
            LocalCity(11, "天津", 39.0842, 117.2010),
            LocalCity(12, "苏州", 31.2987, 120.5843),
            LocalCity(13, "乌鲁木齐", 43.8256, 87.6169)
        )
    }

    val filteredList = remember(searchText, cityList) {
        if (searchText.isBlank()) cityList
        else cityList.filter { it.name.contains(searchText, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "城市管理",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("搜索城市") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (searchText.isNotBlank()) {
                    scope.launch {
                        isLoading = true
                        val result = repo.getCityGeoByName(searchText)
                        isLoading = false
                        when (result) {
                            is DataResult.Success -> {
                                val geo = result.data
                                if (!cityList.any { it.name == geo.cityName }) {
                                    cityList.add(
                                        LocalCity(
                                            id = cityList.size + 1,
                                            name = geo.cityName,
                                            latitude = geo.latitude,
                                            longitude = geo.longitude
                                        )
                                    )
                                }
                                errorMsg = ""
                            }
                            is DataResult.Error -> {
                                errorMsg = result.message ?: "添加失败"
                            }
                            else -> Unit
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("添加搜索城市", fontSize = 16.sp)
            }
        }

        if (errorMsg.isNotEmpty()) {
            Text(
                text = errorMsg,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "已添加城市 (${filteredList.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF334155)
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList, key = { it.id }) { city ->
                AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                    WeatherCityCard(
                        city = city,
                        onDelete = { cityList.remove(city) },
                        onCitySelected = onCitySelected
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherCityCard(
    city: LocalCity,
    onDelete: () -> Unit,
    onCitySelected: (String, Double, Double) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable {
                onCitySelected(city.name, city.latitude, city.longitude)
            },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherIcon(WeatherType.CLOUDY)
            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    city.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "已同步真实坐标",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }

            Text(
                "📍",
                fontSize = 24.sp,
                color = Color(0xFF0284C7)
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFEE2E2), CircleShape)
            ) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444))
            }
        }
    }
}

@Composable
fun WeatherIcon(type: WeatherType) {
    val (bgColor, icon) = when (type) {
        WeatherType.SUNNY -> Color(0xFFFFF9C3) to "☀️"
        WeatherType.CLOUDY -> Color(0xFFE2E8F0) to "☁️"
        WeatherType.RAINY -> Color(0xFFBAE6FD) to "🌧️"
        WeatherType.SNOWY -> Color(0xFFF0F9FF) to "❄️"
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(text = icon, fontSize = 28.sp)
    }
}