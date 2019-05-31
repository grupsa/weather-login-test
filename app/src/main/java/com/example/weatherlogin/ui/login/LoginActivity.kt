package com.example.weatherlogin.ui.login

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.example.weatherlogin.R
import com.example.weatherlogin.data.weather.ActualalWeatherData
import com.example.weatherlogin.data.weather.WeatherCondition
import com.example.weatherlogin.data.weather.YandexWeatherResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private val compositeDisposable = CompositeDisposable()

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val username = findViewById<TextInputEditText>(R.id.username)
        val password = findViewById<TextInputEditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }

            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }

        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                //updateUiWithUser(loginResult.success)
                updateUiWithWeather(loginResult.success, YandexWeatherResponse(0, ActualalWeatherData(10, WeatherCondition.cloudy)))
                // setupWeatherObserver().let { compositeDisposable.add(it) }
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            // Handle input text in passwrod feeld
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            // Handle pressing Done keyboard button
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }
        }

        // Handle tap login button
        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.login(username.text.toString(), password.text.toString())
        }
    }

    fun setupWeatherObserver(): Disposable {
        return loginViewModel.getWeather()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { weather: YandexWeatherResponse? ->
                    weather?.let { Log.d("debe", it.fact.temp.toString()) }
                    //resolveRequestEndUI()
                    //navigateToDetailsActivity(weatherResponse)
                    //do some stuff
                },
                { throwable: Throwable? ->
                    Log.e("debe", throwable?.message)
                    //resolveRequestEndUI()
                    //handle error as you want
                }
            )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu);
        return true
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateUiWithWeather(user: LoggedInUserView, weather: YandexWeatherResponse) {
        val name = user.displayName
        val temp = weather.fact.temp.toString()
        val condition = weather.fact.condition.description
        // TODO : initiate successful logged in experience
//        Toast.makeText(
//            applicationContext,
//            String.format(getString(R.string.welcome_weather), name, condition, temp),
//            Toast.LENGTH_LONG
//        ).show()
        Snackbar.make(
            window.decorView,
            String.format(getString(R.string.welcome_weather), name, condition, temp),
            Snackbar.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
