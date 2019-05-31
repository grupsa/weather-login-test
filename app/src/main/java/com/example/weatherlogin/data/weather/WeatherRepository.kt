package com.example.weatherlogin.data.weather

import io.reactivex.Single

interface WeatherRepository {
    fun getWeather() : Single<YandexWeatherResponse>
}