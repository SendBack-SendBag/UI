package com.example.sendbacksendbag.ui.mypoll

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sendbacksendbag.ExpandableFabExample
import com.example.sendbacksendbag.R
import com.example.sendbacksendbag.ui.theme.SendBackSendBagTheme
import com.example.sendbacksendbag.ui.voting.CommentBottomSheet // 기존 댓글 BottomSheet 재사용
import com.example.sendbacksendbag.ui.voting.VotingViewModel // 기존 ViewModel 재사용
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPollScreen(navController: NavController, viewModel: VotingViewModel = viewModel()) {
    // BottomSheet 상태 관리
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD6E9FA)), // 배경색 설정
            topBar = {
                // 상단 앱 바 (뒤로가기, 검색, 북마크)
                TopAppBar(
                    title = { /* 제목 없음 */ },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* 검색 */ }) {
                            Icon(Icons.Default.Search, "검색")
                        }
                        IconButton(onClick = { /* 북마크 */ }) {
                            Icon(painterResource(id = R.drawable.bookmark), "북마크")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFD6E9FA), // 배경색과 동일하게
                        navigationIconContentColor = Color.Black,
                        actionIconContentColor = Color.Black
                    )
                )
            },
            containerColor = Color(0xFFD6E9FA) // Scaffold 배경색
        ) { innerPadding ->
            // 메인 컨텐츠
            MyPollContent(
                Modifier.padding(innerPadding),
                onChatIconClick = {
                    // 댓글 아이콘 클릭 시 BottomSheet 표시
                    scope.launch { showBottomSheet = true }
                }
            )
        }
        // 플로팅 액션 버튼
        ExpandableFabExample(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            navController = navController
        )
    }

    // 댓글 BottomSheet 표시 여부
    if (showBottomSheet) {
        CommentBottomSheet(
            sheetState = sheetState,
            viewModel = viewModel,
            onDismiss = {
                // BottomSheet 닫기
                scope.launch { sheetState.hide() }
                    .invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
            }
        )
    }
}

@Composable
fun MyPollContent(modifier: Modifier = Modifier, onChatIconClick: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp) // 좌우 패딩 증가
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // 화면 제목
        Text(
            text = "나의 투표",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            modifier = Modifier.padding(start = 0.dp, bottom = 24.dp) // 패딩 조정
        )

        // '내가 게시한 투표' 버튼
        Button(
            onClick = { /* TODO: 내가 게시한 투표 목록으로 이동 */ },
            shape = RoundedCornerShape(50), // 둥근 모서리
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEFEFEF), // 연한 회색 배경
                contentColor = Color.Black
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp), // 그림자 없음
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 0.dp, bottom = 32.dp) // 패딩 조정
                .height(56.dp) // 높이 증가
        ) {
            Text(
                text = "내가 게시한 투표",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold, // 굵게
                modifier = Modifier.padding(horizontal = 24.dp) // 내부 패딩 증가
            )
        }

        // 피드백 카드
        MyFeedbackCard(onChatIconClick = onChatIconClick)
    }
}

@Composable
fun MyFeedbackCard(onChatIconClick: () -> Unit) {
    val yesPercentage = 73
    val noPercentage = 23

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // 그림자 약간
        colors = CardDefaults.cardColors(containerColor = Color.White) // 흰색 배경
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally // 내부 요소 중앙 정렬
        ) {
            // 카드 제목
            Text(
                text = "내가 받은 피드백",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 구분선
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 피드백 내용
            Text(
                text = "상대방의 말을 조금만 더 들어줬으면 좋겠어. 네 말도 중요하지만 상대의 말이 끝난 다음에 이야기해주면 소통이 더 잘 될 것 같아.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Start // 왼쪽 정렬
            )

            // 투표 결과 바
            PollResultBar(yesPercentage = yesPercentage, noPercentage = noPercentage)

            Spacer(modifier = Modifier.height(24.dp))

            // 구분선
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 8.dp) // 간격 조정
            )

            // 댓글 아이콘 및 텍스트 (Row로 묶어 왼쪽 정렬)
            Row(
                modifier = Modifier
                    .fillMaxWidth() // 너비 채우기
                    .clickable(onClick = onChatIconClick) // 클릭 가능
                    .padding(vertical = 8.dp), // 수직 패딩
                verticalAlignment = Alignment.CenterVertically // 수직 중앙 정렬
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

@Composable
fun PollResultBar(yesPercentage: Int, noPercentage: Int) {
    val yesWeight = yesPercentage.toFloat()
    val noWeight = noPercentage.toFloat()
    val pollHeight = 90.dp // 높이 증가

    // Row를 사용하여 O/X 섹션을 가로로 배치
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(pollHeight)
            .clip(RoundedCornerShape(16.dp)) // 전체 Row에 둥근 모서리 적용
            .background(Color.Gray) // 혹시 모를 배경색
    ) {
        // '그렇다' 섹션
        Box(
            modifier = Modifier
                .weight(yesWeight) // 퍼센트에 따른 가중치
                .fillMaxHeight()
                .background(Color(0xFF007AFF)) // 파란색 배경
                .clickable { /* TODO: 클릭 시 동작 */ },
            contentAlignment = Alignment.Center // 내용 중앙 정렬
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "O",
                    fontSize = 32.sp, // 크기 유지
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp)) // 간격 추가
                Text(
                    text = "그렇다($yesPercentage%)",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold // 약간 굵게
                )
            }
        }
        // '아니다' 섹션
        Box(
            modifier = Modifier
                .weight(noWeight) // 퍼센트에 따른 가중치
                .fillMaxHeight()
                .background(Color(0xFFFF3B30)) // 빨간색 배경
                .clickable { /* TODO: 클릭 시 동작 */ },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "X",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "아니다($noPercentage%)",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFD6E9FA)
@Composable
fun MyPollScreenPreview() {
    val navController = rememberNavController()
    SendBackSendBagTheme {
        MyPollScreen(navController)
    }
}