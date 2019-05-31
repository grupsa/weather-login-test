package com.example.weatherlogin.data.weather

import com.example.weatherlogin.AppConfig
import io.reactivex.Single
import javax.inject.Inject

class YandexWeatherRepository @Inject constructor(private  val protocol: YandexWeatherProtocol) : WeatherRepository {
    override fun getWeather(): Single<YandexWeatherResponse> =
        protocol.requestWeatherForCoords(AppConfig.TARGET_LATITUDE, AppConfig.TARGET_LODGITUDE).retry(1)

}