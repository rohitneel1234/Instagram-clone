package com.rohitneel.instagramclone.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewStory(navController: NavController, jsonString: String, userName: String, profile: Int) {
    val listOfImage: List<Int> = if (jsonString.isEmpty()) emptyList() else {
        try {
            Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
    val pagerState = rememberPagerState(pageCount = { listOfImage.size })
    val coroutineScope = rememberCoroutineScope()
    var currentPage by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState) {
            Image(
                painter = painterResource(id = listOfImage[it]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(4.dp)
                    .size(32.dp)
            ) {
                Image(
                    painter = painterResource(id = profile),
                    contentDescription = null,
                    modifier = Modifier.wrapContentSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = userName,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(4.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.verticalGradient(listOf(Color.Black, Color.Transparent))),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.padding(4.dp))

            for (index in listOfImage.indices) {
                LinearIndicator(
                    modifier = Modifier.weight(1f),
                    startProgress = index == currentPage
                ) {
                    coroutineScope.launch {
                        currentPage++
                        if (currentPage < listOfImage.size) {
                            pagerState.animateScrollToPage(currentPage)
                        }
                        if (currentPage == listOfImage.size) {
                            navController.popBackStack()
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
fun LinearIndicator(
    modifier: Modifier,
    startProgress: Boolean = false,
    onAnimationEnd: () -> Unit
) {
    val slideDurationInSeconds: Long = 5
    val delayInMillis = rememberSaveable { (slideDurationInSeconds * 1000) / 100 }
    var progress by remember { mutableStateOf(0.00f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
    )

    if (startProgress) {
        LaunchedEffect(key1 = Unit) {
            while (progress < 1f) {
                progress += 0.01f
                delay(delayInMillis)
            }
            //When the timer is not paused and animation completes then move to next page.
            onAnimationEnd()
        }
    }

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = Color.White,
    )
}