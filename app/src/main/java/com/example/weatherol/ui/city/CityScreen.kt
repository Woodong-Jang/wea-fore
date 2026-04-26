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

enum class WeatherType {
    SUNNY, CLOUDY, RAINY, SNOWY
}

data class City(
    val id: Int,
    val name: String,
    val temperature: String = "23℃",
    val weatherType: WeatherType = WeatherType.CLOUDY,
    val weatherText: String = "多云"
)

@Composable
fun CityScreen() {
    val cityList = remember {
        mutableStateListOf(
            City(1, "北京"),
            City(2, "上海"),
            City(3, "广州"),
            City(4, "深圳"),
            City(5, "杭州"),
            City(6, "成都"),
            City(7, "重庆"),
            City(8, "武汉"),
            City(9, "西安"),
            City(10, "南京"),
            City(11, "天津"),
            City(12, "苏州"),
            City(13, "乌鲁木齐")
        )
    }

    var searchText by remember { mutableStateOf("") }

    val filteredList = remember(searchText, cityList) {
        if (searchText.isBlank()) cityList
        else cityList.filter { it.name.contains(searchText, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))))
            .padding(20.dp)
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
                val newCity = City(
                    id = cityList.size + 1,
                    name = "新城市${cityList.size + 1}"
                )
                cityList.add(newCity)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("添加城市", fontSize = 16.sp)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "已添加城市 (${filteredList.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF334155)
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(filteredList, key = { it.id }) { city ->
                AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                    WeatherCityCard(city) {
                        cityList.remove(city)
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherCityCard(city: City, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { },
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
            WeatherIcon(city.weatherType)
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
                    city.weatherText,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }

            Text(
                city.temperature,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
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