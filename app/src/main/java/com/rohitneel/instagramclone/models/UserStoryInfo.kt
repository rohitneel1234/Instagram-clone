package com.rohitneel.instagramclone.models

import com.google.firebase.Timestamp

data class UserStoryInfo(
    val storyId: String? = null,
    val userId: String? = null,
    var story: String? = null,
    val timestamp: Timestamp = Timestamp.now()
)
