package com.rohitneel.instagramclone.navigation

sealed class DestinationScreen(val route:String){

    object Signup: DestinationScreen("signup")

    object Login: DestinationScreen("login")

    object Feed: DestinationScreen("feed")

    object Search: DestinationScreen("search")

    object MyPosts: DestinationScreen("post")

    object Profile: DestinationScreen("profile")

    object NewPost: DestinationScreen("newpost/{imageUri}/{isMyPostScreen}") {
        fun createRoute(uri: String, isMyPostScreen: Boolean) = "newpost/$uri/$isMyPostScreen"
    }
    object SinglePost: DestinationScreen("singlepost")

    object AddPostButton: DestinationScreen("addpost")

    object Notifications: DestinationScreen("notifications")

    object ViewStory: DestinationScreen("viewstory/{listOfImage}/{imageUri}") {
        fun createRoute(listOfImage: String? = "", uri: String? = ""): String {
            val formattedUri = if (uri.isNullOrEmpty()) "empty" else uri
            val formattedListOfImage = listOfImage?.ifEmpty { "listOfImage" }
            return "viewstory/$formattedListOfImage/$formattedUri"
        }
    }
    object FollowList: DestinationScreen("followers/{isFollowing}") {
        fun createRoute(isFollowing: Boolean) = "followers/$isFollowing"
    }
}