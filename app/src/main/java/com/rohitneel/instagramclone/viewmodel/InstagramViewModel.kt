package com.rohitneel.instagramclone.viewmodel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.rohitneel.instagramclone.core.Constants.Companion.COMMENTS_COLLECTION
import com.rohitneel.instagramclone.core.Constants.Companion.POSTS_COLLECTION
import com.rohitneel.instagramclone.core.Constants.Companion.STORIES_COLLECTION
import com.rohitneel.instagramclone.core.Constants.Companion.TIME_IN_MINUTE
import com.rohitneel.instagramclone.core.Constants.Companion.USERS_COLLECTION
import com.rohitneel.instagramclone.models.CommentData
import com.rohitneel.instagramclone.models.Event
import com.rohitneel.instagramclone.models.UserFollowConnections
import com.rohitneel.instagramclone.models.PostData
import com.rohitneel.instagramclone.models.UserData
import com.rohitneel.instagramclone.models.UserStoryInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class InstagramViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    val signedIn = mutableStateOf(false)
    val inProgress = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val popupNotification = mutableStateOf<Event<String>?>(null)

    val refreshPostsProgress = mutableStateOf(false)
    var isStoryVisible = mutableStateOf(false)
    val posts = mutableStateOf<List<PostData>>(listOf())
    val stories = mutableStateOf<UserStoryInfo?>(null)
    val searchedPost = mutableStateOf<List<PostData>>(listOf())
    val searchedPostProgress = mutableStateOf(false)

    val postFeed = mutableStateOf<List<PostData>>(listOf())
    val postFeedProgress = mutableStateOf(false)
    val comments = mutableStateOf<List<CommentData>>(listOf())
    val commentsProgress = mutableStateOf(false)
    var likedPostList = mutableStateOf<List<PostData>>(listOf())
    val likedPostProgress = mutableStateOf(false)

    val followers = mutableStateOf(0)
    val followerListItem = mutableStateOf<List<UserFollowConnections>>(emptyList())
    val followingListItem = mutableStateOf<List<UserFollowConnections>>(emptyList())

    init {
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let { userId ->
            getUserData(userId)
        }
        getLikedPosts()
    }

    fun onSignup(userName: String, email: String, password: String) {
        if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill in all fields")
            return
        }
        inProgress.value = true
        database.collection(USERS_COLLECTION).whereEqualTo("userName", userName).get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    handleException(customMessage = "Username already exists")
                    inProgress.value = false
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signedIn.value = true
                                createOrUpdateProfile(userName = userName)
                            } else {
                                handleException(task.exception, "Signup Failed")
                            }
                            inProgress.value = false
                        }
                }
            }
            .addOnFailureListener { }
    }

    fun onLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill in all fields")
            return
        }
        inProgress.value = true
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                signedIn.value = true
                inProgress.value = false
                auth.currentUser?.uid?.let { userId ->
                    getUserData(userId)
                }
            } else {
                handleException(task.exception, "Login failed")
                inProgress.value = false
            }
        }
            .addOnFailureListener { exception ->
                handleException(exception, "Login failed")
                inProgress.value = false
            }
    }

    private fun createOrUpdateProfile(
        name: String? = null,
        userName: String? = null,
        bio: String? = null,
        imageUrl: String? = null
    ) {
        val userId = auth.currentUser?.uid
        val userData = UserData(
            userId = userId,
            name = name ?: userData.value?.name,
            userName = userName ?: userData.value?.userName,
            bio = bio ?: userData.value?.bio,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
            following = userData.value?.following
        )
        userId?.let { userId ->
            inProgress.value = true
            database.collection(USERS_COLLECTION).document(userId).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        it.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                this.userData.value = userData
                                inProgress.value = false
                            }
                            .addOnFailureListener {
                                handleException(it, "Cannot update user")
                                inProgress.value = false
                            }
                    } else {
                        database.collection(USERS_COLLECTION).document(userId).set(userData)
                        getUserData(userId)
                        inProgress.value = false
                    }
                }
                .addOnFailureListener { exception ->
                    handleException(exception, "Cannot create user")
                    inProgress.value = false
                }
        }
    }

    private fun getUserData(userId: String) {
        inProgress.value = true
        database.collection(USERS_COLLECTION).document(userId).get()
            .addOnSuccessListener {
                val user = it.toObject<UserData>()
                userData.value = user
                inProgress.value = false
                refreshPosts()
                getPersonalizedFeed()
                getFollowers(user?.userId)
                getFollowerListItem(user?.userId)
                getFollowingListItem(user?.userId)
            }
            .addOnFailureListener { exception ->
                handleException(exception, "Cannot retrieve user model")
                inProgress.value = false
            }
    }

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMessage else "$customMessage: $errorMessage"
        popupNotification.value = Event(message)
    }

    fun updateProfileData(name: String, userName: String, bio: String) {
        createOrUpdateProfile(name, userName, bio)
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask
            .addOnSuccessListener {
                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
            }
            .addOnFailureListener { exe ->
                handleException(exe)
                inProgress.value = false
            }
    }

    private fun uploadStory(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("stories/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask
            .addOnSuccessListener {
                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
            }
            .addOnFailureListener { exe ->
                handleException(exe)
                inProgress.value = false
            }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
            updatePostUserImageData(it.toString())
        }
    }

    private fun updatePostUserImageData(imageUrl: String) {
        val currentUid = auth.currentUser?.uid
        database.collection(POSTS_COLLECTION).whereEqualTo("userId", currentUid).get()
            .addOnSuccessListener {
                val posts = mutableStateOf<List<PostData>>(arrayListOf())
                convertPosts(it, posts)
                val refs = arrayListOf<DocumentReference>()
                for (post in posts.value) {
                    post.postId?.let { id ->
                        refs.add(database.collection(POSTS_COLLECTION).document(id))
                    }
                }
                if (refs.isNotEmpty()) {
                    database.runBatch { batch ->
                        for (ref in refs) {
                            batch.update(ref, "userImage", imageUrl)
                        }
                    }
                        .addOnSuccessListener {
                            refreshPosts()
                        }
                }
            }
    }

    fun onLogout() {
        auth.signOut()
        signedIn.value = false
        userData.value = null
        popupNotification.value = Event("Logged out")
        searchedPost.value = listOf()
        postFeed.value = listOf()
        comments.value = listOf()
        likedPostList.value = listOf()
        followerListItem.value = listOf()
        followingListItem.value = listOf()
    }

    fun onNewPost(uri: Uri, description: String, onPostSuccess: () -> Unit) {
        uploadImage(uri) {
            onCreatePost(it, description, onPostSuccess)
        }
    }

    fun onNewStory(uri: Uri, onPostSuccess: () -> Unit) {
        onPostSuccess.invoke()
        uploadStory(uri) {
            onCreateStory(it, onPostSuccess)
        }
    }

    private fun onCreatePost(imageUri: Uri, description: String, onPostSuccess: () -> Unit) {
        inProgress.value = true
        val currentUid = auth.currentUser?.uid
        val currentUsername = userData.value?.userName
        val currentUserImage = userData.value?.imageUrl

        if (currentUid != null) {
            val postUuid = UUID.randomUUID().toString()

            val fillerWords = listOf("the", "be", "to", "is", "of", "and", "or", "a", "in", "it")
            val searchTerms = description.split(" ", ".", ",", "?", "!", "#")
                .map { it.lowercase() }
                .filter { it.isNotEmpty() and !fillerWords.contains(it) }
                .flatMap { term -> listOf(term, currentUsername.toString()) }
                .distinct()
            val post = PostData(
                postId = postUuid,
                userId = currentUid,
                userName = currentUsername,
                userImage = currentUserImage,
                postImage = imageUri.toString(),
                postDescription = description,
                time = System.currentTimeMillis(),
                likes = listOf(),
                searchTerms = searchTerms
            )
            database.collection(POSTS_COLLECTION).document(postUuid).set(post)
                .addOnSuccessListener {
                    popupNotification.value = Event("Post successfully created")
                    inProgress.value = false
                    refreshPosts()
                    onPostSuccess.invoke()
                }
                .addOnFailureListener { exe ->
                    handleException(exe, "Unable to create post")
                    inProgress.value = false
                }
        } else {
            handleException(customMessage = "Error: username unavailable. Unable to create post")
            onLogout()
            inProgress.value = false
        }
    }

    private fun onCreateStory(imageUri: Uri, onPostSuccess: () -> Unit) {
        inProgress.value = true
        val currentUid = auth.currentUser?.uid
        if (currentUid != null) {
            val storyUuid = UUID.randomUUID().toString()

            val story = UserStoryInfo(
                storyId = storyUuid,
                userId = currentUid,
                story = imageUri.toString(),
                timestamp = Timestamp.now()
            )
            database.collection(STORIES_COLLECTION).document(storyUuid).set(story)
                .addOnSuccessListener {
                    inProgress.value = false
                    getStoryData()
                    isStoryVisible.value = true
                }
                .addOnFailureListener { exe ->
                    handleException(exe, "Unable to create story")
                    inProgress.value = false
                }
        } else {
            handleException(customMessage = "Error: username unavailable. Unable to create story")
            onLogout()
            inProgress.value = false
        }
    }

    private fun refreshPosts() {
        val currentUid = auth.currentUser?.uid
        if (currentUid != null) {
            refreshPostsProgress.value = true
            database.collection(POSTS_COLLECTION).whereEqualTo("userId", currentUid).get()
                .addOnSuccessListener { documents ->
                    convertPosts(documents, posts)
                    refreshPostsProgress.value = false
                }
                .addOnFailureListener { exe ->
                    handleException(exe, "Cannot fetch posts")
                    refreshPostsProgress.value = false
                }
        } else {
            handleException(customMessage = "Error: username unavailable. Unable to refresh posts")
            onLogout()
        }
    }

    private fun getStoryData() {
        val currentUid = auth.currentUser?.uid
        if (currentUid != null) {
            database.collection(STORIES_COLLECTION).whereEqualTo("userId", currentUid).get()
                .addOnSuccessListener { documents ->
                    val currentTime = Timestamp.now()
                    val userStories = mutableListOf<UserStoryInfo>()
                    for (document in documents) {
                        val story = document.toObject<UserStoryInfo>()
                        if (currentTime.seconds - story.timestamp.seconds <= TIME_IN_MINUTE * 60) {
                            userStories.add(story)
                        } else {
                            story.storyId?.let { database.collection(STORIES_COLLECTION).document(it).delete() } // Delete old story
                        }
                    }
                    userStories.sortByDescending { it.timestamp }
                    stories.value = userStories.firstOrNull() // Update stories state with the first story or null if no stories exist
                }
                .addOnFailureListener { exe ->
                    handleException(exe, "Cannot fetch stories")
                }
        } else {
            handleException(customMessage = "Error: username unavailable. Unable to refresh stories")
            onLogout()
        }
    }

    private fun convertPosts(documents: QuerySnapshot, outState: MutableState<List<PostData>>) {
        val newPosts = mutableListOf<PostData>()
        documents.forEach { doc ->
            val post = doc.toObject<PostData>()
            newPosts.add(post)
        }
        val sortedPosts = newPosts.sortedByDescending { it.time }
        outState.value = sortedPosts
    }

    fun searchPosts(searchTerm: String) {
        if (searchTerm.isNotEmpty()) {
            searchedPostProgress.value = true
            database.collection(POSTS_COLLECTION)
                .whereArrayContains("searchTerms", searchTerm.trim().lowercase())
                .get()
                .addOnSuccessListener {
                    convertPosts(it, searchedPost)
                    searchedPostProgress.value = false
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot search posts")
                    searchedPostProgress.value = false
                }
        }
    }

    fun onFollowClick(userId: String) {
        auth.currentUser?.uid?.let { currentUser ->
            val following = arrayListOf<String>()
            userData.value?.following?.let {
                following.addAll(it)
            }
            if (following.contains(userId)) {
                following.remove(userId)
            } else {
                following.add(userId)
            }
            database.collection(USERS_COLLECTION).document(currentUser).update("following", following)
                .addOnSuccessListener {
                    getUserData(currentUser)
                }
        }

    }

    private fun getPersonalizedFeed() {
        val following = userData.value?.following
        if (!following.isNullOrEmpty()) {
            database.collection(POSTS_COLLECTION).whereEqualTo("userId", following).get()
                .addOnSuccessListener {
                    convertPosts(documents = it, outState = postFeed)
                    if (postFeed.value.isEmpty()) {
                        getGeneralFeed()
                    } else {
                        postFeedProgress.value = false
                    }
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot get personalized feed")
                    postFeedProgress.value = false
                }
        } else {
            getGeneralFeed()
        }
    }

    private fun getGeneralFeed() {
        postFeedProgress.value = true
        val currentTime = System.currentTimeMillis()
        val difference = 24 * 60 * 60 * 1000 // 1 day in millis
        database.collection(POSTS_COLLECTION)
            .whereGreaterThan("time", currentTime - difference)
            .get()
            .addOnSuccessListener {
                convertPosts(documents = it, outState = postFeed)
                postFeedProgress.value = false
            }
            .addOnFailureListener { exc ->
                handleException(exc, "Cannot get feed")
                postFeedProgress.value = false
            }
    }

    fun onLikePost(postData: PostData, isLiked: Boolean) {
        auth.currentUser?.uid?.let { userId ->
            postData.likes?.let { likes ->
                val newLikes = ArrayList<String>(likes)
                val hasLiked = likes.contains(userId)
                if (isLiked) {
                    if (!hasLiked) {
                        newLikes.add(userId)
                    }
                    postData.isLiked = true
                } else {
                    if (hasLiked) {
                        newLikes.remove(userId)
                    }
                    postData.isLiked = false
                }
                postData.postId?.let { postId ->
                    database.collection(POSTS_COLLECTION).document(postId).update(mapOf("likes" to newLikes, "isLiked" to postData.isLiked))
                        .addOnSuccessListener {
                            postData.likes = newLikes
                            if (hasLiked) {
                                getLikedPosts()
                            }
                        }
                        .addOnFailureListener {
                            handleException(it, "Unable to like post")
                        }
                }
            }
        }
    }

    fun createComment(postId: String, text: String) {
        userData.value?.userName?.let { username ->
            val commentId = UUID.randomUUID().toString()
            val comment = CommentData(
                commentId = commentId,
                postId = postId,
                userName = username,
                userImage = userData.value?.imageUrl,
                text = text,
                timeStamp = System.currentTimeMillis()
            )
            database.collection(COMMENTS_COLLECTION).document(commentId).set(comment)
                .addOnSuccessListener {
                    getComments(postId)
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot create comment.")
                }
        }
    }

    fun getComments(postId: String?) {
        commentsProgress.value = true
        database.collection(COMMENTS_COLLECTION).whereEqualTo("postId", postId).get()
            .addOnSuccessListener { documents ->
                val newComment = mutableListOf<CommentData>()
                documents.forEach { doc ->
                    val comment = doc.toObject<CommentData>()
                    newComment.add(comment)
                }
                val sortedComment = newComment.sortedByDescending { it.timeStamp }
                comments.value = sortedComment
                commentsProgress.value = false
            }
            .addOnFailureListener { exc ->
                handleException(exc, "Cannot retrieve comments")
                commentsProgress.value = false
            }
    }

    private fun getLikedPosts() {
        likedPostProgress.value = true
        val currentUid = auth.currentUser?.uid
        database.collection(POSTS_COLLECTION).whereEqualTo("userId", currentUid).get()
            .addOnSuccessListener { snapshot ->
                convertPosts(snapshot, likedPostList)
                likedPostProgress.value = false
            }
            .addOnFailureListener { exc ->
                handleException(exc, "Cannot retrieve liked posts")
                likedPostProgress.value = false
            }
    }

    private fun getFollowers(uid: String?) {
        database.collection(USERS_COLLECTION).whereArrayContains("following", uid ?: "").get()
            .addOnSuccessListener { documents ->
                followers.value = documents.size()
            }
    }

    fun deletePost(postId: String) {
        val fireStore = FirebaseFirestore.getInstance()
        val postsCollection = fireStore.collection(POSTS_COLLECTION)
        postsCollection.document(postId).delete()
            .addOnSuccessListener {
                popupNotification.value = Event("Post deleted")
                refreshPosts()
            }
            .addOnFailureListener { exception ->
                handleException(exception, "Failed to delete post")
            }
    }

    fun deleteComment(commentId: String) {
        val fireStore = FirebaseFirestore.getInstance()
        val postsCollection = fireStore.collection(COMMENTS_COLLECTION)
        postsCollection.document(commentId).delete()
            .addOnSuccessListener {
                popupNotification.value = Event("Comment deleted")
            }
            .addOnFailureListener { exception ->
                handleException(exception, "Unable to delete comment")
            }
    }

    private fun getFollowerListItem(uid: String?) {
        val followersList = mutableListOf<UserFollowConnections>()
        database.collection(USERS_COLLECTION)
            .whereArrayContains("following", uid ?: "")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Assuming each document contains a field "name" representing the follower's name
                    val name = document.getString("name")
                    val imageUri = document.getString("imageUrl")
                    if (name != null && imageUri != null) {
                        followersList.add(UserFollowConnections(name, imageUri))
                    }
                }
                // Update the followers list
                followerListItem.value = followersList
            }
            .addOnFailureListener { exception ->
                // Handle errors
                handleException(exception, "Unable to fetch followers list")
            }
    }

    private fun getFollowingListItem(uid: String?) {
        val followingList = mutableListOf<UserFollowConnections>()
        database.collection(USERS_COLLECTION)
            .document(uid ?: "")
            .get()
            .addOnSuccessListener { document ->
                val followingArray = document.get("following") as? List<String>
                followingArray?.let { followingIds ->
                    for (followingId in followingIds) {
                        // Fetch the details of each following user
                        database.collection(USERS_COLLECTION)
                            .document(followingId)
                            .get()
                            .addOnSuccessListener { followingDocument ->
                                val name = followingDocument.getString("name")
                                val imageUri = followingDocument.getString("imageUrl")
                                name?.let { validName ->
                                    imageUri?.let { validImageUri ->
                                        followingList.add(
                                            UserFollowConnections(
                                                validName,
                                                validImageUri
                                            )
                                        )
                                        // If all following have been fetched, update the list
                                        if (followingList.size == followingIds.size) {
                                            followingListItem.value = followingList
                                        }
                                    }
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Handle failures to fetch following details
                                handleException(exception, "Unable to fetch following details")
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle failures to fetch user document
                handleException(exception, "Error in fetching user document")
            }
    }

}