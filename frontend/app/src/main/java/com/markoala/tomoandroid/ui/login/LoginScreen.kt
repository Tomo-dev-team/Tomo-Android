package com.markoala.tomoandroid.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.markoala.tomoandroid.auth.CredentialSignInScreen

@Composable
fun LoginScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "로그인 페이지")
        CredentialSignInScreen(
            onSignedIn = {
                navController.navigate("profile") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
}
