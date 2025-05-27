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
import androidx.compose.material3.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.border
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf



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
fun InboxScreen(navController: NavController) {
    val context = LocalContext.current
    val sampleMessages = listOf(
        Message(
            id = "rabbit",
            name = "잠만 자는 토끼",
            avatarRes = R.drawable.example2,
            content = "메시지가 도착했습니다!",
            time = "8m ago",
            hasActionButton = true
        ),
        Message(
            id = "horse",
            name = "코딩하는 말",
            avatarRes = R.drawable.example2,
            content = "메시지가 도착했습니다!",
            time = "8m ago"
        ),
        Message(
            id = "otter",
            name = "배 긁고 있는 수달",
            avatarRes = R.drawable.example2,
            content = "메시지가 도착했습니다!",
            time = "8m ago"
        ),
        Message(
            id = "badger",
            name = "춤을 추는 오소리",
            avatarRes = R.drawable.example2,
            content = "메시지가 도착했습니다!",
            time = "8m ago"
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text(text = "받은 메시지", fontWeight = FontWeight.Black) },
                actions = {
                    androidx.compose.material3.IconButton(onClick = {navController.navigate("settings")}) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(30.dp)

                        )
                    }
                }
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
                .padding(16.dp),
            navController = navController
        )
    }
}

@Composable
fun MessageItemWithButton(message: Message, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ⚡️ 설정 아이콘 제거 후, 밝은 하늘색 원으로 대체
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFAFDAFF)) // 밝은 하늘색
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
fun ChatScreen(navController: NavController, userId: String, feedbackViewModel: FeedbackViewModel = viewModel()) {
    val context = LocalContext.current
    var isLiked by remember { mutableStateOf(false) }
    var isDisliked by remember { mutableStateOf(false) }
    val userFeedback = feedbackViewModel.userFeedback.value

    // 기본 메시지 및 사용자 피드백 메시지 추가
    val chatMessages = remember(userFeedback) {
        val initialMessages = when (userId) {
            "rabbit" -> listOf(
                ChatMessage(
                    content = "네 말도 중요하지만 상대의 말이 끝난 다음에 이야기\n 해주면 소통이 더 잘 될 것 같아.\n 상대방의 말을 조금만 더 들어줬으면 좋겠어.",
                    isFromMe = false,
                    time = ""
                )
            )
            else -> listOf(
                ChatMessage(
                    content = "안녕하세요! 메시지를 확인해주세요.",
                    isFromMe = false,
                    time = ""
                )
            )
        }.toMutableList()

        // 사용자가 피드백을 작성했으면 메시지 목록에 추가
        userFeedback?.let {
            initialMessages.add(
                ChatMessage(
                    content = it,
                    isFromMe = true,
                    time = ""
                )
            )
        }

        initialMessages
    }

    val userName = when (userId) {
        "rabbit" -> "잠만 자는 토끼"
        "horse" -> "코딩하는 말"
        "otter" -> "배 긁고 있는 수달"
        "badger" -> "춤을 추는 오소리"
        else -> "사용자"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0FA))
        ) {
            // Top App Bar with back button (설정 버튼 제거)
            androidx.compose.material3.TopAppBar(
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE6F0FA),
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )

            // 상단바와 채팅 메시지 사이에 공백 추가
            Spacer(modifier = Modifier.height(24.dp))

            // Chat messages
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatMessages) { message ->
                    ChatMessageItem(message)
                }

                // 피드백이 없을 때만 피드백 평가 카드 표시
                if (userId == "rabbit" && userFeedback == null) {
                    item {
                        Spacer(modifier = Modifier.height(170.dp))

                        // 피드백 평가 카드 표시
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
                // 피드백이 있을 때는 평가 카드와 피드백 남기기 버튼 모두 표시하지 않음
            }
        }

        // 오른쪽 하단에 설정 FAB 추가
        ExpandableFabExample(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            navController = navController,
            onEmailClicked = {
                // 피드백을 null로 설정하여 초기 상태로 돌아가게 함
                feedbackViewModel.resetFeedback()
            }
        )
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF5EA7FF),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}

