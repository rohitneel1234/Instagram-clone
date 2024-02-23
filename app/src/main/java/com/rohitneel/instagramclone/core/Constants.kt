package com.rohitneel.instagramclone.core

class Constants {

    companion object {
        // Login Screen
        // Placeholders
        const val EMAIL_PLACEHOLDER = "Email"
        const val USERNAME_PLACEHOLDER = "Username"
        const val PASSWORD_PLACEHOLDER = "Password"

        // Buttons
        const val LOGIN_BUTTON = "Log In"
        const val REGISTER_BUTTON = "Register"
        const val SIGN_UP_BUTTON = "Sign Up"
        const val FORGOT_PASSWORD_BUTTON = "Forgot Password?"
        const val LOG_OUT_BUTTON = "Log Out"

        // Firebase Constants
        const val POSTS_COLLECTION = "posts"
        const val USERS_COLLECTION = "users"
        const val COMMENTS_COLLECTION = "comments"
        const val STORIES_COLLECTION = "story"

        // Error Messages
        const val ERROR_EMAIL = "Please write correct email!"
        const val ERROR_USERNAME = "In username must be at not special characters and not too long!"
        const val ERROR_PASSWORD = "Password must be at least 6 digits!"
        const val ALREADY_LOGGED_IN = "You are already logged in!"

        // Storage
        const val IMAGES_PATH = "images/"

        // Time
        const val TIME_IN_MINUTE = 10
    }
}