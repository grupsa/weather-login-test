package com.example.weatherlogin

/**
 * CLASS FOR STORE STATIC CONFIGURATION PARAMETERS
 */
class AppConfig {
    companion object {
        // Yandex weather API params:
        const val YANDEX_WEATHER_API_URL = "https://api.weather.yandex.ru/"
        const val YANDEX_WEATHER_API_KEY = "Please insert your api-key hear"
        // Krasnodar coords:
        const val TARGET_LATITUDE = "45.046938"
        const val TARGET_LODGITUDE = "38.993509"
    }
}