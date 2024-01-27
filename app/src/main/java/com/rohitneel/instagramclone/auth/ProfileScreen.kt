package com.rohitneel.instagramclone.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CommonDivider
import com.rohitneel.instagramclone.common.CommonImage
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.common.navigateTo
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: InstagramViewModel) {
    val isLoading = viewModel.inProgress.value
    if(isLoading) {
        CommonProgressSpinner()
    } else {
        val userData = viewModel.userData.value
        var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
        var userName by rememberSaveable { mutableStateOf(userData?.userName ?: "") }
        var bio by rememberSaveable { mutableStateOf(userData?.bio ?: "") }

        ProfileContent(
            viewModel = viewModel,
            name = name,
            userName = userName,
            bio = bio,
            onNameChange = { name = it },
            onUserNameChange = { userName = it },
            onBioChange = { bio = it },
            onSave = { viewModel.updateProfileData(name, userName, bio)},
            onBack = { navigateTo(navController = navController, DestinationScreen.MyPosts) },
            onLogout = {
                viewModel.onLogout()
                navigateTo(navController = navController, DestinationScreen.Login)
            }
        )
    }

}

@Composable
fun ProfileContent(
    viewModel: InstagramViewModel,
    name: String,
    userName: String,
    bio: String,
    onNameChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val imageUrl = viewModel.userData.value?.imageUrl
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                modifier = Modifier.clickable { onBack.invoke() }
            )
            Text(
                text = "Edit profile",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "save",
                tint = colorResource(id = R.color.blue),
                modifier = Modifier.clickable { onSave.invoke() }
            )
        }
        ProfileImage(imageUrl = imageUrl, viewModel = viewModel)
        CommonDivider()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 4.dp)
        ) {
            TextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(text = "Name") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
            TextField(value = userName,
                onValueChange = onUserNameChange,
                label = { Text(text = "Username") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = bio,
                onValueChange = onBioChange,
                label = { Text(text = "Bio") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "Logout",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { showDialog = true }
            )
        }
    }
    if (showDialog) {
        LogoutConfirmationDialog(
            onLogout = { onLogout.invoke() },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun ProfileImage(imageUrl: String?, viewModel: InstagramViewModel) {
    val launcher =  rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfileImage(uri) }
    }
    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { launcher.launch("image/*") },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
                ) {
                CommonImage(data = imageUrl)
            }
            Text(
                text = "Edit picture",
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.blue),
                fontSize = 14.sp
            )
        }
        val isLoading = viewModel.inProgress.value
        if (isLoading) {
            CommonProgressSpinner()
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(onLogout: () -> Unit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(tonalElevation = 8.dp, shape = RoundedCornerShape(12.dp)) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Log out from account?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp)
                )
                CommonDivider()
                TextButton(
                    onClick = onLogout,
                ) {
                    Text(
                        text = "Log out",
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.blue),
                        fontWeight = FontWeight.Bold
                    )
                }
                CommonDivider()
                TextButton(
                    onClick = onDismiss,
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 15.sp,
                        color = colorResource(id = R.color.black)
                    )
                }
            }
        }
    }
}