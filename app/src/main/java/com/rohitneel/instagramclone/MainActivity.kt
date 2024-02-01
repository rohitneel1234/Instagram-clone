package com.rohitneel.instagramclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.rohitneel.instagramclone.common.NotificationMessage
import com.rohitneel.instagramclone.navigation.AppNavHost
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.ui.components.BottomNavigationItems
import com.rohitneel.instagramclone.ui.theme.InstagramCloneTheme
import com.rohitneel.instagramclone.ui.theme.SetStatusBarColor
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstagramCloneTheme {
                SetStatusBarColor(Color.White)
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

@OptIn(ExperimentalCoilApi::class)
@Composable
fun InstagramApp() {
    val viewModel = hiltViewModel<InstagramViewModel>()
    val navController = rememberNavController()
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
    val userData = viewModel.userData.value
    val profileIconPainter = userData?.imageUrl?.let {
        rememberImagePainter(
            data = it,
            builder = {
                // You can apply transformations or other settings here
                transformations(CircleCropTransformation())
                scale(Scale.FIT)
                size(100)
            }
        )
    } ?: painterResource(id = R.drawable.ic_profile_icon)

    NotificationMessage(viewModel = viewModel)

    showBottomBar = when (navBackStackEntry?.destination?.route) {
        DestinationScreen.Signup.route -> false // on this screen bottom bar should be hidden
        DestinationScreen.Login.route -> false // here too
        DestinationScreen.Profile.route -> false // here too
        DestinationScreen.NewPost.route -> false // here too
        DestinationScreen.ViewStory.route -> false // here too
        else -> true // in all other cases show bottom bar
    }

    val items = listOf(
        BottomNavigationItems(
            selectedIcon = painterResource(id = R.drawable.ic_filled_home),
            unselectedIcon = painterResource(id = R.drawable.ic_home),
            bottomNavRoutes = DestinationScreen.Feed
        ),
        BottomNavigationItems(
            selectedIcon = painterResource(id = R.drawable.ic_filled_search),
            unselectedIcon = painterResource(id = R.drawable.ic_search),
            bottomNavRoutes = DestinationScreen.Search
        ),
        BottomNavigationItems(
            selectedIcon = painterResource(id = R.drawable.ic_add_posts),
            unselectedIcon = painterResource(id = R.drawable.ic_add_posts),
            bottomNavRoutes = DestinationScreen.AddPostButton
        ),
        BottomNavigationItems(
            selectedIcon = painterResource(id = R.drawable.ic_filled_notifications),
            unselectedIcon = painterResource(id = R.drawable.ic_notifications),
            bottomNavRoutes = DestinationScreen.Notifications
        ),
        BottomNavigationItems(
            selectedIcon = profileIconPainter,
            unselectedIcon = profileIconPainter,
            bottomNavRoutes = DestinationScreen.MyPosts
        ),
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(50.dp),
                containerColor = Color.White
            ) {
                items.forEachIndexed { index, item ->
                    if (showBottomBar) {
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                navController.navigate(item.bottomNavRoutes.route)
                            },
                            icon = {
                                BadgedBox(
                                    badge = { }
                                ) {
                                    Icon(
                                        painter = if (index == selectedItemIndex) {
                                            item.selectedIcon
                                        } else {
                                            item.unselectedIcon
                                        },
                                        contentDescription = null
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.White)
                        )
                    }
                }
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