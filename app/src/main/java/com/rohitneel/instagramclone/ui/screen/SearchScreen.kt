package com.rohitneel.instagramclone.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.NavParams
import com.rohitneel.instagramclone.common.navigateTo
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import com.rohitneel.instagramclone.navigation.DestinationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: InstagramViewModel) {
    val searchedPostLoading = viewModel.searchedPostProgress.value
    val searchedPosts = viewModel.searchedPost.value
    var searchTerm by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val userData = viewModel.userData.value

    // Observe search term changes to clear searched posts when searchTerm is empty
    LaunchedEffect(searchTerm) {
        if (searchTerm.isEmpty()) {
            viewModel.clearSearchedPosts()
        }
    }

    Column {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp,
                    horizontal = 16.dp
                ),
            query = searchTerm,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchTerm.isNotEmpty()) {
                    Icon(
                        Icons.Default.Close, contentDescription = "Clear",
                        modifier = Modifier.clickable { searchTerm = "" }
                    )
                }
            },
            onQueryChange = { searchTerm = it },
            placeholder = {
                Text(
                    text = "Search name",
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.light_gray_color)
                )
            },
            onSearch = {
                viewModel.searchPosts(searchTerm)
                focusManager.clearFocus()
            },
            active = false,
            colors = SearchBarDefaults.colors(containerColor = Color.LightGray),
            onActiveChange = {}
        ) {}

        PostList(
            isContextLoading = false,
            isSearchScreenPost = true,
            postLoading = searchedPostLoading,
            posts = searchedPosts,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
        ) { post ->
            if (userData?.userId == post.userId) {
                navigateTo(navController = navController, destinationScreen = DestinationScreen.MyPosts)
            } else {
                navigateTo(
                    navController = navController,
                    destinationScreen = DestinationScreen.SearchPost,
                    NavParams("post", post)
                )
            }
        }
    }
}