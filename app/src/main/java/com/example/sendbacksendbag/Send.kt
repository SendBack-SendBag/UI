package com.example.sendbacksendbag

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun SendScreen(
    navController: NavController,
    messageViewModel: MessageViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val sentMessages by messageViewModel.sentMessages.collectAsState()
    val messageSent by messageViewModel.messageSent.collectAsState()

    // 화면이 표시될 때마다 메시지 목록 강제 새로고침
    LaunchedEffect(Unit) {
        messageViewModel.refreshMessages()
    }

    // 메시지 전송 완료 이벤트 감지
    LaunchedEffect(messageSent) {
        if (messageSent) {
            // 전송 완료 후 메시지 목록 다시 로드하고 상태 초기화
            messageViewModel.refreshMessages()
            messageViewModel.resetMessageSent()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(text = "보낸 피드백", fontWeight = FontWeight.Black, fontSize = 30.sp) },
                actions = {
                    IconButton(onClick = {navController.navigate("settings")}) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            )
            BlackHorizontalLine()
            Spacer(modifier = Modifier.height(16.dp))

            // 검색 바
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                CenteredVerticalSearchField(searchQuery) { searchQuery = it }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 전송된 메시지와 예정된 메시지 둘 다 표시
            LazyColumn {
                items(sentMessages.filter {
                    // 이름이나 내용에 검색어가 포함된 항목만 표시
                    (it.name.contains(searchQuery, true) ||
                    it.content.contains(searchQuery, true) ||
                    it.transformedContent.contains(searchQuery, true))
                }) { msg ->
                    MessageItem(
                        msg = msg,
                        onClick = {
                            navController.navigate("sended/${msg.id}")
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
private fun MessageItem(
    msg: Message,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(msg.avatarRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = msg.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                text = msg.sendingTime + "에 전송될 예정입니다. ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Text(text = msg.time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}


@Composable
fun CenteredVerticalSearchField(value: String, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp),
        textStyle = TextStyle(fontSize = 14.sp, color = Color.DarkGray),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(text = "Search", color = Color.Gray, fontSize = 16.sp)
                }
                inner()
            }
        }
    )
}

@Composable
private fun MiniFab(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}

//@Composable
//fun Send(navController: NavController) {
//    val sampleList = listOf(
//        Message(name = "박지열", avatarRes = R.drawable.example , status = "20시에 전송될 예정입니다.", time = "오후 1:33"),
//        Message(name = "이승주", avatarRes = R.drawable.example, status = "18시에 전송될 예정입니다.", time = "오후 3:34"),
//        Message(name = "나이병", avatarRes =  R.drawable.example ,status = "23시에 전송될 예정입니다.", time = "오후 5:21")
//    )
//    SendScreen(messages = sampleList, navController)
//}
