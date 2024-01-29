package com.rohitneel.instagramclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rohitneel.instagramclone.ui.components.BottomNavigationItem
import com.rohitneel.instagramclone.ui.components.BottomNavigationMenu
import com.rohitneel.instagramclone.common.NotificationMessage
import com.rohitneel.instagramclone.navigation.AppNavHost
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.ui.theme.InstagramCloneTheme
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstagramCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InstagramApp()
                }
            }
        }
    }
}

@Composable
fun InstagramApp() {
    val viewModel = hiltViewModel<InstagramViewModel>()
    val navController = rememberNavController()
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    showBottomBar = when (navBackStackEntry?.destination?.route) {
        DestinationScreen.Signup.route -> false // on this screen bottom bar should be hidden
        DestinationScreen.Login.route -> false // here too
        DestinationScreen.Profile.route -> false // here too
        DestinationScreen.NewPost.route -> false // here too
        DestinationScreen.ViewStory.route -> false // here too
        else -> true // in all other cases show bottom bar
    }

    NotificationMessage(viewModel = viewModel)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationMenu(
                    selectedItem = BottomNavigationItem.FEED,
                    navController = navController
                )
            }
        },
        containerColor = Color.White,
    ) {
        AppNavHost(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(it)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InstagramAppPreview() {
    InstagramCloneTheme {
        InstagramApp()
    }
}