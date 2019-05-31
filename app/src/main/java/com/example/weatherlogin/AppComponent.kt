package com.example.weatherlogin

import com.example.weatherlogin.data.LoginRepository
import com.example.weatherlogin.data.weather.WeatherRepositoryModule
import com.example.weatherlogin.ui.login.LoginViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(WeatherRepositoryModule::class))
@Singleton
interface AppComponent {
    fun getLoginViewModel() : LoginViewModel
}