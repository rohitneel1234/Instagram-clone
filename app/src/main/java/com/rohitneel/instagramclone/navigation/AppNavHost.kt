package com.rohitneel.instagramclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import com.rohitneel.instagramclone.auth.LoginScreen
import com.rohitneel.instagramclone.auth.ProfileScreen
import com.rohitneel.instagramclone.auth.SignupScreen
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.ui.screen.CreatePostMenu
import com.rohitneel.instagramclone.ui.screen.FeedScreen
import com.rohitneel.instagramclone.ui.screen.MyPostScreen
import com.rohitneel.instagramclone.ui.screen.NewPostScreen
import com.rohitneel.instagramclone.ui.screen.NotificationsScreen
import com.rohitneel.instagramclone.ui.screen.SearchScreen
import com.rohitneel.instagramclone.ui.screen.SearchedPostScreen
import com.rohitneel.instagramclone.ui.screen.SinglePostScreen
import com.rohitneel.instagramclone.ui.screen.UserFollowerListScreen
import com.rohitneel.instagramclone.ui.screen.ViewStory
import com.rohitneel.instagramclone.ui.screen.ViewUserStory

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: InstagramViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = DestinationScreen.Signup.route
    ) {
        composable(DestinationScreen.Signup.route){
            SignupScreen(navController = navController, viewModel = viewModel)
        }
        composable(DestinationScreen.Login.route) {
            LoginScreen(navController = navController, viewModel = viewModel)
        }
        composable(DestinationScreen.Feed.route) {
            FeedScreen(navController = navController, viewModel = viewModel)
        }
        composable(DestinationScreen.Search.route) {
            SearchScreen(navController = navController, viewModel = viewModel)
        }
        composable(DestinationScreen.MyPosts.route) {
            MyPostScreen(navController = navController, viewModel = viewModel)
        }
        composable(DestinationScreen.Profile.route) {
            ProfileScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            DestinationScreen.NewPost.route,
            arguments = listOf(
                navArgument("imageUri") {
                    type = NavType.StringType
                },
                navArgument("isMyPostScreen") {
                    type = NavType.BoolType
                },
                navArgument("isUserStory") {
                    type = NavType.BoolType
                }
        )) { navBackStackEntry ->
            val imageUri = navBackStackEntry.arguments?.getString("imageUri")
            val isMyPostScreen = navBackStackEntry.arguments?.getBoolean("isMyPostScreen") ?: false
            val isUserStory = navBackStackEntry.arguments?.getBoolean("isUserStory") ?: false
            imageUri?.let {
                NewPostScreen(navController = navController, viewModel = viewModel, encodedUri = it, isMyPostScreen = isMyPostScreen, isUserStory = isUserStory)
            }
        }
        composable(DestinationScreen.SinglePost.route) {
            val postData = navController
                .previousBackStackEntry
                ?.arguments
                ?.getParcelable<PostData>("post")
            postData?.let {
                SinglePostScreen(
                    navController = navController,
                    viewModel = viewModel,
                    post = postData
                )
            }
        }
        composable(DestinationScreen.SearchPost.route) {
            val postData = navController
                .previousBackStackEntry
                ?.arguments
                ?.getParcelable<PostData>("post")
            postData?.let {
                SearchedPostScreen(
                    navController = navController,
                    viewModel = viewModel,
                    post = postData
                )
            }
        }
        composable(DestinationScreen.AddPostButton.route) {
            CreatePostMenu(navController = navController)
        }
        composable(DestinationScreen.Notifications.route) {
            NotificationsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(
            DestinationScreen.ViewStory.route,
            arguments = listOf(
                navArgument("listOfImage") {
                    type = NavType.StringType
                },
                navArgument("userName") {
                    type = NavType.StringType
                },
                navArgument("profile") {
                    type = NavType.IntType
                }
            )
        ) { navBackStackEntry ->
            val listOfImages = navBackStackEntry.arguments?.getString("listOfImage") ?: ""
            val userName = navBackStackEntry.arguments?.getString("userName") ?: ""
            val profile = navBackStackEntry.arguments?.getInt("profile")
            profile?.let { ViewStory(navController = navController, jsonString = listOfImages, userName = userName, profile = it) }
        }
        composable(DestinationScreen.ViewUserStory.route) {
            ViewUserStory(navController = navController, viewModel = viewModel)
        }
        composable(
            DestinationScreen.FollowList.route,
            arguments = listOf(
                navArgument("isFollowing") {
                    type = NavType.BoolType
                }
            )
        ) {
            val isFollower = it.arguments?.getBoolean("isFollowing") ?: false
            UserFollowerListScreen(
                viewModel = viewModel,
                navController = navController,
                isFollowing = isFollower
            )
        }
    }

}