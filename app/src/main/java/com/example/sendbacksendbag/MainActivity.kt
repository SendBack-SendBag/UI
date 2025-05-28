package com.example.sendbacksendbag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.sendbacksendbag.data.FriendsRepository
import com.example.sendbacksendbag.ui.friends.FriendsScreen
import com.example.sendbacksendbag.ui.profile.ProfileScreenContainer
import com.example.sendbacksendbag.ui.theme.SendBackSendBagTheme
import com.example.sendbacksendbag.ui.login.AuthScreen // 로그인 화면 import
import com.example.sendbacksendbag.authentication.AuthViewModel // ViewModel import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SendBackSendBagTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // FriendsRepository 인스턴스 생성 (Hilt나 ViewModelFactory 사용 권장)
                    val context = LocalContext.current
                    val friendsRepository = remember { FriendsRepository(context) }
                    val authViewModel: AuthViewModel = viewModel() // AuthViewModel 가져오기

                    AppNavGraph(
                        navController = rememberNavController(),
                        authViewModel = authViewModel, // ViewModel 전달
                        friendsRepository = friendsRepository // Repository 전달
                    )
                }
            }
        }
    }
}