@Composable
fun FeedbackRatingCard(
    isLiked: Boolean,
    isDisliked: Boolean,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onFeedbackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 피드백 평가 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFAFDAFF),
            shape = RoundedCornerShape(12.dp),
            elevation = 0.dp,
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
            }
        }

        // 카드 밖 오른쪽 하단에 피드백 남기기 버튼 배치
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, end = 4.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            TextButton(
                onClick = onFeedbackClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF5EA7FF)
                ),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "피드백 남기기",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    // 특수 메시지 플래그
    val isSpecial = message.isFromMe && message.content == "네 말을 끝까지 듣도록 노력할게"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        // 항상 우측 끝에 붙이기
        contentAlignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            // special 메시지는 좌우 패딩을 줄여서 오른쪽으로 더 붙이기
            modifier = Modifier
                .padding(
                    start  = if (isSpecial) 32.dp else 16.dp,
                    end    = 16.dp
                )
        ) {
            // --- 좌측 아바타: 내 메시지가 아니면 보여주기 ---
            if (!message.isFromMe) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFE680), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // 메시지 버블 + 시간
            Column(
                horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = when {
                                isSpecial        -> Color.White
                                message.isFromMe -> Color(0xFF5EA7FF)
                                else             -> Color.White
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = message.content,
                        fontSize = 14.sp,
                        color = when {
                            isSpecial        -> Color.Black
                            message.isFromMe -> Color.White
                            else             -> Color.Black
                        }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.time,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            // --- 특수 메시지일 때만 우측 아바타 표시 ---
            if (isSpecial) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFE680), CircleShape)
                )
            }
            // 일반 내 메시지는 여분 공간 확보
            else if (message.isFromMe) {
                Spacer(modifier = Modifier.width(32.dp))
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
fun ExpandableFabExample(
    modifier: Modifier = Modifier,
    navController: NavController,
    onEmailClicked: () -> Unit = {}
) {
    val context = LocalContext.current
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
                MiniFab(icon = Icons.Default.Home, onClick = {navController.navigate("home")})
                MiniFab(icon = Icons.AutoMirrored.Filled.Send, onClick = {navController.navigate("send")})
                MiniFab(icon = Icons.Default.Email, onClick = {
                    // 피드백 초기화
                    onEmailClicked()

                    if (navController != null) {
                        navController.navigate("inbox") {
                            // 백 스택 정리
                            popUpTo("inbox") {
                                inclusive = true
                            }
                        }
                    } else {
                        val intent = Intent(context, Back::class.java)
                        intent.putExtra("screenType", "inbox")
                        context.startActivity(intent)
                        (context as? ComponentActivity)?.finish()
                    }
                })
                MiniFab(icon = Icons.Default.HowToVote, onClick = {navController.navigate("voting")})
                MiniFab(icon = Icons.Default.Person, onClick = {navController.navigate("friends")})
            }
        }
        FloatingActionButton(
            onClick = { expanded = !expanded },
            backgroundColor = Color.LightGray  // 배경색을 회색으로 변경
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.MoreVert,
                contentDescription = null,
                tint = Color.Black
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
        containerColor = Color.LightGray,  // 미니 FAB 배경색도 회색으로 변경
        contentColor = Color.Black  // 미니 FAB 아이콘 색상도 검정색으로 변경
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
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
                val navController = rememberNavController()
                val userId = intent.getStringExtra("userId") ?: "rabbit"
                val screenType = intent.getStringExtra("screenType") ?: "chat"
                val feedbackViewModel = viewModel<FeedbackViewModel>()

                when (screenType) {
                    "inbox" -> InboxScreen(navController)
                    "chat" -> ChatScreen(navController, userId = userId, feedbackViewModel = feedbackViewModel)
                    "feedback" -> {
                        val receiverName = intent.getStringExtra("receiverName") ?: ""
                        FeedbackWriteScreen(navController, receiverName = receiverName, feedbackViewModel = feedbackViewModel)
                    }
                }
            }
        }
    }
}

// FeedbackViewModel에 피드백 초기화 함수 추가
class FeedbackViewModel : ViewModel() {
    private val _userFeedback = mutableStateOf<String?>(null)
    val userFeedback: State<String?> = _userFeedback

    fun saveFeedback(feedback: String) {
        _userFeedback.value = "네 말을 끝까지 듣도록 노력할게"
    }

    // 피드백을 초기 상태로 재설정하는 함수 추가
    fun resetFeedback() {
        _userFeedback.value = null
    }
}

@Composable
fun FeedbackWriteScreen(navController: NavController, receiverName: String,  feedbackViewModel: FeedbackViewModel = viewModel()) {
    val context = LocalContext.current
    var feedbackText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE6F0FA))
        ) {
            // 상단 앱바는 기존과 동일하게 유지
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

            Spacer(modifier = Modifier.height(50.dp))

            // 피드백 작성 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 대상 텍스트
                Text(
                    text = "잠만 자는 토끼에게 하고 싶은 말",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center
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

                Spacer(modifier = Modifier.height(20.dp))

                // 전송 버튼 영역 (피드백 남기기 버튼과 동일한 스타일로 변경)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(
                        onClick = {
                            // 피드백 저장 후 이전 화면으로 돌아가기
                            if (feedbackText.isNotEmpty()) {
                                feedbackViewModel.saveFeedback(feedbackText)
                            }
                            if (navController != null) {
                                navController.popBackStack()
                            } else {
                                (context as? ComponentActivity)?.finish()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF5EA7FF)
                        ),
                        modifier = Modifier.border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                    ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "send",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 오른쪽 하단에 설정 FAB 추가
        ExpandableFabExample(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            navController = navController
        )
    }
}