package com.example.weatherol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.weatherol.ui.city.CityScreen
import com.example.weatherol.ui.forecast.ForecastScreen
import com.example.weatherol.ui.home.HomeScreen
import com.example.weatherol.ui.setting.SettingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {  //页面总控制器
    var selectedIndex by remember { mutableStateOf(0) } //记忆页面选择

    val items = listOf(
        "首页" to Icons.Default.Home,
        "预报" to Icons.Default.List,
        "城市" to Icons.Default.Place,
        "设置" to Icons.Default.Settings
    )

    var selectedLat by remember { mutableStateOf(39.9042) }//城市经纬度寄存
    var selectedLon by remember { mutableStateOf(116.4074) }
    //底部导航栏外观
    Scaffold(//标准布局模板
        bottomBar = {//底部栏
            NavigationBar {//生成每一个导航项
                items.forEachIndexed { index, item ->//对列表里的每一项，都执行一次大括号里的代码//之前定义的列表
                    NavigationBarItem(//单个导航
                        icon = { Icon(item.second, null) },//图表
                        label = { Text(item.first) },//文字
                        selected = selectedIndex == index,//是否选中
                        onClick = { selectedIndex = index },//点击事件
                        colors = NavigationBarItemDefaults.colors(//修改全局颜色
                            selectedIconColor = AppState.themeColor.value,
                            selectedTextColor = AppState.themeColor.value
                        )
                    )
                }
            }
        }
    ) { padding ->//防遮挡内边距
        Box(modifier = Modifier.padding(padding)) {//上padding
            when (selectedIndex) {
                0 -> HomeScreen(selectedLat, selectedLon)// 1. 首页
                1 -> ForecastScreen(selectedLat, selectedLon)  // 2. 预报
                2 -> CityScreen { _, lat, lon ->               // 3. 城市
                    selectedLat = lat
                    selectedLon = lon
                    selectedIndex = 0                          // 选完城市 → 自动跳回首页
                }
                3 -> SettingScreen()                           // 4. 设置
            }
        }
    }
}