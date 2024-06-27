package com.rohitneel.instagramclone.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.rohitneel.instagramclone.R
import com.rohitneel.instagramclone.common.CheckSignedIn
import com.rohitneel.instagramclone.common.CommonProgressSpinner
import com.rohitneel.instagramclone.common.CustomOutlinedTextField
import com.rohitneel.instagramclone.core.Constants.Companion.EMAIL_ERROR
import com.rohitneel.instagramclone.core.Constants.Companion.PASSWORD_ERROR
import com.rohitneel.instagramclone.core.Constants.Companion.USER_NAME_ERROR
import com.rohitneel.instagramclone.util.SharedPreferencesHelper
import com.rohitneel.instagramclone.viewmodel.InstagramViewModel
import com.rohitneel.instagramclone.viewmodel.SignupViewModel

@Composable
fun SignupScreen(navController: NavController, viewModel: InstagramViewModel) {

    CheckSignedIn(navController = navController, viewModel = viewModel)

    val focus = LocalFocusManager.current
    val context = LocalContext.current
    val signupViewModel = viewModel<SignupViewModel>()
    var userName by rememberSaveable { mutableStateOf("") }
    var userEmail by rememberSaveable { mutableStateOf("") }
    var userPassword by rememberSaveable { mutableStateOf("") }

    val isUserNameValid by signupViewModel.userNameValidation.observeAsState(initial = true)
    val isEmailValid by signupViewModel.emailValidation.observeAsState(initial = true)
    val isPasswordValid by signupViewModel.passwordValidation.observeAsState(initial = true)

    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomOutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = "Username",
            showError = !isUserNameValid,
            errorMessage = USER_NAME_ERROR,
            leadingIcon = Icons.Filled.AccountCircle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focus.moveFocus(FocusDirection.Down)}
            )
        )

        CustomOutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = "Email",
            showError = !isEmailValid,
            errorMessage = EMAIL_ERROR,
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
            errorMessage = PASSWORD_ERROR,
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

        Button(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            onClick = {
                focus.clearFocus(force = true)
                if (signupViewModel.validateData(userName, userEmail, userPassword)) {
                    viewModel.onSignup(
                        userName = userName,
                        email = userEmail,
                        password = userPassword
                    )
                    SharedPreferencesHelper.saveCredentials(context, userEmail, userPassword)
                } else {
                    Toast.makeText(context, "Please review fields", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(15),
            contentPadding = PaddingValues(vertical = 14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.button_background_color))
        ) {
            Text(text = "Create new account", fontWeight = FontWeight.Bold)
        }
    }
    val isLoading = viewModel.inProgress.value
    if (isLoading) {
        CommonProgressSpinner()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignupScreenPreview() {
    val navController = rememberNavController()
    val viewModel = InstagramViewModel(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance()
    )
    SignupScreen(navController, viewModel)
}