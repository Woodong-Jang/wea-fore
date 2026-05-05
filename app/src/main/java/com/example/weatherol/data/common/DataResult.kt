package com.example.weatherol.data.common
//网络请求结果包装
sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : DataResult<Nothing>()
}
