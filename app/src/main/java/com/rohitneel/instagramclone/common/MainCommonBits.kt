package com.rohitneel.instagramclone.common

import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.ui.components.ToggleIconButton
import com.rohitneel.instagramclone.ui.screen.ShowCommentScreen
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import kotlinx.coroutines.delay

@Composable
fun NotificationMessage(viewModel: InstagramViewModel) {
    val notificationState = viewModel.popupNotification.value
    val notificationMessage = notificationState?.getContentOrNull()
    if (notificationMessage != null) {
        Toast.makeText(LocalContext.current, notificationMessage, Toast.LENGTH_LONG).show()
    }
}

@Composable
fun CommonProgressIndicator() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) { }
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CommonProgressSpinner() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Spinner(
            modifier = Modifier.size(75.dp),
            color = Color.Black,
            sectionLength = 8.dp,
            sectionWidth = 8.dp
        )
    }
}

@Composable
fun StoryPostProgressSpinner() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) { }
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Posting...")
        }
    }
}

data class NavParams(
    val name: String,
    val value: Parcelable
)

fun navigateTo(
    navController: NavController,
    destinationScreen: DestinationScreen,
    vararg params: NavParams
) {
    for (param in params) {
        navController.currentBackStackEntry?.arguments?.putParcelable(param.name, param.value)
    }
    navController.navigate(destinationScreen.route) {
        popUpTo(destinationScreen.route)
        launchSingleTop = true
    }
}

@Composable
fun CheckSignedIn(navController: NavController, viewModel: InstagramViewModel) {
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = viewModel.signedIn.value
    if (signedIn && !alreadyLoggedIn.value) {
        alreadyLoggedIn.value = true
        navController.navigate(DestinationScreen.Feed.route) {
            popUpTo(0)
        }
    }
}

@Composable
fun CommonImage(
    data: String?,
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberAsyncImagePainter(model = data)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
    if (painter.state is AsyncImagePainter.State.Loading) {
        CommonProgressIndicator()
    }
}

@Composable
fun UserImageCard(
    userImage: String?,
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 6.dp)
        .size(60.dp)
) {
    Card(shape = CircleShape, modifier = modifier) {
        if (userImage.isNullOrEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Gray),
                modifier = modifier
            )
        } else {
            CommonImage(data = userImage)
        }
    }
}

@Composable
fun CommonDivider() {
    HorizontalDivider(
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 8.dp, bottom = 8.dp),
        thickness = 1.dp,
        color = Color.LightGray
    )
}

@Composable
fun CustomButton(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    viewModel: InstagramViewModel,
    post: PostData
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .sizeIn(minWidth = 60.dp, minHeight = 20.dp)
            .clickable {
                viewModel.onFollowClick(post.userId!!)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = textColor
        )
    }
}

private enum class LikeIconSize {
    SMALL,
    LARGE
}

@Composable
fun LikeAnimation(like: Boolean = true) {
    var likeSizeState by remember { mutableStateOf(LikeIconSize.SMALL) }
    var dislikeSizeState by remember { mutableStateOf(LikeIconSize.SMALL) }

    LaunchedEffect(like) {
        likeSizeState = if (like) LikeIconSize.LARGE else LikeIconSize.SMALL
    }
    LaunchedEffect(!like) {
        dislikeSizeState = if (!like) LikeIconSize.LARGE else LikeIconSize.SMALL
    }

    val likeTransition = updateTransition(targetState = likeSizeState, label = "")
    val likeSize by likeTransition.animateDp(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }, label = ""
    ) { state ->
        when (state) {
            LikeIconSize.SMALL -> 0.dp
            LikeIconSize.LARGE -> 150.dp
        }
    }

    val dislikeTransition = updateTransition(targetState = dislikeSizeState, label = "")
    val dislikeSize by dislikeTransition.animateDp(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }, label = ""
    ) { state ->
        when (state) {
            LikeIconSize.SMALL -> 0.dp
            LikeIconSize.LARGE -> 150.dp
        }
    }

    Image(
        painter = painterResource(id = if (like) R.drawable.ic_like else R.drawable.ic_dislike),
        contentDescription = null,
        modifier = if (like) Modifier.size(size = likeSize) else Modifier.size(size = dislikeSize),
        colorFilter = ColorFilter.tint(if (like) Color.Red else Color.Gray)
    )
}

