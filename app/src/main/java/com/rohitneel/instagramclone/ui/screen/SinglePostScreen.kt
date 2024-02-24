package com.rohitneel.instagramclone.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.rohitneel.instagramclone.common.CommonImage
import com.rohitneel.instagramclone.common.ShowMoreOptionsBottomSheet
import com.rohitneel.instagramclone.common.ShowPostActionIcons
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@Composable
fun SinglePostScreen(navController: NavController, viewModel: InstagramViewModel, post: PostData) {
    val comments = viewModel.comments.value
    val posts = viewModel.posts.value

    LaunchedEffect(key1 = Unit) {
        viewModel.getComments(post.postId)
    }

    post.userId?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Text(
                    text = "Posts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
            MyPostList(
                posts = posts,
                navController = navController,
                viewModel = viewModel,
                numberOfComments = comments.size
            )
        }
    }
}

@Composable
fun MyPostList(
    posts: List<PostData>,
    navController: NavController,
    viewModel: InstagramViewModel,
    numberOfComments: Int
) {
    LazyColumn {
        items(items = posts) {
            SinglePostDisplay(
                post = it,
                navController = navController,
                viewModel = viewModel,
                numberOfComments = numberOfComments
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun SinglePostDisplay(
    navController: NavController,
    viewModel: InstagramViewModel,
    post: PostData,
    numberOfComments: Int
) {
    val isBottomSheetOpened = remember { mutableStateOf(false) }
    val likeCount = remember { mutableStateOf(post.likes?.size ?: 0) }
    val isFavorite = remember { mutableStateOf(post.isLiked) }
    val isLikeCountVisible = remember { mutableStateOf(true) }
    val isCommentOptionVisible = remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        viewModel.getComments(post.postId)
    }

    ShowMoreOptionsBottomSheet(
        isBottomSheetOpened = isBottomSheetOpened,
        navController = navController,
        viewModel = viewModel,
        post = post,
        isLikeCountVisible = isLikeCountVisible,
        isCommentOptionVisible = isCommentOptionVisible
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                ) {
                    Image(
                        painter = rememberImagePainter(data = post.userImage),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Text(text = post.userName ?: "", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        isBottomSheetOpened.value = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        Box {
            val modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp)
            CommonImage(
                data = post.postImage,
                modifier = modifier,
                contentScale = ContentScale.FillWidth
            )
        }
        ShowPostActionIcons(
            viewModel = viewModel,
            post = post,
            numberOfComments = numberOfComments,
            likeCount = likeCount,
            isFavorite = isFavorite,
            isLikeCountVisible = isLikeCountVisible,
            isCommentOptionVisible = isCommentOptionVisible
        )
    }
}