package com.example.sendbacksendbag

import HomeScreen // 실제 Composable import 필요
import Send // 실제 Composable import 필요
import Sended // 실제 Composable import 필요
import Sending // 실제 Composable import 필요
import SettingsScreen // 실제 Composable import 필요
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController // NavHostController import
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.sendbacksendbag.data.FriendsRepository
import com.example.sendbacksendbag.ui.friends.FriendsScreen
import com.example.sendbacksendbag.ui.login.AuthScreen
import com.example.sendbacksendbag.ui.profile.ProfileScreenContainer
import com.example.sendbacksendbag.ui.voting.PollScreen
import com.example.sendbacksendbag.authentication.AuthViewModel

// import com.example.sendbacksendbag.FeedbackViewModel // ViewModel import (실제 경로 확인)
// import com.example.sendbacksendbag.R // R import

@Composable
fun AppNavGraph(
    navController: NavHostController, // NavController를 파라미터로 받음
    authViewModel: AuthViewModel,     // AuthViewModel을 파라미터로 받음
    friendsRepository: FriendsRepository // FriendsRepository를 파라미터로 받음
) {
    // Repository의 StateFlow를 구독하여 실시간 데이터를 받습니다.
    val myProfile by friendsRepository.myProfile.collectAsState()
    val friendsList by friendsRepository.friends.collectAsState()
    val context = LocalContext.current
    // FeedbackViewModel (필요 시 실제 ViewModel 사용)
    // val feedbackViewModel: FeedbackViewModel = viewModel()
    LaunchedEffect(key1 = Unit) {
        authViewModel.init(context)
    }
    // 로그인 상태 확인 (AuthViewModel 사용)
    val authState by authViewModel.authState.collectAsState()
    // 로그인 상태에 따라 시작 화면 결정 (예: 로그인 안되어 있으면 "login", 되어 있으면 "home")
    val startDestination = remember(authState.isLoggedIn) {
        if (authState.isLoggedIn) "home" else "login"
    }


    NavHost(
        navController = navController,
        startDestination = startDestination // 동적 시작점 설정
    ) {
        composable("login") {
            AuthScreen(navController, authViewModel) // ViewModel 전달
        }
        composable("home") {
            HomeScreen( // 실제 HomeScreen Composable 사용
                navController = navController,
                notifications = listOf("박지열 님이 투표를 게시했습니다.", "나이병 님에게 메시지가 23시에 전송될 예정입니다.", "이승주 님에게 메시지가 23시에 전송될 예정입니다."), // 예시 데이터
                onSettingsClick = { navController.navigate("settings") },
                onReceivedClick = { navController.navigate("inbox") },
                onSentClick = { navController.navigate("send") }
            )
        }
        composable("send") {
            Send(navController) // 실제 Send Composable 사용
        }
        composable("sending") {
            Sending("박지열", "니 말만 하지 말고 상대방 말좀 들어. 짜증나게 맨날 자기 얘기만해;;; 말좀 끊지 말고 좀 제발;", navController) // 실제 Sending Composable 사용
        }
        composable("sended") {
            Sended(navController) // 실제 Sended Composable 사용
        }
        composable(
            route = "profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: "me"
            ProfileScreenContainer(
                navController = navController,
                userIdFromNav = userId,
                friendsRepository = friendsRepository
                // onMyProfileUpdate 콜백 제거: 이제 Repository 업데이트 시 Flow가 자동으로 반영합니다.
            )
        }
        composable("friends") {
            FriendsScreen(
                navController = navController,
                myProfile = myProfile, // StateFlow에서 온 최신 프로필 전달
                friends = friendsList, // StateFlow에서 온 최신 친구 목록 전달
                onFriendClick = { profile ->
                    navController.navigate("profile/${profile.id}") // null 체크 강화 또는 기본값 설정 필요
                },
                onAddFriendClick = { /* TODO: 친구 추가 로직 */ }
            )
        }
        composable("voting") {
            PollScreen(navController) // 실제 PollScreen Composable 사용
        }
        composable("inbox") {
            InboxScreen(navController = navController) // 실제 InboxScreen Composable 사용
        }
        composable("chat/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            // ChatScreen(navController = navController, userId = userId, feedbackViewModel = feedbackViewModel) // 실제 ChatScreen Composable 사용
        }
        composable("feedback/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val receiverProfile = friendsRepository.getFriendById(userId)
            val receiverName = receiverProfile?.name ?: "사용자"
            // FeedbackWriteScreen(navController = navController, receiverName = receiverName, feedbackViewModel = feedbackViewModel, userId = userId) // 실제 FeedbackWriteScreen Composable 사용
        }
        composable("settings") {
            SettingsScreen( // 실제 SettingsScreen Composable 사용
                navController = navController,
                onMenuClick = { /* 메뉴 클릭 */ },
                onSearch = { /* 검색 기능 */ },
                onAccountClick = { navController.navigate("profile/me") }, // 계정 클릭 시 내 프로필로 이동
                onItemClick = { item -> /* 아이템 클릭 */ }
            )
        }
    }
}