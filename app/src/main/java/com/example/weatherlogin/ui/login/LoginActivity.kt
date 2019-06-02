package com.example.weatherlogin.ui.login

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.weatherlogin.R
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

        // Setup menu in actionbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val username = findViewById<TextInputEditText>(R.id.username)
        val password = findViewById<TextInputEditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        // Formstate observation via livedata
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer { state ->
            val loginState = state ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            loginState.usernameError?.let { username.error = getString(it) }
            loginState.passwordError?.let { password.error = getString(it) }
        })

        // Login result observation via livedata
        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                // Request and observation weather via RX
                setupWeatherObserver(loginResult.success.displayName).let { compositeDisposable.add(it) }
            }
        })

        // Handle input text in login feeld
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
            hideSoftInputKeybord()
            loading.visibility = View.VISIBLE
            loginViewModel.login(username.text.toString(), password.text.toString())
        }
    }

    /**
     * Request and observation weathher via RX
     */
    private fun setupWeatherObserver(username: String): Disposable {
        return loginViewModel.getWeather()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { weather: YandexWeatherResponse? ->
                    weather?.let { updateUiWithWeather(username, it) }
                },
                { throwable: Throwable? ->
                    Log.e("Weather request", throwable?.message)
                    showError()
                }
            )
    }

    /**
     * Output current weather in snsckbar
     */
    private fun updateUiWithWeather(user: String, weather: YandexWeatherResponse) {
        val name = user
        val temp = weather.fact.temp.toString()
        val condition = weather.fact.condition.description

        Snackbar.make(
            window.decorView,
            String.format(getString(R.string.welcome_weather), name, condition, temp),
            Snackbar.LENGTH_LONG).setDuration(6000).show()
    }

    /**
     * Output the error message in snsckbar
     */
    private fun showError() {
        Snackbar.make(
            window.decorView,
            getString(R.string.error_weather),
            Snackbar.LENGTH_LONG).setDuration(6000).show()
    }

    /**
     * Hide skreen keybord
     */
    private fun hideSoftInputKeybord() {
        // Check if no view has focus:
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu);
        return true
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
