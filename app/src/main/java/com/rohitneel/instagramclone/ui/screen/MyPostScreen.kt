package com.rohitneel.instagramclone.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CommonImage
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.common.NavParams
import com.rohitneel.instagramclone.common.UserImageCard
import com.rohitneel.instagramclone.common.navigateTo
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.ui.theme.LIGHT_BLUE
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostScreen(navController: NavController, viewModel: InstagramViewModel) {
    val newPostImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            val encoded = Uri.encode(it.toString())
            val route = DestinationScreen.NewPost.createRoute(encoded, false)
            navController.navigate(route)
        }
    }

    val userData = viewModel.userData.value
    val isLoading = viewModel.inProgress.value
    val postLoading = viewModel.refreshPostsProgress.value
    val posts = viewModel.posts.value
    val followers = viewModel.followers.value
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val userNameDisplay = if (userData?.userName == null) "" else "${userData.userName}"
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = userNameDisplay,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            colors = TopAppBarColors(
                containerColor = Color.White,
                scrolledContainerColor = Color.White,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.Black,
                actionIconContentColor = Color.Black
            ),
            actions = {
                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_menu),
                        contentDescription = "menu",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        )
        Column(modifier = Modifier.weight(1f)) {
            Row {
                ProfileImage(userData?.imageUrl) {
                    newPostImageLauncher.launch("image/*")
                }
                StateItem(
                    title = "Posts",
                    count = "${posts.size}",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                StateItem(
                    title = "Follower",
                    count = "$followers",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    onClick = {
                        val route = DestinationScreen.FollowList.createRoute(false)
                        navController.navigate(route)
                    }
                )
                StateItem(
                    title = "Following",
                    count = "${userData?.following?.size ?: 0}",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    onClick = {
                        val route = DestinationScreen.FollowList.createRoute(true)
                        navController.navigate(route)
                    }
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = userData?.name ?: "", fontWeight = FontWeight.Bold)
                Text(text = userData?.bio ?: "")
            }
            OutlinedButton(
                onClick = { navigateTo(navController, DestinationScreen.Profile) },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    disabledElevation = 0.dp
                ),
                border = BorderStroke(width = 1.dp, color = Color.Gray),
                shape = RoundedCornerShape(15)
            ) {
                Text(text = "Edit profile", color = Color.Black, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(20.dp))
            PostTabView(
                imageWithText = listOf(
                    "Posts" to painterResource(id = R.drawable.ic_grid),
                    "Profile" to painterResource(id = R.drawable.ic_profile)
                )
            ) {
                selectedTabIndex = it
            }
            when (selectedTabIndex) {
                0 -> PostList(
                    isContextLoading = isLoading,
                    postLoading = postLoading,
                    posts = posts,
                    modifier = Modifier
                        .weight(1f)
                        .padding(1.dp)
                        .fillMaxSize()
                ) { post ->
                    navigateTo(navController, DestinationScreen.SinglePost, NavParams("post", post))
                }
            }
        }
    }

    if (isLoading) {
        CommonProgressSpinner()
    }
}

@Composable
fun ProfileImage(imageUrl: String?, onClick: () -> Unit) {
    Box(modifier = Modifier
        .padding(top = 16.dp)
        .clickable { onClick.invoke() }
    ) {
        UserImageCard(
            userImage = imageUrl, modifier = Modifier
                .padding(8.dp)
                .size(80.dp)
        )
        Card(
            shape = CircleShape,
            border = BorderStroke(width = 2.dp, color = Color.White),
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp)
        ) {
            Icon(
                tint = LIGHT_BLUE,
                imageVector = Icons.Default.AddCircle,
                contentDescription = null
            )
        }
    }
}

@Composable
fun PostList(
    isContextLoading: Boolean,
    postLoading: Boolean,
    posts: List<PostData>,
    modifier: Modifier,
    onPostClick: (PostData) -> Unit
) {
    if (postLoading) {
        CommonProgressSpinner()
    } else if (posts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // Background color, adjust as needed
            contentAlignment = Alignment.Center
        ) {
            if (!isContextLoading) {
                Text(text = "No posts available", fontWeight = FontWeight.Bold)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // Specify number of columns for the grid
            modifier = modifier
        ) {
            items(posts) { post ->
                PostImage(
                    imageUrl = post.postImage,
                    modifier = Modifier
                        .padding(1.dp) // Adjust padding as needed
                        .aspectRatio(1f) // Maintain square aspect ratio
                        .clickable { onPostClick(post) }
                )
            }
        }
    }
}

@Composable
fun PostImage(imageUrl: String?, modifier: Modifier) {
    Row(modifier = modifier) {
        var imageModifier = Modifier
            .padding(1.dp)
            .fillMaxSize()
        if (imageUrl == null) {
            imageModifier = imageModifier.clickable(enabled = false) {}
        }
        CommonImage(data = imageUrl, modifier = imageModifier, contentScale = ContentScale.Crop)
    }
}

@Composable
fun StateItem(
    title: String,
    count: String,
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = count,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif
        )
        Text(
            text = title,
            fontSize = 14.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun PostTabView(
    modifier: Modifier = Modifier,
    imageWithText: List<Pair<String, Painter>>,
    onTabSelected: (selectedIndex: Int) -> Unit,
) {
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    val inactiveColor = Color.Gray
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = Color.Black,
        modifier = modifier.padding(vertical = 1.dp)
    ) {
        imageWithText.forEachIndexed { index, item ->
            Tab(selected = selectedTabIndex == index,
                selectedContentColor = MaterialTheme.colorScheme.onBackground,
                unselectedContentColor = inactiveColor,
                onClick = {
                    selectedTabIndex = index
                    onTabSelected(index)
                }
            ) {
                Icon(
                    painter = item.second,
                    contentDescription = item.first,
                    tint = if (selectedTabIndex == index)
                        MaterialTheme.colorScheme.onBackground
                    else
                        inactiveColor,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp)
                )
            }
        }
    }
}