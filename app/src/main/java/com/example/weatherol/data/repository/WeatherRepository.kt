package com.example.weatherol.data.repository

import android.util.Log
import com.example.weatherol.data.common.AppConstants
import com.example.weatherol.data.common.DataResult
import com.example.weatherol.data.remote.NetworkModule
import com.example.weatherol.data.remote.model.CityGeo
import com.example.weatherol.data.remote.model.WeatherResponse

class WeatherRepository {

    suspend fun getCityGeoByName(cityName: String): DataResult<CityGeo> {
        return runCatching {
            Log.d("WeatherLog", "开始请求城市定位：$cityName")
            val response = NetworkModule.weatherApiService.geocodeCity(cityName = cityName)
            Log.d("WeatherLog", "城市接口请求成功")

            val firstResult = response.results?.firstOrNull()
                ?: return DataResult.Error("City not found: $cityName")

            DataResult.Success(
                CityGeo(
                    cityName = firstResult.name,
                    latitude = firstResult.latitude,
                    longitude = firstResult.longitude
                )
            )
        }.getOrElse {
            Log.e("WeatherLog", "城市接口请求失败：${it.message}", it)
            DataResult.Error(it.message ?: "Unknown network error", it)
        }
    }

    suspend fun fetchWeather(
        latitude: Double,
        longitude: Double
    ): DataResult<WeatherResponse> {
        return runCatching {
            Log.d("WeatherLog", "开始请求天气 经纬度：$latitude , $longitude")

            val res = NetworkModule.weatherApiService.getWeather(
                latitude = latitude,
                longitude = longitude,
                currentFields = AppConstants.WeatherApi.DEFAULT_CURRENT_FIELDS,
                hourlyFields = AppConstants.WeatherApi.DEFAULT_HOURLY_FIELDS,
                dailyFields = "weather_code,temperature_2m_max,temperature_2m_min",
                timezone = AppConstants.WeatherApi.DEFAULT_TIMEZONE
            )

            Log.d("WeatherLog", "天气接口请求成功，返回数据不为空：${res != null}")
            DataResult.Success(res)
        }.fold(
            onSuccess = { it },
            onFailure = {
                Log.e("WeatherLog", "天气接口请求失败：${it.message}", it)
                DataResult.Error(it.message ?: "Unknown network error", it)
            }
        )
    }
}