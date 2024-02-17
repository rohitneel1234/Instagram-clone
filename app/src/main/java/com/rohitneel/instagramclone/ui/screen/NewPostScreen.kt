package com.rohitneel.instagramclone.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@OptIn(ExperimentalCoilApi::class)
@Composable
fun NewPostScreen(
    navController: NavController,
    viewModel: InstagramViewModel,
    encodedUri: String,
    isMyPostScreen: Boolean,
    isUserStory: Boolean
) {
    val imageUri by remember { mutableStateOf(encodedUri) }
    var description by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                modifier = Modifier.clickable {
                    if (isMyPostScreen || isUserStory) {
                        val route = DestinationScreen.Feed.createRoute(isUserStory = false)
                        navController.navigate(route)
                    } else {
                        navController.popBackStack()
                    }
                }
            )
            Text(
                text = if (isUserStory) "New story" else "New post",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Image(
            painter = rememberImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp),
            contentScale = ContentScale.FillWidth
        )

        Row(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text(text = "Write a caption...") },
                singleLine = false,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (isUserStory) {
                        viewModel.onNewStory(Uri.parse(encodedUri)) {
                            navController.navigate(DestinationScreen.ViewUserStory.route)
                        }
                    } else {
                        viewModel.onNewPost(Uri.parse(imageUri), description) {
                            navController.navigate(DestinationScreen.MyPosts.route)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(15),
                colors = ButtonColors(
                    containerColor = colorResource(id = R.color.blue),
                    contentColor = colorResource(id = R.color.white),
                    disabledContainerColor = colorResource(id = R.color.blue),
                    disabledContentColor = colorResource(id = R.color.blue)
                )
            ) {
                Text(
                    text = "Share",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    val inProgress = viewModel.inProgress.value
    if (inProgress) {
        CommonProgressSpinner()
    }
}
