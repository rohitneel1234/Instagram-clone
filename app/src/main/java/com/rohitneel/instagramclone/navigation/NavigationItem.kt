package com.rohitneel.instagramclone.navigation

sealed class DestinationScreen(val route:String){
    object Signup: DestinationScreen("signup")
    object Login: DestinationScreen("login")
    object Feed: DestinationScreen("feed")
    object Search: DestinationScreen("search")
    object MyPosts: DestinationScreen("post")
    object Profile: DestinationScreen("profile")
    object NewPost: DestinationScreen("newpost/{imageUri}") {
        fun createRoute(uri: String) = "newpost/$uri"
    }
    object SinglePost: DestinationScreen("singlepost")
    object Comments: DestinationScreen("comments/{postId}") {
        fun createRoute(postId: String) =  "comments/$postId"
    }
    object AddPostButton: DestinationScreen("addpost")

    object Notifications: DestinationScreen("notifications")
}