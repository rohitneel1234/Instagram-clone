package com.rohitneel.instagramclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import com.rohitneel.instagramclone.auth.LoginScreen
import com.rohitneel.instagramclone.auth.ProfileScreen
import com.rohitneel.instagramclone.auth.SignupScreen
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.ui.screen.CommentsScreen
import com.rohitneel.instagramclone.ui.screen.CreatePostMenu
import com.rohitneel.instagramclone.ui.screen.FeedScreen
import com.rohitneel.instagramclone.ui.screen.MyPostScreen
import com.rohitneel.instagramclone.ui.screen.NewPostScreen
import com.rohitneel.instagramclone.ui.screen.NotificationsScreen
import com.rohitneel.instagramclone.ui.screen.SearchScreen
import com.rohitneel.instagramclone.ui.screen.SinglePostScreen

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
            SignupScreen(navController, viewModel)
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
        composable(DestinationScreen.NewPost.route) { navBackStackEntry ->
            val imageUri = navBackStackEntry.arguments?.getString("imageUri")
            imageUri?.let {
                NewPostScreen(navController = navController, viewModel = viewModel, encodedUri = it)
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
        composable(DestinationScreen.Comments.route) { navBackStackEntry ->
            val postId = navBackStackEntry.arguments?.getString("postId")
            postId?.let { CommentsScreen(viewModel = viewModel, postId = it) }
        }
        composable(DestinationScreen.AddPostButton.route) {
            CreatePostMenu(navController = navController, viewModel = viewModel)
        }
        composable(DestinationScreen.Notifications.route) {
            NotificationsScreen(navController = navController, viewModel = viewModel)
        }
    }

}