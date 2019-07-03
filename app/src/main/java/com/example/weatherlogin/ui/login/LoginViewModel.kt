package com.example.weatherlogin.ui.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.weatherlogin.data.login.LoginRepository
import com.example.weatherlogin.data.model.Result

import com.example.weatherlogin.R
import com.example.weatherlogin.data.model.LoginFormState
import com.example.weatherlogin.data.model.LoginResult
import com.example.weatherlogin.data.weather.WeatherRepository
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository,
                                         private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun getWeather() = weatherRepository.getWeather()

    fun loginDataChanged(username: String, password: String) {
        _loginForm.value = loginRepository.validateLoginPass(username, password)
    }
}
