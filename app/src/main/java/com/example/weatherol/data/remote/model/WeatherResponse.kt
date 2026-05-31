package com.example.weatherol.data.remote.model

import com.google.gson.annotations.SerializedName

// 顶层天气响应
data class WeatherResponse(
    @SerializedName("current") val current: Current?,
    @SerializedName("hourly") val hourly: Hourly?,
    @SerializedName("daily") val daily: Daily?
)

// 当前天气
data class Current(
    @SerializedName("temperature_2m") val temperature2m: Double?,
    @SerializedName("weather_code") val weatherCode: Int?,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: Int?
)

// 24小时预报
data class Hourly(
    @SerializedName("time") val time: List<String>?,
    @SerializedName("temperature_2m") val temperature2m: List<Double>?,
    @SerializedName("weather_code") val weatherCode: List<Int>?
)

// 7天预报
data class Daily(
    @SerializedName("time") val time: List<String>?,
    @SerializedName("weather_code") val weatherCode: List<Int>?,
    @SerializedName("temperature_2m_max") val temperature2mMax: List<Double>?,
    @SerializedName("temperature_2m_min") val temperature2mMin: List<Double>?
)

// 城市搜索
data class GeocodingResponse(
    @SerializedName("results") val results: List<GeocodingResult>?
)

data class GeocodingResult(
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)