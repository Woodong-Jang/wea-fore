package com.example.weatherol.data.remote.api

import com.example.weatherol.data.remote.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // 👇 这是【完整天气接口】包含：当前 + 小时预报 + 7天预报
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") currentFields: String = "temperature_2m,weather_code,relative_humidity_2m",
        @Query("hourly") hourlyFields: String = "temperature_2m,weather_code",
        @Query("daily") dailyFields: String = "weather_code,temperature_2m_max,temperature_2m_min",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse

    // 城市搜索接口（不变）
    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun geocodeCity(
        @Query("name") cityName: String,
        @Query("count") count: Int = 1,
        @Query("language") language: String = "zh",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}

// 城市搜索响应（不变）
data class GeocodingResponse(
    val results: List<GeocodingResult>?
)

data class GeocodingResult(
    val name: String,
    val latitude: Double,
    val longitude: Double
)