@Composable
fun ShowPostActionIcons(
    viewModel: InstagramViewModel,
    post: PostData,
    numberOfComments: Int,
    likeCount: MutableState<Int>,
    isFavorite: MutableState<Boolean>,
    isLikeCountVisible: MutableState<Boolean>,
    isCommentOptionVisible: MutableState<Boolean>
) {
    val isCommentBottomSheetOpened = remember { mutableStateOf(false) }
    var likeCountValue by remember { likeCount }
    var isBookmarked by remember { mutableStateOf(false) }
    val shareContent: String = ("Check out this post: " + post.postImage)
    val shareLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    Row(
        modifier = Modifier.padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ToggleIconButton(
            enableTint = Color.Red,
            enableIcon = rememberVectorPainter(image = Icons.Filled.Favorite),
            disableIcon = rememberVectorPainter(image = Icons.Filled.FavoriteBorder),
            initialState = isFavorite.value
        ) {
            isFavorite.value = !isFavorite.value
            viewModel.onLikePost(post, isFavorite.value)
            likeCountValue += if (isFavorite.value) 1 else -1
        }
        if (isCommentOptionVisible.value) {
            IconButton(
                onClick = {
                    post.postId?.let {
                        isCommentBottomSheetOpened.value = true
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_comment),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        IconButton(
            onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareContent)
                    type = "text/plain"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                shareLauncher.launch(Intent.createChooser(sendIntent, "Sharing post"))
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = null,
                tint = Color.Black,
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
    if (isLikeCountVisible.value) {
        Text(
            text = if (likeCountValue == -1) "0 likes" else "$likeCountValue likes",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

    Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp)) {
        Text(text = post.userName ?: "", fontWeight = FontWeight.Bold)
        Text(text = post.postDescription ?: "", modifier = Modifier.padding(start = 8.dp))
    }

    Row {
        Text(
            text = if (isCommentOptionVisible.value) "View $numberOfComments comments" else "Comments are off.",
            color = Color.DarkGray,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable {
                    post.postId?.let {
                        isCommentBottomSheetOpened.value = true
                    }
                }
        )
    }
    if (isCommentBottomSheetOpened.value && isCommentOptionVisible.value) {
        post.postId?.let {
            ShowCommentScreen(
                isCommentBottomSheetOpened = isCommentBottomSheetOpened,
                viewModel = viewModel,
                postId = it,
                postUserId = post.userId
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMoreOptionsBottomSheet(
    isBottomSheetOpened: MutableState<Boolean>,
    navController: NavController,
    viewModel: InstagramViewModel,
    post: PostData,
    isLikeCountVisible: MutableState<Boolean>,
    isCommentOptionVisible: MutableState<Boolean>
) {
    val bottomSheet = rememberModalBottomSheetState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                        .clickable {
                            isLikeCountVisible.value = !isLikeCountVisible.value
                            isBottomSheetOpened.value = false
                            if (isLikeCountVisible.value) {
                                Toast
                                    .makeText(context, "Like count unhidden", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast
                                    .makeText(context, "Like count hidden", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = if (isLikeCountVisible.value) R.drawable.ic_like_disabled else R.drawable.ic_dislike),
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLikeCountVisible.value) "Hide like count" else "Unhide like count",
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
                            isCommentOptionVisible.value = !isCommentOptionVisible.value
                            isBottomSheetOpened.value = false
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = if (isCommentOptionVisible.value) R.drawable.ic_comment_disabled else R.drawable.ic_comment),
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCommentOptionVisible.value) "Turn off commenting" else "Turn on commenting",
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

@Composable
fun DeleteCommentDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(tonalElevation = 8.dp, shape = RoundedCornerShape(8.dp)) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .width(180.dp)
                        .clickable { onConfirm() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 16.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    leadingIcon: ImageVector,
    leadingIconDescription: String = "",
    isPasswordField: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityChange: (Boolean) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    showError: Boolean = false,
    errorMessage: String = ""
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp),
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = leadingIconDescription,
                    tint = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            },
            isError = showError,
            trailingIcon = {
                if (showError && !isPasswordField)
                    Icon(imageVector = Icons.Filled.Error, contentDescription = "Show error icon")
                if (isPasswordField) {
                    IconButton(onClick = { onVisibilityChange(!isPasswordVisible) }) {
                        Icon(
                            painter = if (isPasswordVisible) painterResource(id = R.drawable.ic_visibility_24) else painterResource(
                                id = R.drawable.ic_visibility_off_24
                            ),
                            contentDescription = "Toggle password visibility"
                        )
                    }
                }
            },
            visualTransformation = when {
                isPasswordField && isPasswordVisible -> VisualTransformation.None
                isPasswordField -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true
        )
        if (showError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .offset(y = (-8).dp)
                    .fillMaxWidth(0.9f)
            )
        }
    }
}