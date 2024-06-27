package com.rohitneel.instagramclone.models
data class LoginState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
