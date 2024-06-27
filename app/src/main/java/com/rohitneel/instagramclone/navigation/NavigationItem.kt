package com.rohitneel.instagramclone.navigation

sealed class DestinationScreen(val route:String){

    object Splash: DestinationScreen("splash")

    object Onboarding: DestinationScreen("onboarding")

    object Authentication: DestinationScreen("authentication")

    object ForgotPassword: DestinationScreen("forgotpassword")

    object Feed: DestinationScreen("feed")

    object Search: DestinationScreen("search")

    object MyPosts: DestinationScreen("post")

    object Profile: DestinationScreen("profile")

    object NewPost: DestinationScreen("newpost/{imageUri}/{isMyPostScreen}/{isUserStory}") {
        fun createRoute(uri: String, isMyPostScreen: Boolean? = false, isUserStory: Boolean? = false) = "newpost/$uri/$isMyPostScreen/$isUserStory"
    }

    object SinglePost: DestinationScreen("singlepost")

    object SearchPost: DestinationScreen("searchpost")

    object AddPostButton: DestinationScreen("addpost")

    object Notifications: DestinationScreen("notifications")

    object ViewStory: DestinationScreen("viewstory/{listOfImage}/{userName}/{profile}") {
        fun createRoute(listOfImage: String? = "", userName: String? = "", profile: Int): String {
            val formattedListOfImage = listOfImage ?: "listOfImage"
            val storyUserName = userName ?: ""
            return "viewstory/$formattedListOfImage/$storyUserName/$profile"
        }
    }
    object ViewUserStory: DestinationScreen("viewuserstory")

    object FollowList: DestinationScreen("followers/{isFollowing}") {
        fun createRoute(isFollowing: Boolean) = "followers/$isFollowing"
    }
}