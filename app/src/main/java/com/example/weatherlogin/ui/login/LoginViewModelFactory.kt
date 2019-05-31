package com.example.weatherlogin.ui.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.weatherlogin.WeatherLoginApp
import com.example.weatherlogin.data.LoginDataSource
import com.example.weatherlogin.data.LoginRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
//            return LoginViewModel(
//                loginRepository = LoginRepository(
//                    dataSource = LoginDataSource()
//                )
//            ) as T
            return WeatherLoginApp.diComponent.getLoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
