package com.example.sendbacksendbag

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sending(
    userName: String = "박지열",
    message: String = "",
    navController: NavHostController,
    messageViewModel: MessageViewModel = viewModel(),
    onBack: () -> Unit = { navController.popBackStack() }
) {
    val context = LocalContext.current

    // 상태 관리
    var inputMessage by remember { mutableStateOf("") }
    val isLoading by messageViewModel.isLoading.collectAsState()
    val error by messageViewModel.error.collectAsState()

    // 수신자가 설정한 도착시간 가져오기
    val sendingTime = navController.currentBackStackEntry?.arguments?.getString("sendingTime") ?: "20 : 00"

    // 에러 메시지 표시
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            messageViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "${userName}에게 피드백 보내기", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 안내 텍스트
                Text(
                    text = "${userName}님에게 보낼 피드백을 작성해주세요",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )

                // 메시지 입력 필드
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF5EA7FF),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 전송 버튼
                Button(
                    onClick = {
                        if (inputMessage.isNotEmpty()) {
                            // 코루틴 스코프에서 실행
                            val scope = CoroutineScope(Dispatchers.Main)
                            scope.launch {
                                // 메시지 전송이 완료될 때까지 대기
                                val job = messageViewModel.sendMessage(
                                    receiverName = userName,
                                    content = inputMessage,
                                    sendingTime = sendingTime
                                )
                                job.join() // 전송이 완료될 때까지 대기

                                // 전송 완료 후 네비게이션
                                navController.navigate("send") {
                                    popUpTo("send") {
                                        inclusive = true
                                    }
                                }

                                // 메시지 입력 초기화
                                inputMessage = ""
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5EA7FF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "보내기", // "Send"에서 "보내기"로 변경
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            // 확장 가능한 FAB
            ExpandableFabExample(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                navController = navController
            )
        }
    }
}

// MessageViewModel 확장 함수
fun MessageViewModel.clearError() {
    viewModelScope.launch {
        _error.value = null
    }
}