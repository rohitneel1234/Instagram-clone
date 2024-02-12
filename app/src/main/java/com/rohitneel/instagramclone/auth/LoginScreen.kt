package com.rohitneel.instagramclone.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CheckSignedIn
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.common.navigateTo
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: InstagramViewModel) {

    CheckSignedIn(navController = navController, viewModel = viewModel)

    val focus = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val emailState = remember { mutableStateOf(TextFieldValue()) }
            val passwordState = remember { mutableStateOf(TextFieldValue()) }
            var passwordVisibility by remember { mutableStateOf(false) }

            Image(
                painter = painterResource(id = R.drawable.ic_instagram_title),
                contentDescription = "Instagram title logo",
                modifier = Modifier
                    .width(250.dp)
                    .height(60.dp)
                    .padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                label = { Text(text = "Email") }
            )
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                label = { Text(text = "Password") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val painter = if (passwordVisibility)  painterResource(id = R.drawable.ic_visibility_24) else painterResource(id = R.drawable.ic_visibility_off_24)
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(painter, contentDescription = if (passwordVisibility) "Hide password" else "Show password")
                    }
                }
            )
            Button(
                onClick = {
                    focus.clearFocus(force = true)
                    viewModel.onLogin(emailState.value.text, passwordState.value.text)
                },
                shape = RoundedCornerShape(15),
                contentPadding = PaddingValues(vertical = 14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.button_background_color)),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Log in", fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Don't have an account? Sign up.",
                color = colorResource(id = R.color.button_background_color),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { navigateTo(navController, DestinationScreen.Signup) }
            )
        }
        val isLoading = viewModel.inProgress.value
        if (isLoading) {
            CommonProgressSpinner()
        }
    }

}