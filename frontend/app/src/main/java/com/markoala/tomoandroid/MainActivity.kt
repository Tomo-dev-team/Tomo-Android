package com.markoala.tomoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.navigation.AppNavHost
import com.markoala.tomoandroid.ui.components.ToastProvider
import com.markoala.tomoandroid.ui.theme.TomoAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AuthManager.init()
        AuthManager.initTokenManager(this)

        setContent {
            TomoAndroidTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var signedIn by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    // 앱 시작 시 저장된 토큰 확인
    LaunchedEffect(Unit) {
        signedIn = AuthManager.hasValidTokens()
    }

    ToastProvider {
        // 로그인/로그아웃 시 signedIn 상태 변경
        AppNavHost(navController = navController, isSignedIn = signedIn)
    }
}
