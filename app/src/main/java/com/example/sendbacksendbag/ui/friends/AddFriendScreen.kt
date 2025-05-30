package com.example.sendbacksendbag.ui.friends

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendScreen(
    navController: NavController,
    friendsViewModel: FriendsViewModel // ViewModel 주입
) {
    var friendName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current // Toast 메시지를 위해 Context 가져오기

    // ViewModel에서 친구 추가 결과 관찰 (StateFlow 또는 다른 메커니즘 사용 가능)
    // 여기서는 간단히 Toast 메시지로 결과를 표시합니다.

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "연락처로 친구 추가",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center // 제목 중앙 정렬 (선택 사항)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
                    }
                },
                actions = {
                    // TopAppBar 오른쪽에 공간을 두어 제목을 중앙에 가깝게 배치 (선택 사항)
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White // Scaffold 배경색
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp), // 좌우 패딩 증가
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // 카드를 중앙에 배치
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(), // 내용물 높이에 맞춤
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F0FF)) // 연한 파란색 배경
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 친구 이름 입력 필드
                    AddFriendTextField(
                        value = friendName,
                        onValueChange = { friendName = it },
                        placeholder = "친구 이름"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 전화번호 입력 필드
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "+82",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 15.dp) // TextField 높이와 맞춤
                                .height(56.dp) // TextField와 높이 맞춤
                                .wrapContentHeight(Alignment.CenterVertically) // 텍스트 세로 중앙 정렬
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AddFriendTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            placeholder = "010-1234-1234",
                            keyboardType = KeyboardType.Phone,
                            modifier = Modifier.weight(1f) // 남은 공간 채우기
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 친구 추가 버튼
                    Button(
                        onClick = {
                            if (friendName.isNotBlank() && phoneNumber.isNotBlank()) {
                                friendsViewModel.addFriendByContact(friendName, phoneNumber) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        navController.popBackStack() // 성공 시 이전 화면으로 이동
                                    }
                                }
                            } else {
                                Toast.makeText(context, "이름과 전화번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent, // 배경 투명
                            contentColor = MaterialTheme.colorScheme.primary // 글자색
                        ),
                        modifier = Modifier.align(Alignment.End), // 오른쪽 정렬
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp) // 그림자 없음
                    ) {
                        Text("친구 추가", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f)) // 카드를 위쪽으로 밀어 올리기 위한 여백 (선택 사항)
        }
    }
}

@Composable
fun AddFriendTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { Text(placeholder, color = Color.Gray) },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,    // 포커스 시 배경 흰색
            unfocusedContainerColor = Color.White,  // 비포커스 시 배경 흰색
            focusedBorderColor = Color.Transparent, // 포커스 시 테두리 투명
            unfocusedBorderColor = Color.Transparent, // 비포커스 시 테두리 투명
            cursorColor = MaterialTheme.colorScheme.primary, // 커서 색상
            focusedPlaceholderColor = Color.Gray,   // 플레이스홀더 색상 (포커스)
            unfocusedPlaceholderColor = Color.Gray, // 플레이스홀더 색상 (비포커스)
            focusedTextColor = Color.Black,         // 텍스트 색상 (포커스)
            unfocusedTextColor = Color.Black        // 텍스트 색상 (비포커스)
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}