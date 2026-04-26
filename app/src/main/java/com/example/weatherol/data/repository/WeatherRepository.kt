package com.example.weatherol.data.repository

import com.example.weatherol.data.common.AppConstants
import com.example.weatherol.data.common.DataResult
import com.example.weatherol.data.remote.NetworkModule
import com.example.weatherol.data.remote.model.WeatherResponse

class WeatherRepository {

    suspend fun fetchWeather(
        latitude: Double,
        longitude: Double
    ): DataResult<WeatherResponse> {
        return runCatching {
            NetworkModule.weatherApiService.getWeather(
                latitude = latitude,
                longitude = longitude,
                currentFields = AppConstants.WeatherApi.DEFAULT_CURRENT_FIELDS,
                hourlyFields = AppConstants.WeatherApi.DEFAULT_HOURLY_FIELDS,
                timezone = AppConstants.WeatherApi.DEFAULT_TIMEZONE
            )
        }.fold(
            onSuccess = { DataResult.Success(it) },
            onFailure = { DataResult.Error(it.message ?: "Unknown network error", it) }
        )
    }
}
