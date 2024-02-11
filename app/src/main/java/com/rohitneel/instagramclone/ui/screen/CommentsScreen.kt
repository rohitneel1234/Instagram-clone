package com.rohitneel.instagramclone.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CommonDivider
import com.rohitneel.instagramclone.common.CommonImage
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.common.DeleteCommentDialog
import com.rohitneel.instagramclone.models.CommentData
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun ShowCommentScreen(
    isCommentBottomSheetOpened: MutableState<Boolean>,
    viewModel: InstagramViewModel,
    postId: String,
    postUserId: String?
) {
    val bottomSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var commentText by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val comments = viewModel.comments.value
    val commentsProgress = viewModel.commentsProgress.value
    val keyboardController = LocalSoftwareKeyboardController.current
    val userImage = viewModel.userData.value?.imageUrl
    val userId = viewModel.userData.value?.userId

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
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                if (commentsProgress) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CommonProgressSpinner()
                        }
                    }
                } else if (comments.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No comments yet",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Start the conversation.",
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    items(items = comments) {comment ->
                        CommentRow(comment, userId, postUserId, viewModel)
                    }
                }
            }
            CommonDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp)
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                        .size(36.dp)
                ) {
                    Image(
                        painter = rememberImagePainter(data = userImage),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Add a comment...") },
                    trailingIcon = {
                        if (commentText.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    viewModel.createComment(postId = postId, text = commentText)
                                    commentText = ""
                                    focusManager.clearFocus()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_comment_send),
                                    tint = colorResource(id = R.color.button_background_color),
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
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
                        .background(Color.White),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White, // Set your desired background color
                        cursorColor = colorResource(id = R.color.green_900), // Change cursor color if needed
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun CommentRow(
    comment: CommentData,
    userId: String?,
    postUserId: String?,
    viewModel: InstagramViewModel
) {
    var isCommentSelected by remember { mutableStateOf(false) }
    val modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    if (userId == postUserId) {
                        isCommentSelected = true
                    }
                }
            )
        }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            shape = CircleShape, modifier = Modifier
                .padding(4.dp)
                .size(40.dp)
        ) {
            CommonImage(data = comment.userImage, contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = comment.userName ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = comment.text ?: "", fontSize = 14.sp)
        }
    }
    if (isCommentSelected) {
        DeleteCommentDialog(
            onConfirm = {
                comment.commentId?.let { it1 -> viewModel.deleteComment(it1) }
                isCommentSelected = false
            },
            onCancel = { isCommentSelected = false }
        )
    }
}
