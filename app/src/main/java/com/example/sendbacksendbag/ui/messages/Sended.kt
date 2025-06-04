package com.example.sendbacksendbag.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Sended(
    navHostController: NavHostController,
    messageId: String,
    messageViewModel: MessageViewModel = viewModel()
) {
    // messageViewModel에서 해당 ID의 메시지 찾기
    val messages by messageViewModel.messages.collectAsState()
    val message = messages.find { it.id == messageId }

    // 메시지가 존재하면 상세 화면 표시, 없으면 기본 메시지 사용
    FeedbackDetailScreen(
        userName = message?.name ?: "메시지를 찾을 수 없음",
        message = message?.content ?: "메시지를 찾을 수 없습니다.",  // 원본 메시지(content) 사용
        time = message?.time ?: "",
        navController = navHostController,
        onBack = { navHostController.popBackStack() }
    )
}

@Composable
fun FeedbackDetailScreen(
    userName: String,
    message: String,
    time: String = "",
    navController: NavHostController,
    onBack: () -> Unit = {},
    onFabClick: () -> Unit = {}
) {
    var fabExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFFE3F2FD))) {
        // Content Column
        Column {
            // 1. Top Bar
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE3F2FD),
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),
            )

            // 2. 메시지 + 스위치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 말풍선 - 원본 메시지 표시
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFFFFF)
                    )
                ) {
                    Text(
                        text = message,  // 여기서는 원본 메시지가 표시됨 (FeedbackDetailScreen의 파라미터)
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // 토글 스위치
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFE680), CircleShape)
                )
            }
        }

        ExpandableFabExample(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
            , navController
        )
    }
}
