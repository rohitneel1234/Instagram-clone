package com.rohitneel.instagramclone.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CommonImage
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.common.LikeAnimation
import com.rohitneel.instagramclone.common.NavParams
import com.rohitneel.instagramclone.common.UserImageCard
import com.rohitneel.instagramclone.common.navigateTo
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.models.Stories
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.ui.theme.LIGHT_BLUE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(navController: NavController, viewModel: InstagramViewModel) {

    val userDataLoading = viewModel.inProgress.value
    val userData = viewModel.userData.value
    val personalizedFeed = viewModel.postFeed.value
    val personalizedFeedLoading = viewModel.postFeedProgress.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        TopAppBar(title = {
            Image(
                painter = painterResource(id = R.drawable.ic_instagram_title_logo),
                contentDescription = null,
                Modifier.padding(8.dp)
            )
        },
            colors = TopAppBarColors(
                containerColor = Color.White,
                scrolledContainerColor = Color.White,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
                actionIconContentColor = Color.Black
            ),
            actions = {
                Row {
                    IconButton(onClick = {
                        //navController.navigate(NavigationItem.Notification.route)
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_notifications),
                            contentDescription = null,
                            Modifier.padding(8.dp)
                        )
                    }
                    IconButton(onClick = {
                        //navController.navigate(NavigationItem.ChatList.route)
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_message),
                            contentDescription = null,
                            Modifier.padding(8.dp)
                        )
                    }
                }
            })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .clickable { }
                ) {
                    UserImageCard(userImage = userData?.imageUrl)
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
                Text(
                    text = "Your story",
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(60.dp)
                        .padding(start = 10.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            StoriesSection(storyList = getStories())
        }
        PostsList(
            posts = personalizedFeed,
            modifier = Modifier.weight(1f),
            loading = personalizedFeedLoading or userDataLoading,
            navController = navController,
            viewModel = viewModel,
            currentUserId = userData?.userId ?: ""
        )
    }
}

@Composable
fun StoriesSection(storyList: List<Stories>) {
    LazyRow {
        items(storyList) { story ->
            StoryItem(story = story)
        }
    }
}

fun getStories(): List<Stories> = listOf(
    Stories(userName = "instagram", profile = R.drawable.ic_instagram_app_logo),
    Stories(userName = "instagram", profile = R.drawable.ic_instagram_app_logo),
    Stories(userName = "instagram", profile = R.drawable.ic_instagram_app_logo),
    Stories(userName = "instagram", profile = R.drawable.ic_instagram_app_logo)
)

@Composable
fun StoryItem(story: Stories) {

    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
        Image(
            painter = painterResource(id = story.profile),
            contentDescription = "story title",
            modifier = Modifier
                .size(60.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color("#DE0046".toColorInt()),
                            Color("#F7A34B".toColorInt())
                        )
                    ),
                    shape = CircleShape
                )
                .padding(5.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = story.userName,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(60.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }

}

@Composable
fun PostsList(
    posts: List<PostData>,
    modifier: Modifier,
    loading: Boolean,
    navController: NavController,
    viewModel: InstagramViewModel,
    currentUserId: String
) {
    Box(modifier = modifier) {
        LazyColumn {
            items(items = posts) {
                Post(post = it, currentUserId = currentUserId, viewModel = viewModel) {
                    navigateTo(
                        navController,
                        DestinationScreen.SinglePost,
                        NavParams("post", it)
                    )
                }
            }
        }
        if (loading) {
            CommonProgressSpinner()
        }
    }
}

@Composable
fun Post(
    post: PostData,
    currentUserId: String,
    viewModel: InstagramViewModel,
    onPostClick: () -> Unit
) {
    val likeAnimation = remember { mutableStateOf(false) }
    val dislikeAnimation = remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(corner = CornerSize(4.dp)),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp, bottom = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape, modifier = Modifier
                        .padding(4.dp)
                        .size(32.dp)
                ) {
                    CommonImage(data = post.userImage, contentScale = ContentScale.Crop)
                }
                Text(
                    text = post.userName ?: "",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (post.likes?.contains(currentUserId) == true) {
                                    dislikeAnimation.value = true
                                } else {
                                    likeAnimation.value = true
                                }
                                viewModel.onLikePost(post)
                            },
                            onTap = {
                                onPostClick.invoke()
                            }
                        )
                    }
                CommonImage(
                    data = post.postImage,
                    modifier = modifier,
                    contentScale = ContentScale.FillWidth
                )

                if (likeAnimation.value) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        likeAnimation.value = false
                    }
                    LikeAnimation()
                }
                if (dislikeAnimation.value) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        dislikeAnimation.value = false
                    }
                    LikeAnimation()
                }
            }
        }
    }
}