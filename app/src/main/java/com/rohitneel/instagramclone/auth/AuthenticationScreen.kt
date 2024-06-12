package com.rohitneel.instagramclone.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@Composable
fun AuthenticationScreen(navController: NavController, viewModel: InstagramViewModel) {
    val (selected, setSelected) = remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_app_logo),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Welcome Back!", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(40.dp))
            CustomTab(
                items = listOf("Existing", "New"),
                selectedItemIndex = selected,
                onClick = setSelected,
            )
            if (selected == 0) {
                LoginScreen(navController = navController, viewModel = viewModel)
            } else {
                SignupScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CustomTab(
    selectedItemIndex: Int,
    items: List<String>,
    modifier: Modifier = Modifier,
    tabWidth: Dp = 120.dp,
    onClick: (index: Int) -> Unit,
) {
    val indicatorOffset: Dp by animateDpAsState(
        targetValue = tabWidth * selectedItemIndex,
        animationSpec = tween(easing = LinearEasing), label = "",
    )
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(colorResource(id = R.color.gray_color))
            .height(intrinsicSize = IntrinsicSize.Min),
    ) {
        MyTabIndicator(
            indicatorWidth = tabWidth,
            indicatorOffset = indicatorOffset,
            indicatorColor = colorResource(id = R.color.button_background_color),
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.clip(CircleShape),
        ) {
            items.mapIndexed { index, text ->
                val isSelected = index == selectedItemIndex
                MyTabItem(
                    isSelected = isSelected,
                    onClick = {
                        onClick(index)
                    },
                    tabWidth = tabWidth,
                    text = text,
                )
            }
        }
    }
}

@Composable
private fun MyTabIndicator(
    indicatorWidth: Dp,
    indicatorOffset: Dp,
    indicatorColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(
                width = indicatorWidth,
            )
            .offset(
                x = indicatorOffset,
            )
            .clip(
                shape = CircleShape,
            )
            .background(
                color = indicatorColor,
            ),
    )
}

@Composable
private fun MyTabItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    tabWidth: Dp,
    text: String,
) {
    val tabTextColor: Color by animateColorAsState(
        targetValue = if (isSelected) {
            Color.White
        } else {
            Color.Black
        },
        animationSpec = tween(easing = LinearEasing), label = "",
    )
    Text(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .width(tabWidth)
            .padding(
                vertical = 8.dp,
                horizontal = 12.dp,
            ),
        text = text,
        color = tabTextColor,
        textAlign = TextAlign.Center,
    )
}