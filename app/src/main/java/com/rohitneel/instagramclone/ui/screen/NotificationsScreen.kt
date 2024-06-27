package com.rohitneel.instagramclone.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.rohitneel.instagramclone.common.CommonImage
import com.rohitneel.instagramclone.common.CommonProgressIndicator
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: InstagramViewModel,
    navController: NavController,
) {
    val likedPostProgress = viewModel.likedPostProgress.value

    Column {
        TopAppBar(
            title = {
                Text(
                    text = "Notifications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }, colors = TopAppBarColors(
                containerColor = Color.White,
                scrolledContainerColor = Color.White,
                navigationIconContentColor = Color.Black,
                titleContentColor = Color.Black,
                actionIconContentColor = Color.Black
            ),
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        )

        if (likedPostProgress) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CommonProgressIndicator()
            }
        }

        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(viewModel.likedPostList.value) { post ->
                NotificationItem(postData = post)
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun NotificationItem(postData: PostData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            shape = CircleShape, modifier = Modifier
                .padding(4.dp)
                .size(40.dp)
        ) {
            CommonImage(data = postData.userImage, contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.width(10.dp))
        postData.userName?.let {
            Text(text = it, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = " liked your photo.", fontSize = 14.sp)
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = rememberImagePainter(data = postData.postImage),
            contentDescription = null,
            modifier = Modifier
                .padding(1.dp)
                .size(40.dp)
                .wrapContentSize()
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
    }
}