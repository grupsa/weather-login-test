package com.example.weatherlogin

import android.app.Application
import com.example.weatherlogin.data.weather.WeatherRepositoryModule

class WeatherLoginApp : Application() {

    companion object {
        lateinit var diComponent : AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        diComponent = DaggerAppComponent.builder()
            .weatherRepositoryModule(WeatherRepositoryModule()).build()

    }
}