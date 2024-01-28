package com.rohitneel.instagramclone.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.rohitneel.instagramclone.navigation.DestinationScreen

@Composable
fun CreatePostMenu(navController: NavController) {
    val newPostImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            val encoded = Uri.encode(it.toString())
            val route = DestinationScreen.NewPost.createRoute(encoded, true)
            navController.navigate(route)
        }
    }
    LaunchedEffect(true) {
        newPostImageLauncher.launch("image/*")
    }
}