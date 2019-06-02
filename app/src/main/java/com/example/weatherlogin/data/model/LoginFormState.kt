package com.example.weatherlogin.data.model

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    var usernameError: Int? = null,
    var passwordError: Int? = null,
    var isDataValid: Boolean = true
)
