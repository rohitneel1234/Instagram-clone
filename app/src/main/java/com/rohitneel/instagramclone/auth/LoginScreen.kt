package com.rohitneel.instagramclone.auth

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.Identity
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CheckSignedIn
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.common.CustomOutlinedTextField
import com.rohitneel.instagramclone.core.Constants
import com.rohitneel.instagramclone.navigation.DestinationScreen
import com.rohitneel.instagramclone.ui.theme.LightBlueWhite
import com.rohitneel.instagramclone.util.SharedPreferencesHelper
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import com.rohitneel.instagramclone.viewmodel.LoginViewModel
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavController, viewModel: InstagramViewModel) {

    CheckSignedIn(navController = navController, viewModel = viewModel)


    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val focus = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val loginViewModel = viewModel<LoginViewModel>()

    var userEmail by rememberSaveable { mutableStateOf("") }
    var userPassword by rememberSaveable { mutableStateOf("") }

    val isEmailValid by loginViewModel.emailValidation.observeAsState(initial = true)
    val isPasswordValid by loginViewModel.passwordValidation.observeAsState(initial = true)

    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                val credential = Identity.getSignInClient(context).getSignInCredentialFromIntent(result.data)
                val userId = credential.id
                scope.launch {
                    val userExists = viewModel.checkUserExists(userId)
                    if (userExists) {
                        val signInResult = googleAuthUiClient.signInWithIntent(intent = result.data ?: return@launch)
                        viewModel.onSignInResult(signInResult, context)
                    } else {
                        Toast.makeText(context, "User does not exist! Please create user", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        CustomOutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = "Email",
            showError = !isEmailValid,
            errorMessage = Constants.EMAIL_ERROR,
            leadingIcon = Icons.Filled.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focus.moveFocus(FocusDirection.Down)}
            )
        )

        CustomOutlinedTextField(
            value = userPassword,
            onValueChange = { userPassword = it },
            label = "Password",
            showError = !isPasswordValid,
            errorMessage = Constants.PASSWORD_ERROR,
            isPasswordField = true,
            isPasswordVisible = isPasswordVisible,
            onVisibilityChange = { isPasswordVisible = it },
            leadingIcon = Icons.Filled.Lock,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focus.moveFocus(FocusDirection.Down)}
            )
        )

        Text(
            text = "Forgot Password?",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colorResource(id = R.color.button_background_color),
            modifier = Modifier
                .padding(top = 5.dp, bottom = 10.dp, end = 20.dp)
                .align(Alignment.End)
                .clickable {
                    navController.navigate(DestinationScreen.ForgotPassword.route)
                }
        )
        Button(
            onClick = {
                focus.clearFocus(force = true)
                if (loginViewModel.validateData(userEmail, userPassword)) {
                    viewModel.onLogin(userEmail, userPassword)
                    SharedPreferencesHelper.saveCredentials(context, userEmail, userPassword)
                } else {
                    Toast.makeText(context, "Please review fields", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(15),
            contentPadding = PaddingValues(vertical = 14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.button_background_color)),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Log in", fontWeight = FontWeight.Bold)
        }
        Text(
            text = "Or continue with",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(id = R.color.button_background_color),
            modifier = Modifier
                .padding(top = 20.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialMediaLogin(
                icon = R.drawable.google_icon,
                text = "Google",
                modifier = Modifier.weight(1f)
            ) {
                scope.launch {
                    val signInIntentSender = googleAuthUiClient.signIn()
                    launcher.launch(
                        IntentSenderRequest
                            .Builder(signInIntentSender ?: return@launch)
                            .build()
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            SocialMediaLogin(
                icon = R.drawable.telephone,
                text = "Phone",
                modifier = Modifier.weight(1f)
            ) {
                /* TODO SIGN IN WITH PHONE NUMBER */
            }
        }
    }
    val isLoading = viewModel.inProgress.value
    if (isLoading) {
        CommonProgressSpinner()
    }

    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            navController.navigate(DestinationScreen.Feed.route)
            viewModel.resetState()
        }
    }
}

@Composable
fun SocialMediaLogin(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(LightBlueWhite)
            .clickable { onClick() }
            .height(40.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF64748B)), fontWeight = FontWeight.SemiBold)
    }
}