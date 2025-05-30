package com.example.sendbacksendbag

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavController
import com.example.sendbacksendbag.*
import com.example.sendbacksendbag.R
import com.example.sendbacksendbag.ui.friends.FriendsScreen
import com.example.sendbacksendbag.ui.login.AuthScreen
import com.example.sendbacksendbag.ui.profile.ProfileData
import com.example.sendbacksendbag.ui.profile.ProfileScreenContainer
import com.example.sendbacksendbag.ui.voting.PollScreen

// 메시지 데이터 모델
data class Message2(
    val name: String,
    val avatarRes: Int,
    val content: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendScreen(
    messages: List<Message2>,
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(text = "보낸 메시지", fontWeight = FontWeight.Black) },
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
                CenteredVerticalSearchField()
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(messages) { msg ->
                    MessageItem(msg = msg, onClick = {
                        navController.navigate("sended")
                    })
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
    msg: Message2,
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
                text = msg.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Text(text = msg.time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
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

@Composable
fun Send(navController: NavController) {
    val sampleList = listOf(
        Message2("박지열", R.drawable.example , "20시에 전송될 예정입니다.", "오후 1:33"),
        Message2("이승주", R.drawable.example, "18시에 전송될 예정입니다.", "오후 3:34"),
        Message2("나이병", R.drawable.example ,"23시에 전송될 예정입니다.", "오후 5:21")
    )
    SendScreen(messages = sampleList, navController)
}
@Composable
fun BlackHorizontalLine() {
    Divider(
        color = Color.Black,
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)  // 좌우 16dp 패딩
    )
}
@Composable
fun CenteredVerticalSearchField() {
    var searchQuery by remember { mutableStateOf("") }

    BasicTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp),    // 좌우 패딩
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = Color.DarkGray
        ),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp), // 위·아래 10dp 패딩
                contentAlignment = Alignment.CenterStart // 왼쪽 정렬, 세로 중앙
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                inner()  // 실제 입력 텍스트
            }
        }
    )
}
