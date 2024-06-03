package com.rohitneel.instagramclone.models

import androidx.compose.ui.graphics.Color
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.ui.theme.ColorBlue
import com.rohitneel.instagramclone.ui.theme.ColorGreen
import com.rohitneel.instagramclone.ui.theme.ColorYellow

data class OnboardingItem(
    val image: Int,
    val title: Int,
    val desc: Int,
    val backgroundColor: Color,
    val mainColor: Color = ColorBlue
) {
    companion object {
        fun getData() = listOf(
            OnboardingItem(
                image = R.drawable.creative_post,
                title = R.string.title_1,
                desc = R.string.description_1,
                backgroundColor = Color(0xFF0189C5),
                mainColor = Color(0xFF00B5EA)
            ),
            OnboardingItem(
                image = R.drawable.capture_moments,
                title = R.string.title_2,
                desc = R.string.description_2,
                backgroundColor = Color(0xFFE4AF19),
                mainColor = ColorYellow
            ),
            OnboardingItem(
                image = R.drawable.connect_engage,
                title = R.string.title_3,
                desc = R.string.description_3,
                backgroundColor = Color(0xFF96E172),
                mainColor = ColorGreen
            )
        )
    }
}
