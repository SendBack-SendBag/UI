package com.example.sendbacksendbag

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.sendbacksendbag.ui.theme.SendBackSendBagTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.ui.platform.LocalContext


// 메시지 데이터 클래스
data class Message(
    val id: String,
    val name: String,
    val avatarRes: Int,
    val content: String,
    val time: String,
    val hasActionButton: Boolean = false
)

// 채팅 메시지 데이터 클래스
data class ChatMessage(
    val content: String,
    val isFromMe: Boolean,
    val time: String
)

@Composable
fun MainApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "inbox") {
        composable("inbox") {
            InboxScreen(navController = navController)
        }
        composable("chat/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ChatScreen(navController = navController, userId = userId)
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
            FeedbackWriteScreen(navController = navController, receiverName = receiverName)
        }
    }
}

@Composable
fun InboxScreen(navController: NavController) {
    val context = LocalContext.current
    val sampleMessages = listOf(
        Message(
            id = "rabbit",
            name = "잠만 자는 토끼",
            avatarRes = R.drawable.example,
            content = "메시지가 도착했습니다!",
            time = "8m ago",
            hasActionButton = true
        ),
        Message(
            id = "horse",
            name = "코딩하는 말",
            avatarRes = R.drawable.example,
            content = "메시지가 도착했습니다!",
            time = "8m ago"
        ),
        Message(
            id = "otter",
            name = "배 긁고 있는 수달",
            avatarRes = R.drawable.example,
            content = "메시지가 도착했습니다!",
            time = "8m ago"
        ),
        Message(
            id = "badger",
            name = "춤을 추는 오소리",
            avatarRes = R.drawable.example,
            content = "메시지가 도착했습니다!",
            time = "8m ago"
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "받은 메시지",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                },
                backgroundColor = Color.White,
                elevation = 0.dp
            )
            BlackHorizontalLine()
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(sampleMessages) { message ->
                    MessageItemWithButton(
                        message = message,
                        onClick = {
                            if (navController != null) {
                                navController.navigate("chat/${message.id}")
                            } else {
                                val intent = android.content.Intent(context, Back::class.java)
                                intent.putExtra("userId", message.id)
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }

        ExpandableFabExample(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
fun MessageItemWithButton(message: Message, onClick: () -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = message.avatarRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE6F0FA)) // 연한 블루 느낌 배경
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = message.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = message.content,
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = message.time,
                fontSize = 10.sp,
                color = Color.Gray
            )
            if (message.hasActionButton) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "투표 올리기",
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0xFF5EA7FF), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ChatScreen(navController: NavController?, userId: String) {
    val context = LocalContext.current
    var isLiked by remember { mutableStateOf(false) }
    var isDisliked by remember { mutableStateOf(false) }
    val chatMessages = remember {
        when (userId) {
            "rabbit" -> listOf(
                ChatMessage(
                    content = "네 말도 중요하지만 상대의 말이 끝난 다음에 이야기\n 해주면 소통이 더 잘 될 것 같아.\n 상대방의 말을 조금만 더 들어줬으면 좋겠어.",
                    isFromMe = false,
                    time = "오후 2:34"
                )
            )
            else -> listOf(
                ChatMessage(
                    content = "안녕하세요! 메시지를 확인해주세요.",
                    isFromMe = false,
                    time = "오후 2:30"
                )
            )
        }
    }

    val userName = when (userId) {
        "rabbit" -> "잠만 자는 토끼"
        "horse" -> "코딩하는 말"
        "otter" -> "배 긁고 있는 수달"
        "badger" -> "춤을 추는 오소리"
        else -> "사용자"
    }

    // 피드백 상태는 위에서 선언했으므로 중복 선언 제거

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFE6F0FA))
    ) {
        // Top App Bar with back button
        TopAppBar(
            title = {
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (navController != null) {
                        navController.popBackStack()
                    } else {
                        (context as? ComponentActivity)?.finish()
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            backgroundColor = Color.White,
            elevation = 0.dp
        )

        // Chat messages
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatMessages) { message ->
                ChatMessageItem(message)
            }

            if (userId == "rabbit") {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    FeedbackRatingCard(
                        isLiked = isLiked,
                        isDisliked = isDisliked,
                        onLikeClick = {
                            isLiked = !isLiked
                            if (isLiked) isDisliked = false
                        },
                        onDislikeClick = {
                            isDisliked = !isDisliked
                            if (isDisliked) isLiked = false
                        },
                        onFeedbackClick = {
                            // 피드백 작성 화면으로 이동
                            if (navController != null) {
                                navController.navigate("feedback/rabbit")
                            } else {
                                val intent = Intent(context, Back::class.java)
                                intent.putExtra("screenType", "feedback")
                                intent.putExtra("receiverName", "잠만 자는 토끼")
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }



        // Bottom message input (placeholder, not functional)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("메시지를 입력하세요") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF5EA7FF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun FeedbackRatingCard(
    isLiked: Boolean,
    isDisliked: Boolean,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onFeedbackClick: () -> Unit  // 피드백 남기기 버튼 클릭 핸들러 추가
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFAFDAFF),
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "이 피드백에 대한 평가를 남겨주세요!",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 좋아요 버튼
                IconButton(
                    onClick = onLikeClick,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = if (isLiked) Color(0xFF5EA7FF) else Color.White,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ThumbUp,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 싫어요 버튼
                IconButton(
                    onClick = onDislikeClick,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = if (isDisliked) Color(0xFF5EA7FF) else Color.White,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ThumbDown,
                        contentDescription = "Dislike",
                        tint = if (isDisliked) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 카드 하단에 피드백 남기기 버튼 추가
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                TextButton(
                    onClick = onFeedbackClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF5EA7FF)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "피드백 작성",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "피드백 남기기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 메시지가 내 것이 아닐 때만 원을 표시
            if (!message.isFromMe) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Yellow, CircleShape)
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (message.isFromMe) Color(0xFF5EA7FF) else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = message.content,
                        color = if (message.isFromMe) Color.White else Color.Black,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message.time,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            // 메시지가 내 것일 때만 오른쪽에 공간 확보
            if (message.isFromMe) {
                Spacer(modifier = Modifier.width(32.dp)) // 원 크기에 맞춘 공간
            }
        }
    }
}

@Composable
fun BlackHorizontalLine() {
    Divider(
        color = Color.Black,
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}

@Composable
fun ExpandableFabExample(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniFab(icon = Icons.Default.Settings, onClick = {})
                MiniFab(icon = Icons.AutoMirrored.Filled.Send, onClick = {})
                MiniFab(icon = Icons.Default.Email, onClick = {})
                MiniFab(icon = Icons.Default.Lock, onClick = {})
                MiniFab(icon = Icons.Default.Person, onClick = {})
            }
        }
        FloatingActionButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.MoreVert,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun MiniFab(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    // Material 3의 FloatingActionButton 사용
    androidx.compose.material3.FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}

// ActivityMain에서 사용할 수 있는 앱의 진입점
@Composable
fun AppEntryPoint() {
    MaterialTheme {
        MainApp()
    }
}

/**
 * Back 액티비티 클래스
 */
class Back : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SendBackSendBagTheme {
                val userId = intent.getStringExtra("userId") ?: "rabbit"
                val screenType = intent.getStringExtra("screenType") ?: "chat"

                when (screenType) {
                    "chat" -> ChatScreen(navController = null, userId = userId)
                    "feedback" -> {
                        val receiverName = intent.getStringExtra("receiverName") ?: ""
                        FeedbackWriteScreen(navController = null, receiverName = receiverName)
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackWriteScreen(navController: NavController?, receiverName: String) {
    val context = LocalContext.current
    var feedbackText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0FA))
    ) {
        // 상단 앱바
        TopAppBar(
            title = {
                Text(
                    text = "피드백 작성",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (navController != null) {
                        navController.popBackStack()
                    } else {
                        (context as? ComponentActivity)?.finish()
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            backgroundColor = Color.White,
            elevation = 0.dp
        )

        // 피드백 작성 영역
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .weight(1f)
        ) {
            // 대상 텍스트
            Text(
                text = "잠만 자는 토끼에게 하고 싶은 말",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 피드백 입력 필드
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color.White
            ) {
                TextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    placeholder = { Text("상대방에게 피드백을 작성해주세요.") },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 제출 버튼
            Button(
                onClick = {
                    // 피드백 제출 (여기서는 이전 화면으로 돌아가는 기능만 구현)
                    if (navController != null) {
                        navController.popBackStack()
                    } else {
                        (context as? ComponentActivity)?.finish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5EA7FF))
            ) {
                Text(
                    text = "피드백 보내기",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}