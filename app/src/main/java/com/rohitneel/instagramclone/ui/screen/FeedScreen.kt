package com.rohitneel.instagramclone.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CommonImage
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.common.LikeAnimation
import com.rohitneel.instagramclone.common.ShowMoreOptionsBottomSheet
import com.rohitneel.instagramclone.common.ShowPostActionIcons
import com.rohitneel.instagramclone.common.UserImageCard
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.models.Stories
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.ui.theme.LIGHT_BLUE
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(navController: NavController, viewModel: InstagramViewModel, isUserStory: Boolean) {

    val userDataLoading = viewModel.inProgress.value
    val userData = viewModel.userData.value
    val personalizedFeed = viewModel.postFeed.value
    val personalizedFeedLoading = viewModel.postFeedProgress.value
    var isAddToStory by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        TopAppBar(title = {
            Image(
                painter = painterResource(id = R.drawable.ic_instagram_title),
                contentDescription = null,
                Modifier.padding(vertical = 8.dp)
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
                        .clickable {
                            if (isUserStory) {
                                navController.navigate(DestinationScreen.ViewUserStory.route)
                            } else {
                                isAddToStory = true
                            }
                        }
                ) {
                    if (isUserStory) {
                        UserStoryImageCard(userImage = userData?.imageUrl)
                    } else {
                        UserImageCard(userImage = userData?.imageUrl)
                    }
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
            StoriesSection(storyList = getStories(), navController = navController)
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
    if (isAddToStory) {
        CreateStory(navController = navController)
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun UserStoryImageCard(
    userImage: String?,
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 6.dp)
) {
    Card(shape = CircleShape, modifier = modifier) {
        if (userImage.isNullOrEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Gray),
                modifier = modifier.size(60.dp)
            )
        } else {
            Image(
                painter = rememberImagePainter(data = userImage),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color("#43A047".toColorInt()),
                                Color("#03DAC5".toColorInt())
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(5.dp)
                    .clip(CircleShape),
                contentScale =  ContentScale.Crop
            )
        }
    }
}

@Composable
fun CreateStory(navController: NavController) {
    val newStoryImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            val encoded = Uri.encode(it.toString())
            val route = DestinationScreen.NewPost.createRoute(encoded, isUserStory = true)
            navController.navigate(route)
        }
    }
    LaunchedEffect(true) {
        newStoryImageLauncher.launch("image/*")
    }
}

@Composable
fun StoriesSection(storyList: List<Stories>, navController: NavController) {
    LazyRow {
        items(storyList) { story ->
            StoryItem(story = story, navController = navController)
        }
    }
}

fun getStories(): List<Stories> = listOf(
    Stories(userName = "instagram", profile = R.drawable.ic_instagram_logo),
    Stories(userName = "instagram", profile = R.drawable.ic_instagram_logo),
    Stories(userName = "instagram", profile = R.drawable.ic_instagram_logo),
    Stories(userName = "instagram", profile = R.drawable.ic_instagram_logo)
)

@Composable
fun StoryItem(story: Stories, navController: NavController) {
    Column(modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 8.dp)
        .width(60.dp)) {
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
                .clip(CircleShape)
                .clickable {
                    val listOfImage = listOf(R.drawable.insta_story_01, R.drawable.insta_story_02)
                    val jsonString = Json.encodeToString(listOfImage)
                    val route = DestinationScreen.ViewStory.createRoute(jsonString)
                    navController.navigate(route)
                },
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
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .alpha(0.3f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }
            items(items = posts) {
                Post(
                    post = it,
                    currentUserId = currentUserId,
                    viewModel = viewModel,
                    navController = navController
                )
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
    navController: NavController
) {
    val likeAnimation = remember { mutableStateOf(false) }
    val dislikeAnimation = remember { mutableStateOf(false) }
    val isBottomSheetOpened = remember { mutableStateOf(false) }
    val userData = viewModel.userData.value
    val comments = viewModel.comments.value

    ShowMoreOptionsBottomSheet(
        isBottomSheetOpened = isBottomSheetOpened,
        navController = navController,
        viewModel = viewModel,
        post = post
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .padding(bottom = 10.dp),
    ) {
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
             if (userData?.following?.contains(post.userId) == true) {
                Text(
                    text = "Following",
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .clickable {
                            viewModel.onFollowClick(post.userId!!)
                        })
            } else if (userData?.userId != post.userId) {
                Text(
                    text = "Follow",
                    color = colorResource(id = R.color.blue),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .clickable {
                            viewModel.onFollowClick(post.userId!!)
                        })
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    if (userData?.userId == post.userId) {
                        isBottomSheetOpened.value = true
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
            }
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
        ShowPostActionIcons(
            viewModel = viewModel,
            post = post,
            numberOfComments = comments.size
        )
    }
}