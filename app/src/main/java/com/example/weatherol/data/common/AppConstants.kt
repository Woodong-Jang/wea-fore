package com.example.weatherol.data.common
//存全局常量
import android.Manifest

object AppConstants {

    object Network {
        const val WEATHER_BASE_URL = "https://api.open-meteo.com/"
        const val CONNECT_TIMEOUT_SECONDS = 15L
        const val READ_TIMEOUT_SECONDS = 20L
        const val WRITE_TIMEOUT_SECONDS = 20L
    }

    object WeatherApi {
        const val DEFAULT_CURRENT_FIELDS = "temperature_2m,relative_humidity_2m,weather_code"
        const val DEFAULT_HOURLY_FIELDS = "temperature_2m,weather_code"
        const val DEFAULT_TIMEZONE = "auto"
    }

    object Permission {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    object TimePattern {
        const val DATE = "yyyy-MM-dd"
        const val HOUR_MINUTE = "HH:mm"
        const val DATE_TIME = "yyyy-MM-dd HH:mm"
    }
}
