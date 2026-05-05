package com.example.weatherol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.weatherol.ui.city.CityScreen
import com.example.weatherol.ui.forecast.ForecastScreen
import com.example.weatherol.ui.home.HomeScreen
import com.example.weatherol.ui.settings.SettingsScreen
import com.example.weatherol.ui.theme.WeatherolTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherolTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val items = listOf(
        NavItem("首页", Icons.Default.Home),
        NavItem("预报", Icons.Default.LocationOn),
        NavItem("城市", Icons.Default.Add),
        NavItem("设置", Icons.Default.Settings)
    )

    var selected by remember { mutableIntStateOf(0) }
    // 👇 这两行就是你缺失的城市状态变量
    var selectedLat by remember { mutableStateOf(39.9042) }
    var selectedLon by remember { mutableStateOf(116.4074) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { i, it ->
                    NavigationBarItem(
                        selected = selected == i,
                        onClick = { selected = i },
                        icon = { Icon(it.icon, it.label) },
                        label = { Text(it.label) }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            when (selected) {
                0 -> HomeScreen(latitude = selectedLat, longitude = selectedLon)
                1 -> ForecastScreen(latitude = selectedLat, longitude = selectedLon)
                2 -> CityScreen { _, lat, lon ->
                    selectedLat = lat
                    selectedLon = lon
                    selected = 0 // 选完城市自动切回首页
                }
                3 -> SettingsScreen()
            }
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector)