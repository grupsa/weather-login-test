package com.example.weatherlogin.data.login

import android.util.Patterns
import com.example.weatherlogin.R
import com.example.weatherlogin.data.model.LoggedInUser
import com.example.weatherlogin.data.model.LoginFormState
import com.example.weatherlogin.data.model.Result
import javax.inject.Inject

/**
 * Class that mast requests authentication and user information from the remote data source
 */
class LoginRepository @Inject constructor(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    /**
     * Validation of user credentials
     */
    fun validateLoginPass(username: String, password: String): LoginFormState {
        val state = LoginFormState()

        if ( ! Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            state.isDataValid = false
            state.usernameError = R.string.invalid_username
        }

        if (password.length < 5) {
            state.isDataValid = false
            state.passwordError = R.string.invalid_password_size
        } else if ( ! password.matches(".*\\d+.*".toRegex())) {
            state.isDataValid = false
            state.passwordError = R.string.invalid_password_numbers
        } else if ( ! password.matches(".*[A-ZА-Я].*".toRegex())) {
            state.isDataValid = false
            state.passwordError = R.string.invalid_password_uppercase
        } else if ( ! password.matches(".*[a-zа-я].*".toRegex())) {
            state.isDataValid = false
            state.passwordError = R.string.invalid_password_lowercase
        }

        return state
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }
}
