package com.example.sendbacksendbag.ui.voting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // 댓글 목록을 위해 추가
import androidx.compose.foundation.lazy.items // 댓글 목록을 위해 추가
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle // 프로필 아이콘 예시
import androidx.compose.material.icons.filled.Close // 닫기 아이콘 예시
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send // 보내기 아이콘 예시
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController // 키보드 컨트롤러
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // ModalBottomSheetState를 위해 추가
import androidx.compose.material3.rememberModalBottomSheetState // rememberModalBottomSheetState를 위해 추가
import androidx.compose.material3.ExperimentalMaterial3Api // OptIn 어노테이션에 필요
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState // <<--- 이 타입을 사용해야 합니다.
import androidx.compose.material3.BottomSheetDefaults
import com.example.sendbacksendbag.R
import com.example.sendbacksendbag.ui.theme.SendBackSendBagTheme
import kotlinx.coroutines.launch // 코루틴 스코프

// --- 기존 코드 시작 (SendBackSendBagTheme 사용 및 HorizontalDivider 변경) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollScreen() {
    // SendBackSendBagTheme { // 앱의 실제 테마 사용
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* 현재 화면에서는 제목이 TopAppBar에 없음 */ },
                navigationIcon = {
                    IconButton(onClick = { /* 뒤로가기 동작 */ }) {
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
                            painter = painterResource(id = R.drawable.bookmark), // 북마크 아이콘
                            contentDescription = "북마크"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        PollContent(Modifier.padding(innerPadding))
    }
    // }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollContent(modifier: Modifier = Modifier) {
    // sheetState의 타입을 명시적으로 SheetState로 지정하거나, 추론하도록 둡니다.
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "오늘의 투표",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Surface(
            shape = RoundedCornerShape(50),
            color = Color(0xFFE0E0E0).copy(alpha = 0.7f),
            modifier = Modifier
                .wrapContentWidth()
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = "박지열님이 게시한 투표",
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        FeedbackCard(
            onChatIconClick = { // FeedbackCard에서 채팅 아이콘 클릭 시 호출될 콜백
                scope.launch {
                    showBottomSheet = true
                }
            }
        )
    }

    if (showBottomSheet) {
        CommentBottomSheet(
            sheetState = sheetState,
            onDismiss = {
                scope.launch {
                    sheetState.hide() // suspend 함수 호출
                }.invokeOnCompletion { // hide() 코루틴 완료 후 실행
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
                // 또는 더 간단하게, hide()가 상태를 즉시 반영한다고 가정할 수 있다면:
                // scope.launch {
                //     sheetState.hide()
                //     if (!sheetState.isVisible) {
                //         showBottomSheet = false
                //     }
                // }
            }
        )
    }
}

@Composable
fun FeedbackCard(onChatIconClick: () -> Unit) {
    var selectedOption by remember { mutableStateOf<PollOption?>(null) } // <<--- 초기값을 null로 변경

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "박지열님이 받은 피드백",
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

            // PollOptions에 nullable selectedOption을 그대로 전달
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

// --- 기존 코드 끝 ---

// --- 새로운 코드: 댓글 BottomSheet ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    var commentInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current // 키보드 컨트롤러
    // 실제 댓글 데이터 (예시)
    val comments = remember {
        mutableStateListOf(
            CommentData("춤추는 고양이", "가끔씩 그런 점이 있는듯"),
            CommentData("배고픈 수달", "인정"),
            CommentData("코딩하는 말", "난 아닌거 같던데")
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.9f), // 화면 높이의 90%까지 차지하도록 설정
        dragHandle = { BottomSheetDefaults.DragHandle() } // 상단 드래그 핸들 추가
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 1. 상단 영역 (제목, 닫기 버튼)
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

            // 2. 댓글 목록 (LazyColumn 사용)
            LazyColumn(
                modifier = Modifier.weight(1f) // 남은 공간을 모두 차지하도록
            ) {
                items(comments) { comment ->
                    CommentItem(comment)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 3. 댓글 입력 필드
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    placeholder = { Text("댓글 입력...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp), // 둥근 모서리
                    leadingIcon = { // 사용자 프로필 아이콘 (선택 사항)
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
                            // 새 댓글 추가 (실제 앱에서는 ViewModel 등을 통해 처리)
                            comments.add(0, CommentData("나", commentInput)) // 맨 위에 추가
                            commentInput = "" // 입력 필드 초기화
                            keyboardController?.hide() // 키보드 숨기기
                        }
                    },
                    enabled = commentInput.isNotBlank() // 입력 내용이 있을 때만 활성화
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "댓글 보내기",
                        tint = if (commentInput.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)) // 네비게이션 바 높이만큼 간격 추가
            Spacer(modifier = Modifier.height(16.dp)) // 추가적인 하단 여백
        }
    }
}

// 댓글 데이터 클래스
data class CommentData(val author: String, val text: String)

@Composable
fun CommentItem(comment: CommentData) {
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
            Text(comment.text, fontSize = 14.sp, lineHeight = 18.sp)
        }
    }
}

