package com.example.sendbacksendbag

import HomeScreen // 실제 Composable import 필요
// 실제 Composable import 필요
import SettingsScreen // 실제 Composable import 필요
import android.content.Context
import android.util.Log
import HomeScreen
import SettingsScreen
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sendbacksendbag.data.FriendsRepository
import com.example.sendbacksendbag.ui.login.AuthScreen
import com.example.sendbacksendbag.authentication.AuthViewModel
import com.example.sendbacksendbag.ui.friends.AddFriendScreen
import com.example.sendbacksendbag.ui.friends.FriendsScreen
import com.example.sendbacksendbag.ui.friends.FriendsViewModel
import com.example.sendbacksendbag.ui.friends.FriendsViewModelFactory
import com.example.sendbacksendbag.ui.profile.ProfileScreenContainer
import com.example.sendbacksendbag.ui.voting.*

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
    val votingViewModel: VotingViewModel = viewModel(
        factory = VotingViewModelFactory(friendsRepository)
    )
    val messageViewModel = viewModel<MessageViewModel>() // MessageViewModel을 ViewModel로 사용
    val feedbackViewModel = viewModel<FeedbackViewModel>()
    val votingcontainerViewModel = viewModel<VotingContainerViewModel>() // VotingContainerViewModel을 ViewModel로 사용


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
    val friendsViewModel: FriendsViewModel = viewModel(
        factory = FriendsViewModelFactory(friendsRepository)
    )


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
            SendScreen(navController = navController, messageViewModel = messageViewModel)
        }
        composable(
            route = "sending/{receiverName}?sendingTime={sendingTime}",
            arguments = listOf(
                navArgument("receiverName") { type = NavType.StringType },
                navArgument("sendingTime") {
                    type = NavType.StringType
                    defaultValue = "20:00"
                }
            )
        ) { backStackEntry ->
            val receiverName = backStackEntry.arguments?.getString("receiverName") ?: "Unknown"
            val sendingTime = backStackEntry.arguments?.getString("sendingTime") ?: "20:00"

            Sending(
                userName = receiverName,
                message = "니 말만 하지 말고 상대방 말좀 들어. 짜증나게 맨날 자기 얘기만해;;; 말좀 끊지 말고 좀 제발;",
                navController = navController,
                messageViewModel = messageViewModel,
            )
        }
        composable(
            route = "sended/{messageId}",
            arguments = listOf(navArgument("messageId") { type = NavType.StringType })
        ) { backStackEntry ->
            val messageId = backStackEntry.arguments?.getString("messageId") ?: ""
            Sended(
                navHostController = navController,
                messageId = messageId,
                messageViewModel = messageViewModel
            )
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
                myProfile = myProfile,
                friends = friendsList,
                onFriendClick = { profile ->
                    navController.navigate("profile/${profile.id}")
                },
                // --- 친구 추가 버튼 클릭 시 addFriend 화면으로 이동 ---
                onAddFriendClick = { navController.navigate("addFriend") }
            )
        }
        composable("addFriend") {
            AddFriendScreen(
                navController = navController,
                friendsViewModel = friendsViewModel // 생성된 ViewModel 전달
            )
        }
        composable("voting") {
            PollListScreen(navController, votingViewModel = votingcontainerViewModel) // 예시 데이터 사용)
        }
        composable("inbox") {
            InboxScreen(navController = navController, votingcontainerViewModel = votingcontainerViewModel) // 실제 InboxScreen Composable 사용
        }
        composable("chat/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
             ChatScreen(navController = navController, userId = userId, feedbackViewModel = feedbackViewModel) // 실제 ChatScreen Composable 사용
        }
        composable("feedback/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val receiverProfile = friendsRepository.getFriendById(userId)
            val receiverName = receiverProfile?.name ?: "사용자"
            FeedbackWriteScreen(navController = navController, receiverName = receiverName, feedbackViewModel = feedbackViewModel) // 실제 FeedbackWriteScreen Composable 사용
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
        composable("mypoll") { // <<--- 새로운 경로 추가
            MyPollScreen(navController,votingViewModel) // MyPollScreen 연결
        }
        composable("poll/{pollId}/{pollContent}") { backStackEntry ->
            val pollContent = backStackEntry.arguments?.getString("pollContent") ?: ""
            val pollId = backStackEntry.arguments?.getString("pollId") ?: ""
            PollScreen(pollId,pollContent, navController, votingViewModel) // PollScreen 연결
        }
    }
}