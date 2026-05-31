package com.example.weatherol.data.repository
//负责找网络拿数据、处理成功失败、向ui层返回干净的数据。
import android.util.Log
import com.example.weatherol.data.common.AppConstants
import com.example.weatherol.data.common.DataResult
import com.example.weatherol.data.remote.NetworkModule
import com.example.weatherol.data.remote.model.CityGeo
import com.example.weatherol.data.remote.model.WeatherResponse

class WeatherRepository {

    suspend fun getCityGeoByName(cityName: String): DataResult<CityGeo> {//通过城市名获取经纬度
        return runCatching {
            Log.d("WeatherLog", "开始请求城市定位：$cityName")
            val response = NetworkModule.weatherApiService.geocodeCity(cityName = cityName)//调用网络接口
            Log.d("WeatherLog", "城市接口请求成功")

            val firstResult = response.results?.firstOrNull()//取第一个搜索结果
                ?: return DataResult.Error("City not found: $cityName")

            DataResult.Success(
                CityGeo(//包装成 CityGeo 对象
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

    suspend fun fetchWeather(//获取天气数据
        latitude: Double,
        longitude: Double
    ): DataResult<WeatherResponse> {
        return runCatching {
            Log.d("WeatherLog", "开始请求天气 经纬度：$latitude , $longitude")

            val res = NetworkModule.weatherApiService.getWeather(//调用天气接口
                latitude = latitude,//传入参数
                longitude = longitude,
                currentFields = AppConstants.WeatherApi.DEFAULT_CURRENT_FIELDS,
                hourlyFields = AppConstants.WeatherApi.DEFAULT_HOURLY_FIELDS,
                dailyFields = "weather_code,temperature_2m_max,temperature_2m_min",
                timezone = AppConstants.WeatherApi.DEFAULT_TIMEZONE
            )

            Log.d("WeatherLog", "天气接口请求成功，返回数据不为空：${res != null}")
            DataResult.Success(res)//WeatherResponse
        }.fold(
            onSuccess = { it },//成功返回WeatherResponse
            onFailure = {//失败返回错误信息
                Log.e("WeatherLog", "天气接口请求失败：${it.message}", it)
                DataResult.Error(it.message ?: "Unknown network error", it)
            }
        )
    }
}