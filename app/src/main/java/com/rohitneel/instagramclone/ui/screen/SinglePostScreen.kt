package com.rohitneel.instagramclone.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CommonDivider
import com.rohitneel.instagramclone.common.CommonImage
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.ui.components.ToggleIconButton

@Composable
fun SinglePostScreen(navController: NavController, viewModel: InstagramViewModel, post: PostData) {
    val comments = viewModel.comments.value

    LaunchedEffect(key1 = Unit) {
        viewModel.getComments(post.postId)
    }

    post.userId?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Text(
                    text = "Explore",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            SinglePostDisplay(
                navController = navController,
                viewModel = viewModel,
                post = post,
                numberOfComments = comments.size
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
    val userData = viewModel.userData.value
    val isBottomSheetOpened = remember { mutableStateOf(false) }
    val isCommentBottomSheetOpened = remember { mutableStateOf(false) }

    ShowBottomSheetState(
        isBottomSheetOpened = isBottomSheetOpened,
        navController = navController,
        viewModel = viewModel,
        post = post
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                        contentDescription = null
                    )
                }
                Text(text = post.userName ?: "", fontWeight = FontWeight.Bold)
                Text(text = ".", modifier = Modifier.padding(8.dp))

                if (userData?.userId == post.userId) {
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
                } else if (userData?.following?.contains(post.userId) == true) {
                    Text(
                        text = "Following",
                        color = Color.Gray,
                        modifier = Modifier.clickable {
                            viewModel.onFollowClick(post.userId!!)
                        })
                } else {
                    Text(
                        text = "Follow",
                        color = colorResource(id = R.color.blue),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            viewModel.onFollowClick(post.userId!!)
                        })
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

        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            var isFavorite by remember { mutableStateOf(false) }
            var isBookmarked by remember { mutableStateOf(false) }
            ToggleIconButton(
                enableTint = Color.Red,
                enableIcon = rememberVectorPainter(image = Icons.Filled.Favorite),
                disableIcon = rememberVectorPainter(image = Icons.Filled.FavoriteBorder),
                initialState = isFavorite
            ) {
                if (it) {
                    post.likes
                    viewModel.onLikePost(post)
                } else {
                    post.likes
                }
                isFavorite = !isFavorite
            }
            IconButton(
                onClick = {
                    post.postId?.let {
                        //navController.navigate(DestinationScreen.Comments.createRoute(it))
                        isCommentBottomSheetOpened.value = true
                    }
                }
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_comment),
                    contentDescription = null,
                    Modifier.padding(vertical = 8.dp)
                )
            }
            IconButton(
                onClick = { }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(28.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            ToggleIconButton(
                enableTint = MaterialTheme.colorScheme.onBackground,
                enableIcon = painterResource(id = R.drawable.ic_save_post),
                disableIcon = painterResource(id = R.drawable.ic_unsave_post),
                initialState = isBookmarked
            ) {
                isBookmarked = !isBookmarked
            }
        }
        Text(
            text = "${post.likes?.size ?: 0} likes",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(text = post.userName ?: "", fontWeight = FontWeight.Bold)
            Text(text = post.postDescription ?: "", modifier = Modifier.padding(start = 8.dp))
        }

        Row {
            Text(
                text = "View $numberOfComments comments",
                color = Color.DarkGray,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        post.postId?.let {
                            //navController.navigate(DestinationScreen.Comments.createRoute(it))
                            isCommentBottomSheetOpened.value = true
                        }
                    }
            )
        }
    }

    if (isCommentBottomSheetOpened.value) {
        post.postId?.let {
            ShowCommentScreen(
                isCommentBottomSheetOpened = isCommentBottomSheetOpened,
                viewModel = viewModel,
                postId = it
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowCommentScreen(
    isCommentBottomSheetOpened: MutableState<Boolean>,
    viewModel: InstagramViewModel,
    postId: String
) {
    val bottomSheet = rememberModalBottomSheetState()
    var commentText by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val comments = viewModel.comments.value
    val commentsProgress = viewModel.commentsProgress.value
    val keyboardController = LocalSoftwareKeyboardController.current

    ModalBottomSheet(
        sheetState = bottomSheet,
        containerColor = Color.White,
        onDismissRequest = {
            isCommentBottomSheetOpened.value = false
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Text(
                    text = "Comments",
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                CommonDivider()
            }
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (commentsProgress) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CommonProgressSpinner()
                    }
                } else if (comments.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No comments yet", fontWeight = FontWeight.Bold)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(items = comments) { comment ->
                            CommentRow(comment)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Add a comment...") },
                    trailingIcon = {
                        IconButton(
                            onClick = { }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_comment_send),
                                tint = Color.Blue,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowBottomSheetState(
    isBottomSheetOpened: MutableState<Boolean>,
    navController: NavController,
    viewModel: InstagramViewModel,
    post: PostData
) {
    val bottomSheet = rememberModalBottomSheetState()
    var showDialog by remember { mutableStateOf(false) }

    if (isBottomSheetOpened.value) {
        ModalBottomSheet(
            sheetState = bottomSheet,
            containerColor = Color.White,
            onDismissRequest = {
                isBottomSheetOpened.value = false
            }
        ) {
            Column {
                Text(
                    text = "More options",
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_like_disabled),
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hide like count",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_comment_disabled),
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Turn off commenting",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDialog = true
                            isBottomSheetOpened.value = false
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "delete",
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    )
                }
            }
        }
    }

    if (showDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                post.postId?.let { viewModel.deletePost(it) }
                showDialog = false
                isBottomSheetOpened.value = false
                navController.popBackStack()
            }
        ) {
            showDialog = false
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(tonalElevation = 8.dp, shape = RoundedCornerShape(12.dp)) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Delete this post?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "Are you sure you want to delete this post? This action is final and will permanently remove the post.",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                CommonDivider()
                TextButton(
                    onClick = onConfirm,
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 16.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
                CommonDivider()
                TextButton(
                    onClick = onCancel,
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 15.sp,
                        color = colorResource(id = R.color.black)
                    )
                }
            }
        }
    }
}