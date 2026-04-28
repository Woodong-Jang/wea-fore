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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherol.ui.city.CityScreen
import com.example.weatherol.ui.forecast.ForecastScreen
import com.example.weatherol.ui.home.HomeScreen
import com.example.weatherol.ui.settings.AboutScreen
import com.example.weatherol.ui.settings.HelpScreen
import com.example.weatherol.ui.settings.SettingsScreen
import com.example.weatherol.ui.theme.WeatherolTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherolTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // 底部导航栏的选中状态
    var selectedIndex by remember { mutableIntStateOf(0) }

    // 监听导航变化，更新底部导航栏的选中状态
    androidx.compose.runtime.LaunchedEffect(navController) {
        // 当返回设置页面时，更新选中状态
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navItems = listOf(
                    NavItem("首页", Icons.Default.Home, "home"),
                    NavItem("预报", Icons.Default.LocationOn, "forecast"),
                    NavItem("城市", Icons.Default.Add, "city"),
                    NavItem("设置", Icons.Default.Settings, "settings")
                )

                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.label)
                        },
                        label = {
                            Text(item.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    selectedIndex = 0
                    HomeScreen()
                }
                composable("forecast") {
                    selectedIndex = 1
                    ForecastScreen()
                }
                composable("city") {
                    selectedIndex = 2
                    CityScreen()
                }
                composable("settings") {
                    selectedIndex = 3
                    SettingsScreen(navController)
                }
                composable("about") {
                    AboutScreen(navController)
                }
                composable("help") {
                    HelpScreen(navController)
                }
            }
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)