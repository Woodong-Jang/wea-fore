package com.example.weatherol.ui.city

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherol.AppState
import com.example.weatherol.City
import com.example.weatherol.data.common.DataResult
import com.example.weatherol.data.repository.WeatherRepository
import kotlinx.coroutines.launch

@Composable
fun CityScreen() {
    val repo = remember { WeatherRepository() }
    val coroutineScope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // ✅ 直接使用全局列表（切换页面不会重置）北京
    val cityList = AppState.cityList

    // 搜索过滤
    val filteredList = remember(searchText, cityList) {
        if (searchText.isBlank()) cityList
        else cityList.filter { it.name.contains(searchText, ignoreCase = true) }
    }

    // 添加网络城市
    fun addCityByName(name: String) {
        if (name.isBlank()) return
        coroutineScope.launch {
            isLoading = true
            errorMsg = null
            when (val res = repo.getCityGeoByName(name)) {
                is DataResult.Success -> {
                    val geo = res.data
                    val exists = cityList.any { it.name == geo.cityName }
                    if (!exists) {
                        val newId = (cityList.maxOfOrNull { it.id } ?: 0) + 1
                        val newCity = City(
                            id = newId,
                            name = geo.cityName,
                            latitude = geo.latitude,
                            longitude = geo.longitude
                        )
                        cityList.add(newCity)
                    }

                    AppState.currentLat.value = geo.latitude
                    AppState.currentLon.value = geo.longitude
                    AppState.currentCityName.value = geo.cityName
                }
                is DataResult.Error -> {
                    errorMsg = res.message
                }
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "城市管理",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("输入城市名添加") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = { addCityByName(searchText) },
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(Icons.Default.Add, null)
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }

        errorMsg?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "已添加城市 (${filteredList.size})",
            fontSize = 18.sp
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList, key = { it.id }) { city ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CityCard(city) {
                        cityList.remove(city)
                    }
                }
            }
        }
    }
}

@Composable
fun CityCard(
    city: City,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                AppState.currentLat.value = city.latitude
                AppState.currentLon.value = city.longitude
                AppState.currentCityName.value = city.name
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                city.name,
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text(
                city.weatherText,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.width(12.dp))

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}