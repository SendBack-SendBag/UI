import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sendbacksendbag.*
import com.example.sendbacksendbag.ui.friends.FriendsScreen
import com.example.sendbacksendbag.ui.login.AuthScreen
import com.example.sendbacksendbag.ui.profile.ProfileData
import com.example.sendbacksendbag.ui.profile.ProfileScreenContainer
import com.example.sendbacksendbag.ui.voting.PollScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val feedbackViewModel = viewModel<FeedbackViewModel>()
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login"){
            AuthScreen(navController)
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                notifications = listOf("박지열 님이 투표를 게시했습니다.", "나이병 님에게 메시지가 23시에 전송될 예정입니다.", "이승주 님에게 메시지가 23시에 전송될 예정입니다."),
                onSettingsClick = { navController.navigate("settings") },
                onReceivedClick = { navController.navigate("inbox") },
                onSentClick = { navController.navigate("send") }
            )
        }
        composable("send") {
            Send(navController)
        }
        composable("sending"){
            Sending("박지열","니 말만 하지 말고 상대방 말좀 들어. 짜증나게 맨날 자기 얘기만해;;; 말좀 끊지 말고 좀 제발;",navController)
        }
        composable("sended") {
            Sended(navController)
        }
        composable("profile"){
            ProfileScreenContainer(navController, "data")
        }
        composable("friends"){
            FriendsScreen(
                navController = navController,
                myProfile = ProfileData(
                    id = "me",
                    name = "김승우",
                    messageArrivalTime = "20 : 00",
                    profileImageUri = null, // Uri.parse("...") 또는 null
                    placeholderImageRes = R.drawable.example_picture // R.drawable.kim_seung_woo 등 실제 이미지 리소스 사용
                ),
                friends = listOf(
                    ProfileData(
                        id = "friend1",
                        name = "박지열",
                        messageArrivalTime = "20 : 00",
                        profileImageUri = null,
                        placeholderImageRes = R.drawable.example_picture // R.drawable.park_ji_yeol
                    ),
                    ProfileData(
                        id = "friend2",
                        name = "원숭이",
                        messageArrivalTime = "20 : 00",
                        profileImageUri = null,
                        placeholderImageRes = R.drawable.example_picture // R.drawable.won_soong_i
                    ),
                    ProfileData(
                        id = "friend3",
                        name = "이승주",
                        messageArrivalTime = "20 : 00",
                        profileImageUri = null,
                        placeholderImageRes = R.drawable.example_picture // R.drawable.lee_seung_ju
                    ),
                    ProfileData(
                        id = "friend4",
                        name = "나이병",
                        messageArrivalTime = "20 : 00",
                        profileImageUri = null,
                        placeholderImageRes = R.drawable.example_picture // R.drawable.na_i_byeong
                    )
                ),
                onFriendClick = { profile ->
                    println("Clicked on: ${profile.name}") // 클릭 시 로그 출력 (네비게이션 로직 대체)
                },
                onAddFriendClick = {
                    println("Add Friend Clicked!") // 친구 추가 버튼 클릭 시 로그 출력
                }
            )
        }
        composable("voting"){
            PollScreen(navController)
        }
        composable("inbox") {
            InboxScreen(navController = navController)
        }
        composable("chat/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ChatScreen(navController = navController, userId = userId, feedbackViewModel = feedbackViewModel)
        }
        composable("feedback/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val receiverName = when (userId) {
                "rabbit" -> "잠만 자는 토끼"
                "horse" -> "코딩하는 말"
                "otter" -> "배 긁고 있는 수달"
                "badger" -> "춤을 추는 오소리"
                else -> "사용자"
            }
            FeedbackWriteScreen(navController = navController, receiverName = receiverName, feedbackViewModel = feedbackViewModel)
        }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                onMenuClick = { /* 메뉴 클릭 */ },
                onSearch = { /* 검색 기능 */ },
                onAccountClick = { /* 계정 클릭 */ },
                onItemClick = { item -> /* 아이템 클릭 */ }
            )
        }
    }
}