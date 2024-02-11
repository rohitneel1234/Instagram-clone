package com.rohitneel.instagramclone.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CommonImage
import com.rohitneel.instagramclone.models.UserFollowConnections
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFollowerListScreen(
    viewModel: InstagramViewModel,
    navController: NavController,
    isFollowing: Boolean,
) {
    var selectedTabIndex by remember { mutableStateOf(if (isFollowing) 1 else 0) }
    Column {
        TopAppBar(
            title = {
                viewModel.userData.value?.userName?.let { Text(text = it, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            },
            navigationIcon = {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back",
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
            },
            colors = TopAppBarColors(
                containerColor = Color.White,
                scrolledContainerColor = Color.White,
                navigationIconContentColor = Color.Black,
                titleContentColor = Color.Black,
                actionIconContentColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))
        
        FollowListTabView(
            text = listOf(
                "Follower" to "${viewModel.followerListItem.value.size} Follower",
                "Following" to "${viewModel.followingListItem.value.size} Following"
            ),
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index ->
                selectedTabIndex = index
            }
        )
        when (selectedTabIndex) {
            0 -> LazyColumn(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                items(viewModel.followerListItem.value) {
                    FollowListItem(
                        user = it,
                        isFollowing = !isFollowing,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .clickable {}
                    )
                }
            }
            1 -> LazyColumn(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                items(viewModel.followingListItem.value) {
                    FollowListItem(
                        user = it,
                        isFollowing = isFollowing,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .clickable {}
                    )
                }
            }
        }
    }
}

@Composable
fun FollowListItem(user: UserFollowConnections, modifier: Modifier, isFollowing: Boolean) {
    var isFollowed by remember { mutableStateOf(isFollowing) }
    val backgroundColor = if (isFollowed) MaterialTheme.colorScheme.inverseOnSurface else colorResource(id = R.color.button_background_color)
    val text = if (isFollowed) "Following" else "Follow"
    val textColor = if (isFollowed) MaterialTheme.colorScheme.onBackground else Color.White
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card(
            shape = CircleShape, modifier = Modifier
                .padding(4.dp)
                .size(50.dp)
        ) {
            CommonImage(data = user.imageUri, contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.width(8.dp))
        user.name?.let {
            Text(
                text = it,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(vertical = 4.dp, horizontal = 12.dp)
                .sizeIn(minWidth = 80.dp, minHeight = 20.dp)
                .clickable {
                    isFollowed = !isFollowed
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
}

@Composable
fun FollowListTabView(
    modifier: Modifier = Modifier,
    text: List<Pair<String, String>>,
    selectedTabIndex: Int,
    onTabSelected: (selectedIndex: Int) -> Unit,
) {
    val inactiveColor = Color.Gray
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = Color.Black,
        modifier = modifier.padding(vertical = 1.dp)
    ) {
        text.forEachIndexed { index, item ->
            Tab(selected = selectedTabIndex == index,
                selectedContentColor = MaterialTheme.colorScheme.onBackground,
                unselectedContentColor = inactiveColor,
                onClick = {
                    onTabSelected(index)
                }
            ) {
                Text(
                    text = item.second,
                    modifier = modifier.padding(bottom = 10.dp)
                )
            }
        }
    }
}