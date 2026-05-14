# wea-fore

`wea-fore` 是一个基于 **Android + Kotlin + Jetpack Compose** 的天气应用示例项目，使用 **Open-Meteo API** 获取实时天气数据并在移动端展示。

## 项目亮点

- 使用 Jetpack Compose 构建界面（首页/预报/城市/设置）
- 通过 Retrofit + OkHttp 请求 Open-Meteo 天气接口
- 数据层包含统一常量、请求结果封装与仓库模式
- 已声明网络与定位相关权限，便于后续接入定位城市天气

## 技术栈

- Kotlin
- Jetpack Compose + Material 3
- Retrofit 2 + Gson Converter
- OkHttp 4 + Logging Interceptor
- Kotlin Coroutines
- Google Play Services Location

## 目录结构

```text
app/src/main/java/com/example/weatherol
├─ data
│  ├─ common
│  │  ├─ AppConstants.kt
│  │  └─ DataResult.kt
│  ├─ remote
│  │  ├─ NetworkModule.kt
│  │  ├─ api/WeatherApiService.kt
│  │  └─ model/WeatherResponse.kt
│  └─ repository/WeatherRepository.kt
├─ ui
│  ├─ home/HomeScreen.kt
│  ├─ forecast/ForecastScreen.kt
│  ├─ city/CityScreen.kt
│  └─ settings/SettingsScreen.kt
└─ MainActivity.kt
```

## 快速开始

### 1. 环境要求

- Android Studio（建议使用最新稳定版）
- Android SDK（`compileSdk = 36`）
- JDK 17+

### 2. 克隆并打开项目

```bash
git clone <your-repo-url>
cd weatherol1
```

在 Android Studio 中打开项目根目录。

### 3. 构建与运行

命令行构建：

```bash
./gradlew :app:assembleDebug
```

或在 Android Studio 中直接运行 `app` 模块到模拟器/真机。

## 数据来源

- Weather API: [Open-Meteo Forecast API](https://open-meteo.com/)
- 默认接口基地址：`https://api.open-meteo.com/`

## 已实现能力（当前版本）

- 基础多页面导航（Bottom Navigation）
- 天气网络请求与响应解析
- 首页展示实时天气核心信息（温度、湿度、天气状态等）

## 后续可改进方向

- 将页面中的示例城市/静态字段替换为真实城市与完整接口字段
- 增加 ViewModel + 状态管理（如 `StateFlow`）
- 引入依赖注入（如 Hilt）提升可测试性与可维护性
- 增加单元测试与 UI 测试
