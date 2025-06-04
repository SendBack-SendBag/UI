package com.example.sendbacksendbag.ui.voting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.BottomSheetDefaults
import androidx.lifecycle.viewmodel.compose.viewModel // ViewModel 의존성 추가
import androidx.navigation.NavController
import com.example.sendbacksendbag.ui.messages.ExpandableFabExample
import com.example.sendbacksendbag.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollScreen(name:String,navController: NavController, viewModel: VotingViewModel = viewModel()) { // ViewModel 주입
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()    
    Box {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD6E9FA)),
            topBar = {
                TopAppBar(
                    title = { /* 현재 화면에서는 제목이 TopAppBar에 없음 */ },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* 검색 동작 */ }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "검색"
                            )
                        }
                        IconButton(onClick = { /* 북마크 동작 */ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.bookmark),
                                contentDescription = "북마크"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFD6E9FA),
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            containerColor = Color(0xFFD6E9FA)
        ) { innerPadding ->
            PollContent(name,Modifier.padding(innerPadding), viewModel)// ViewModel 전달
        }
        ExpandableFabExample(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            navController = navController
        )
    }
    if (showBottomSheet) {
        CommentBottomSheet(
            sheetState = sheetState,
            viewModel = viewModel, // ViewModel 전달
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollContent(name:String,modifier: Modifier = Modifier, viewModel: VotingViewModel) { // ViewModel 받기
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "오늘의 투표",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        Row {
            Spacer(modifier = Modifier.padding(10.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFE0E0E0).copy(alpha = 0.7f),
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(bottom = 24.dp)
            ) {
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = name+"님이 게시한 투표",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        FeedbackCard(name,
            onChatIconClick = {
                scope.launch {
                    showBottomSheet = true
                }
            }
        )
    }

    if (showBottomSheet) {
        CommentBottomSheet( // ViewModel 전달
            sheetState = sheetState,
            viewModel = viewModel,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }
        )
    }
}

@Composable
fun FeedbackCard(name:String,
    onChatIconClick: () -> Unit) {
    var selectedOption by remember { mutableStateOf<PollOption?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = name+"님이 받은 피드백",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "상대방의 말을 조금만 더 들어줬으면 좋겠어. 네 말도 중요하지만 상대의 말이 끝난 다음에 이야기해주면 소통이 더 잘 될 것 같아.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            PollOptions(
                selectedOption = selectedOption,
                onOptionSelected = { option -> selectedOption = option }
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(onClick = onChatIconClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.chatbubble),
                    contentDescription = "댓글 보기 및 작성",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("댓글", color = Color.Gray)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    sheetState: SheetState,
    viewModel: VotingViewModel, // ViewModel 받기
    onDismiss: () -> Unit
) {
    var commentInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val comments by viewModel.comments.collectAsState() // ViewModel에서 댓글 목록 가져오기

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.9f),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "댓글",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "닫기")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(comments, key = { it.author + it.text}) { comment -> // key 추가 (선택 사항)
                    CommentItem(comment)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    placeholder = { Text("댓글 입력...") }, // 플레이스홀더 텍스트 변경
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = "User Profile",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (commentInput.isNotBlank()) {
                            viewModel.addComment(commentInput) // ViewModel 함수 호출
                            commentInput = ""
                            keyboardController?.hide()
                        }
                    },
                    enabled = commentInput.isNotBlank()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "댓글 보내기",
                        tint = if (commentInput.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CommentItem(comment: CommentData) { // CommentData 받도록 수정
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Icons.Filled.AccountCircle, // 프로필 아이콘
            contentDescription = "프로필",
            modifier = Modifier
                .size(40.dp)
                .padding(end = 8.dp),
            tint = Color.Gray
        )
        Column {
            Text(comment.author, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            // 로딩 상태에 따라 UI 분기 처리
            if (comment.isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(comment.text, fontSize = 14.sp, lineHeight = 18.sp, color = Color.Gray)
                }
            } else {
                Text(comment.text, fontSize = 14.sp, lineHeight = 18.sp)
            }
        }
    }
}

enum class PollOption {
    YES, NO
}

@Composable
fun PollOptions(
    selectedOption: PollOption?,
    onOptionSelected: (PollOption) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OptionItem(
            text = "그렇다",
            optionType = PollOption.YES,
            globallySelectedOption = selectedOption,
            onClick = { onOptionSelected(PollOption.YES) }
        )

        OptionItem(
            text = "아니다",
            optionType = PollOption.NO,
            globallySelectedOption = selectedOption,
            onClick = { onOptionSelected(PollOption.NO) }
        )
    }
}

@Composable
fun OptionItem(
    text: String,
    optionType: PollOption,
    globallySelectedOption: PollOption?,
    onClick: () -> Unit
) {
    val baseColor = if (optionType == PollOption.YES) Color(0xFF007AFF) else Color(0xFFFF3B30)

    val iconDisplayColor: Color
    val labelDisplayColor: Color
    val borderDisplayColor: Color
    val fontWeightForLabel: FontWeight
    val circleBackground: Color

    if (globallySelectedOption == null) {
        iconDisplayColor = baseColor
        labelDisplayColor = baseColor
        borderDisplayColor = baseColor
        fontWeightForLabel = FontWeight.Bold
        circleBackground = Color.Transparent
    } else {
        if (globallySelectedOption == optionType) {
            iconDisplayColor = baseColor
            labelDisplayColor = baseColor
            borderDisplayColor = baseColor
            fontWeightForLabel = FontWeight.Bold
            circleBackground = baseColor.copy(alpha = 0.1f)
        } else {
            iconDisplayColor = Color.LightGray
            labelDisplayColor = Color.Gray
            borderDisplayColor = Color.LightGray
            fontWeightForLabel = FontWeight.Normal
            circleBackground = Color.Transparent
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(circleBackground)
                .border(
                    width = 2.dp,
                    color = borderDisplayColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (optionType == PollOption.YES) "O" else "X",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = iconDisplayColor,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            color = labelDisplayColor,
            fontSize = 14.sp,
            fontWeight = fontWeightForLabel
        )
    }
}

