package com.rohitneel.instagramclone.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.common.navigateTo

enum class BottomNavigationItem(val icon: Int, val navDestinationScreen: DestinationScreen){
    FEED(R.drawable.ic_home, DestinationScreen.Feed),
    SEARCH(R.drawable.ic_search, DestinationScreen.Search),
    ADD_POSTS(R.drawable.ic_add_posts, DestinationScreen.AddPostButton),
    NOTIFICATIONS(R.drawable.ic_notifications, DestinationScreen.Notifications),
    POSTS(R.drawable.ic_posts, DestinationScreen.MyPosts)
}

@Composable
fun BottomNavigationMenu(selectedItem: BottomNavigationItem, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White)
    ) {
        for (item in BottomNavigationItem.values()) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(5.dp)
                    .weight(1f)
                    .clickable {
                        navigateTo(navController, item.navDestinationScreen)
                    },
                colorFilter = if(item == selectedItem) ColorFilter.tint(Color.Black)
                else ColorFilter.tint(Color.DarkGray)
            )
        }
    }
}