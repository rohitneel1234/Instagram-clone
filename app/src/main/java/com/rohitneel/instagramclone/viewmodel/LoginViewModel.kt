package com.rohitneel.instagramclone.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _emailValidation = MutableLiveData<Boolean>()
    val emailValidation: LiveData<Boolean> = _emailValidation

    private val _passwordValidation = MutableLiveData<Boolean>()
    val passwordValidation: LiveData<Boolean> = _passwordValidation

    private fun validateEmail(email: String): Boolean {
        val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        _emailValidation.postValue(isValid)
        return isValid
    }

    private fun validatePassword(password: String): Boolean {
        val passwordRegex =  "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=]).{8,}".toRegex()
        val isValid = passwordRegex.matches(password)
        _passwordValidation.postValue(isValid)
        return isValid
    }

     fun validateData(userEmail: String, userPassword: String): Boolean {
        val validateUserEmail = validateEmail(userEmail)
        val validateUserPassword = validatePassword(userPassword)

        return validateUserEmail && validateUserPassword
    }

}