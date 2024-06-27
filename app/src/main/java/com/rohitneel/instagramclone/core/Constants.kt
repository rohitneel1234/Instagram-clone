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
        const val USER_NAME_ERROR = "Please, input a valid user name!"
        const val EMAIL_ERROR = "The format of email doesn't seem correct"
        const val PASSWORD_ERROR = "Please, input capital and non-capital letters, a number, special character and a minimum of 8 digits"
        const val ALREADY_LOGGED_IN = "You are already logged in!"

        // Time
        const val TIME_IN_MINUTE = 10

        const val SPLASH_SCREEN_TIME = 3000L

        val IMAGE_URLS = listOf(
            "https://cdn.pixabay.com/photo/2023/05/28/03/34/flowers-8022731_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/05/07/20/12/forget-me-not-5143015_150.jpg",
            "https://cdn.pixabay.com/photo/2024/03/21/08/56/robin-8647147_150.jpg",
            "https://cdn.pixabay.com/photo/2024/03/19/13/44/flowers-8643341_150.jpg",
            "https://cdn.pixabay.com/photo/2024/03/18/12/02/flowers-8641008_150.jpg",
            "https://cdn.pixabay.com/photo/2024/03/19/18/54/mountain-goat-8643896_150.jpg",
            "https://cdn.pixabay.com/photo/2024/02/27/00/13/heliconia-8599119_150.jpg",
            "https://cdn.pixabay.com/photo/2024/02/25/10/11/forsythia-8595521_150.jpg",
            "https://cdn.pixabay.com/photo/2024/03/18/20/43/marigold-8641842_150.jpg",
            "https://cdn.pixabay.com/photo/2024/03/21/18/31/horse-8648282_150.jpg",
            "https://cdn.pixabay.com/photo/2024/02/21/15/28/dahlia-8587940_150.jpg",
            "https://cdn.pixabay.com/photo/2024/03/21/18/34/landscape-8648283_150.jpg",
            "https://cdn.pixabay.com/photo/2017/05/08/13/15/spring-bird-2295434_150.jpg",
            "https://cdn.pixabay.com/photo/2024/03/21/12/17/ai-generated-8647605_150.jpg",
        )
    }
}