// --- 나머지 코드 (PollOption, PollOptions, OptionItem)는 이전과 동일 ---
enum class PollOption {
    YES, NO
}

@Composable
fun PollOptions(
    selectedOption: PollOption?, // PollOption? (nullable) 타입을 사용합니다.
    onOptionSelected: (PollOption) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OptionItem(
            text = "그렇다",
            optionType = PollOption.YES, // 현재 아이템의 타입
            globallySelectedOption = selectedOption, // 전체 선택 상태 전달
            onClick = { onOptionSelected(PollOption.YES) }
        )

        OptionItem(
            text = "아니다",
            optionType = PollOption.NO, // 현재 아이템의 타입
            globallySelectedOption = selectedOption, // 전체 선택 상태 전달
            onClick = { onOptionSelected(PollOption.NO) }
        )
    }
}

@Composable
fun OptionItem(
    text: String,
    optionType: PollOption, // 이 아이템이 O인지 X인지
    globallySelectedOption: PollOption?, // 현재 전역적으로 선택된 옵션 (null일 수 있음)
    onClick: () -> Unit
) {
    val baseColor = if (optionType == PollOption.YES) Color(0xFF007AFF) else Color(0xFFFF3B30) // O 파랑, X 빨강

    // 상태에 따른 색상 및 스타일 결정
    val iconDisplayColor: Color
    val labelDisplayColor: Color
    val borderDisplayColor: Color
    val fontWeightForLabel: FontWeight
    val circleBackground: Color

    if (globallySelectedOption == null) {
        // 초기 상태: 아무것도 선택되지 않았을 때 (둘 다 활성 상태로 보임)
        iconDisplayColor = baseColor
        labelDisplayColor = baseColor // 라벨도 기본 색상 사용
        borderDisplayColor = baseColor
        fontWeightForLabel = FontWeight.Bold // 초기에도 강조
        circleBackground = Color.Transparent
    } else {
        // 무언가 선택된 상태
        if (globallySelectedOption == optionType) {
            // 이 아이템이 선택된 경우
            iconDisplayColor = baseColor
            labelDisplayColor = baseColor
            borderDisplayColor = baseColor
            fontWeightForLabel = FontWeight.Bold
            circleBackground = baseColor.copy(alpha = 0.1f) // 선택된 아이템 배경 강조
        } else {
            // 이 아이템이 선택되지 않은 경우 (흐리게 처리)
            iconDisplayColor = Color.LightGray
            labelDisplayColor = Color.Gray // 텍스트는 LightGray보다 조금 더 진한 Gray 사용
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
                .background(circleBackground) // 결정된 배경색 적용
                .border(
                    width = 2.dp,
                    color = borderDisplayColor, // 결정된 테두리색 적용
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (optionType == PollOption.YES) "O" else "X",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold, // O, X 아이콘 자체는 항상 굵게
                color = iconDisplayColor, // 결정된 아이콘 색상 적용
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            color = labelDisplayColor, // 결정된 라벨 색상 적용
            fontSize = 14.sp,
            fontWeight = fontWeightForLabel // 결정된 폰트 굵기 적용
        )
    }
}

// PollOption enum class는 동일하게 유지됩니다.
// enum class PollOption { YES, NO }


@Preview(showBackground = true, backgroundColor = 0xFFE7F0FE)
@Composable
fun PollScreenPreview() {
    SendBackSendBagTheme { // 앱의 실제 테마 사용
        PollScreen()
    }
}
