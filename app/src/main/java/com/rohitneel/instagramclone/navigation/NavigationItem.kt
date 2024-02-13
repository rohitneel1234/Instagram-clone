package com.rohitneel.instagramclone.navigation

import android.net.Uri

sealed class DestinationScreen(val route:String){

    object Signup: DestinationScreen("signup")

    object Login: DestinationScreen("login")

    object Feed: DestinationScreen("feed")

    object Search: DestinationScreen("search")

    object MyPosts: DestinationScreen("post")

    object Profile: DestinationScreen("profile")

    object NewPost: DestinationScreen("newpost/{imageUri}/{isMyPostScreen}/{isUserStory}") {
        fun createRoute(uri: String, isMyPostScreen: Boolean? = false, isUserStory: Boolean? = false) = "newpost/$uri/$isMyPostScreen/$isUserStory"
    }
    object SinglePost: DestinationScreen("singlepost")

    object AddPostButton: DestinationScreen("addpost")

    object Notifications: DestinationScreen("notifications")

    object ViewStory: DestinationScreen("viewstory/{listOfImage}") {
        fun createRoute(listOfImage: String? = ""): String {
            val formattedListOfImage = listOfImage ?: "listOfImage"
            return "viewstory/$formattedListOfImage"
        }
    }
    object ViewUserStory: DestinationScreen("viewuserstory/{imageUri}") {
        fun createRoute(uri: Uri): String {
            val encodedUri = Uri.encode(uri.toString())
            return "viewuserstory/$encodedUri"
        }
    }
    object FollowList: DestinationScreen("followers/{isFollowing}") {
        fun createRoute(isFollowing: Boolean) = "followers/$isFollowing"
    }
}