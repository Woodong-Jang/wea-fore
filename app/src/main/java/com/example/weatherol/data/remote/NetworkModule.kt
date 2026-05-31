package com.example.weatherol.data.remote

import com.example.weatherol.data.remote.api.WeatherApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://api.open-meteo.com/"// 1. 存服务器地址

    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()// 2. 用 Retrofit 配置好 JSON 解析、地址等
            .baseUrl(BASE_URL)// 3. 设置 API 的基础地址
            .addConverterFactory(GsonConverterFactory.create())// 4.配置解析器,告诉Retrofit，服务器返回的JSON，我要用Gson自动帮我转成 Kotlin对象
            .build()//生成一个 Retrofit 实例
            .create(WeatherApiService::class.java)//Retrofit会根据WeatherApiService接口生成具体的实现代码。
    }
}