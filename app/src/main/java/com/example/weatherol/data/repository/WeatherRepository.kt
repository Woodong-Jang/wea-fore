package com.example.weatherol.data.repository

import com.example.weatherol.data.common.AppConstants
import com.example.weatherol.data.common.DataResult
import com.example.weatherol.data.remote.NetworkModule
import com.example.weatherol.data.remote.model.CityGeo
import com.example.weatherol.data.remote.model.WeatherResponse

class WeatherRepository {

    suspend fun getCityGeoByName(cityName: String): DataResult<CityGeo> {
        return runCatching {
            val response = NetworkModule.weatherApiService.geocodeCity(cityName = cityName)
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
            DataResult.Error(it.message ?: "Unknown network error", it)
        }
    }

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
