package com.example.weatherlogin.data.weather

import com.example.weatherlogin.AppConfig
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface YandexWeatherProtocol {

    @Headers("X-Yandex-API-Key: " + AppConfig.YANDEX_WEATHER_API_KEY)
    @GET("/v1/informers")
    fun requestWeatherForCoords(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String
    ): Single<YandexWeatherResponse>
}