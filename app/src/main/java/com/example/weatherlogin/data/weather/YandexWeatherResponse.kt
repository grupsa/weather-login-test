package com.example.weatherlogin.data.weather

data class YandexWeatherResponse(
    val now: Int,
    val fact: ActualalWeatherData
)

data class ActualalWeatherData(
    val temp: Int,
    val condition: WeatherCondition
)