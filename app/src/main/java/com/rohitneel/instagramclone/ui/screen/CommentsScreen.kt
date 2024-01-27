package com.rohitneel.instagramclone.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import com.rohitneel.instagramclone.models.CommentData

@Composable
fun CommentsScreen(viewModel: InstagramViewModel, postId: String) {
    var commentText by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val comments = viewModel.comments.value
    val commentsProgress = viewModel.commentsProgress.value
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if(commentsProgress) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CommonProgressSpinner()
            }
        } else if(comments.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No comments yet")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items =  comments) { comment ->
                    CommentRow(comment)
                }
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        ) {
            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.LightGray),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Button(
                onClick = {
                    viewModel.createComment(postId = postId, text = commentText)
                    commentText = ""
                    focusManager.clearFocus()
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "Comment")
            }
        }

    }
}

@Composable
fun CommentRow(comment: CommentData) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(text = comment.userName ?: "", fontWeight = FontWeight.Bold)
        Text(text = comment.text ?: "", modifier = Modifier.padding(start = 8.dp))
    }
}
