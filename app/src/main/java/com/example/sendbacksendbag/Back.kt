package com.example.sendbacksendbag

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sendbacksendbag.ui.theme.SendBackSendBagTheme
import com.example.sendbacksendbag.ui.voting.Poll
import kotlinx.coroutines.launch


// 채팅 메시지 데이터 클래스
data class ChatMessage(
    val content: String,
    val isFromMe: Boolean,
    val time: String,
    val isSpecialFormat: Boolean = false  // 기본값을 false로 설정
)

@Composable
fun InboxScreen(navController: NavController, messageViewModel: MessageViewModel = viewModel(), votingcontainerViewModel: VotingContainerViewModel = viewModel()) {
    val receivedMessages by messageViewModel.receivedMessages.collectAsState()
    var searchText by remember { mutableStateOf("") }

    val messageSent by messageViewModel.messageSent.collectAsState()

    LaunchedEffect(Unit) {
        messageViewModel.loadAllMessages()
    }

    // 메시지 전송 완료 시 추가 새로고침
    LaunchedEffect(messageSent) {
        if (messageSent) {
            messageViewModel.loadAllMessages()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text(text = "받은 메시지", fontWeight = FontWeight.Black, fontSize = 30.sp) },
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

            // 전송된 메시지만 표시 (status가 "전송됨"인 것만)
            LazyColumn {
                items(receivedMessages.filter {
                    it.status == "전송됨" &&

                    (it.anonymousName.contains(searchText, true) ||
                    it.transformedContent.contains(searchText, true))
                }) { message ->
                    // 익명 이름으로 메시지 표시
                    AnonymousMessageItem(
                        navController = navController,
                        message = message,
                        onClick = {
                            navController.navigate("chat/${message.id}")
                        },
                        votingcontainerViewModel
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

// 익명 이름을 사용하는 메시지 아이템 컴포넌트
@Composable
fun AnonymousMessageItem(
    navController: NavController,
    message: Message,
    onClick: () -> Unit,
    votingViewModel: VotingContainerViewModel// ViewModel 추가
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedMessage by remember { mutableStateOf<Message?>(null) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(

            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFAFDAFF)) // 밝은 하늘색
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // 익명 이름과 시간 표시
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.anonymousName,  // 익명 이름 사용
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 변환된 메시지 내용 표시
            Text(
                text = "피드백이 도착했습니다!",
                fontSize = 12.sp,
                color = Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                        .clickable { showDialog = true; selectedMessage = message } // 클릭 시 다이얼로그 표시
                )
            }
        }
    }

    if (showDialog && selectedMessage != null) {
        AlertDialog(
            onDismissRequest = {
                // 다이얼로그 외부를 누르거나 백버튼을 눌렀을 때
                showDialog = false
                selectedMessage = null
            },
            title = {
                Text(text = "투표 확인")
            },
            text = {
                Text(text = "정말로 투표 올리시겠습니까?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // "확인" 버튼을 눌렀을 때: 투표 생성 및 네비게이션
                        selectedMessage?.let { msg ->
                            // 메시지 내용으로 새 투표 생성
                            val newPoll = Poll(
                                id = msg.name,
                                title = "${msg.anonymousName}의 피드백에 대한 투표",
                                subtitle = "피드백 내용에 대한 의견을 투표해주세요.",
                                content = msg.transformedContent
                            )
                            // ViewModel에 추가
                            votingViewModel.addPoll(newPoll)
                        }
                        // 투표 화면으로 이동
                        navController.navigate("voting")
                        // 다이얼로그 닫기
                        showDialog = false
                        selectedMessage = null
                    }
                ) {
                    Text(text = "확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // "취소" 버튼을 눌렀을 때: 다이얼로그만 닫기
                        showDialog = false
                        selectedMessage = null
                    }
                ) {
                    Text(text = "취소")
                }
            }
        )
    }

}



@Composable
fun ChatScreen(navController: NavController, userId: String, feedbackViewModel: FeedbackViewModel = viewModel()) {
    val context = LocalContext.current
    var isLiked by remember { mutableStateOf(false) }
    var isDisliked by remember { mutableStateOf(false) }
    val userFeedback = feedbackViewModel.userFeedback.value

    // messageViewModel에서 해당 userId에 대한 메시지 가져오기
    val messageViewModel = viewModel<MessageViewModel>()
    val messages by messageViewModel.messages.collectAsState()
    val message = messages.find { it.id == userId }

    // 해당 메시지의 anonymousName을 사용
    val userName = message?.anonymousName ?: "익명의 사용자"
    val transformedMessage = message?.transformedContent

    // 기본 메시지 및 사용자 피드백 메시지 추가
    val chatMessages = remember(userFeedback, transformedMessage) {
        val initialMessages = when (userId) {
            "rabbit" -> listOf(
                ChatMessage(
                    content = "네 말도 중요하지만 상대의 말이 끝난 다음에 이야기\n 해주면 소통이 더 잘 될 것 같아.\n 상대방의 말을 조금만 더 들어줬으면 좋겠어.",
                    isFromMe = false,
                    time = ""
                )
            )
            else -> {
                if (transformedMessage != null) {
                    listOf(
                        ChatMessage(
                            content = transformedMessage,  // 변환된 메시지 표시
                            isFromMe = false,
                            time = ""
                        )
                    )
                } else {
                    listOf(
                        ChatMessage(
                            content = "안녕하세요! 메시지를 확인해주세요.",
                            isFromMe = false,
                            time = ""
                        )
                    )
                }
            }
        }.toMutableList()

        // 사용자가 피드백을 작성했으면 메시지 목록에 추가
        userFeedback?.let {
            initialMessages.add(
                ChatMessage(
                    content = it,
                    isFromMe = true,
                    time = "",
                    isSpecialFormat = true  // 피드백 메시지는 특수 포맷으로 설정
                )
            )
        }

        initialMessages
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0FA))
        ) {
            // Top App Bar with back button (설정 버튼 제거)
            TopAppBar(
                title = {
                    Text(
                        text = userName,  // 익명 이름 사용
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
                if (userFeedback == null) {
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
                                navController.navigate("feedback/${userId}") {
                                    // 백 스택 정리
                                    popUpTo("chat/${userId}") {
                                        inclusive = true
                                    }
                                }
                            } else {
                                val intent = Intent(context, Back::class.java)
                                intent.putExtra("screenType", "feedback")
                                intent.putExtra("receiverName", userName)  // 익명 이름 전달
                                context.startActivity(intent)
                            }
                        }
                    )
                }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        // 항상 우측 끝에 붙이기
        contentAlignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.widthIn(max = 280.dp) // 메시지 최대 너비 제한
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
                horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start,
                modifier = Modifier.weight(1f, fill = false) // 필요한 크기만 차지하도록 설정
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = when {
                                message.isSpecialFormat -> Color.White
                                message.isFromMe -> Color(0xFF5EA7FF)
                                else -> Color.White
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = message.content,
                        fontSize = 14.sp,
                        color = when {
                            message.isSpecialFormat -> Color.Black
                            message.isFromMe -> Color.White
                            else -> Color.Black
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

            // 내가 보낸 메시지일 때는 항상 노란색 원 표시 (특수 포맷 여부 상관없이)
            if (message.isFromMe) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFE680), CircleShape)
                )
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
                val votingcontainerViewModel = viewModel<VotingContainerViewModel>()

                when (screenType) {
                    "inbox" -> InboxScreen(navController,  votingcontainerViewModel = votingcontainerViewModel)
                    "chat" -> ChatScreen(navController, userId = userId, feedbackViewModel = feedbackViewModel)
                    "feedback" -> {
                        val receiverName = intent.getStringExtra("receiverName") ?: ""
                        FeedbackWriteScreen(
                            navController,
                            receiverName = receiverName,
                            feedbackViewModel = feedbackViewModel
                        )
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

    // 로딩 상태를 관리하기 위한 변수 추가
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Gemini API를 사용하여 피드백을 변환하고 저장하는 함수
    fun processFeedback(originalFeedback: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // GeminiTranslator를 사용하여 피드백 변환
                val transformedFeedback = com.example.sendbacksendbag.communication.GeminiTranslator.generateComment(originalFeedback)
                _userFeedback.value = transformedFeedback
            } catch (e: Exception) {
                Log.e("FeedbackViewModel", "Error processing feedback: ${e.message}", e)
                _userFeedback.value = "네 말을 끝까지 듣도록 노력할게" // 오류 시 기본 응답 제공
            } finally {
                _isLoading.value = false
            }
        }
    }

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
                    text = "${receiverName}에게 하고 싶은 말",
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
                                feedbackViewModel.processFeedback(feedbackText)
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