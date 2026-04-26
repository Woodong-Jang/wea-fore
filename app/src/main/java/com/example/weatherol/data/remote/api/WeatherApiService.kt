package com.example.weatherol.data.remote.api

import com.example.weatherol.data.remote.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") currentFields: String,
        @Query("hourly") hourlyFields: String,
        @Query("timezone") timezone: String
    ): WeatherResponse
}
