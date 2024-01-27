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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rohitneel.instagramclone.common.CommonImage
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.common.NavParams
import com.rohitneel.instagramclone.common.UserImageCard
import com.rohitneel.instagramclone.common.navigateTo
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.ui.theme.LIGHT_BLUE
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@Composable
fun MyPostScreen(navController: NavController, viewModel: InstagramViewModel) {
    val newPostImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            val encoded = Uri.encode(it.toString())
            val route = DestinationScreen.NewPost.createRoute(encoded)
            navController.navigate(route)
        }
    }

    val userData = viewModel.userData.value
    val isLoading = viewModel.inProgress.value
    val postLoading = viewModel.refreshPostsProgress.value
    val posts = viewModel.posts.value
    val followers = viewModel.followers.value

    Column {
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
                    title = "Followers",
                    count = "$followers",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                StateItem(
                    title = "Following",
                    count = "${userData?.following?.size ?: 0}",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                val userNameDisplay =
                    if (userData?.userName == null) "" else "@${userData.userName}"
                Text(text = userData?.name ?: "", fontWeight = FontWeight.Bold)
                Text(text = userNameDisplay)
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
                shape = RoundedCornerShape(10)
            ) {
                Text(text = "Edit profile", color = Color.Black)
            }
            PostList(
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
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = modifier
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