package com.rohitneel.instagramclone.common

import android.os.Parcelable
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@Composable
fun NotificationMessage(viewModel: InstagramViewModel) {
    val notificationState = viewModel.popupNotification.value
    val notificationMessage = notificationState?.getContentOrNull()
    if (notificationMessage != null) {
        Toast.makeText(LocalContext.current, notificationMessage, Toast.LENGTH_LONG).show()
    }
}

@Composable
fun CommonProgressSpinner() {
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

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CommonImage(
    data: String?,
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberImagePainter(data = data)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
    if (painter.state is ImagePainter.State.Loading) {
        CommonProgressSpinner()
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

private enum class LikeIconSize() {
    SMALL,
    LARGE
}

@Composable
fun LikeAnimation(like: Boolean = true) {
    var sizeState by remember { mutableStateOf(LikeIconSize.SMALL) }
    val transition = updateTransition(targetState = sizeState, label = "")
    val size by transition.animateDp(
        label = "",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }
    ) { state ->
        when (state) {
            LikeIconSize.SMALL -> 0.dp
            LikeIconSize.LARGE -> 150.dp
        }
    }

    Image(
        painter = painterResource(id = if (like) R.drawable.ic_like else R.drawable.ic_dislike),
        contentDescription = null,
        modifier = Modifier.size(size = size),
        colorFilter = ColorFilter.tint(if (like) Color.Red else Color.Gray)
    )
    sizeState = LikeIconSize.LARGE
}