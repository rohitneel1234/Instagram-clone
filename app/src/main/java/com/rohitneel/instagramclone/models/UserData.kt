package com.rohitneel.instagramclone.models

data class UserData(
    var userId: String? = null,
    var name: String? = null,
    var userName: String? = null,
    var userEmail: String? = null,
    var imageUrl: String? = null,
    var bio: String? = null,
    var following: List<String>? = null
) {
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "userName" to userName,
        "userEmail" to userEmail,
        "imageUrl" to imageUrl,
        "bio" to bio,
        "following" to following
    )
}
