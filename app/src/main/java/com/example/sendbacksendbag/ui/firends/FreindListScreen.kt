package com.example.sendbacksendbag.ui.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.sendbacksendbag.ExpandableFabExample
import com.example.sendbacksendbag.R
import com.example.sendbacksendbag.ui.profile.ProfileData
import com.example.sendbacksendbag.ui.theme.SendBackSendBagTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    navController: NavController,
    myProfile: ProfileData,
    friends: List<ProfileData>,
    onFriendClick: (ProfileData) -> Unit, // 친구 클릭 시 호출될 람다
    onAddFriendClick: () -> Unit // 친구 추가 아이콘 클릭 시 호출될 람다
) {
    Box{
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("친구", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    IconButton(onClick = onAddFriendClick) {
                        Icon(Icons.Filled.PersonAdd, contentDescription = "친구 추가")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 나의 프로필
            FriendListItem(
                navController = navController,
                profile = myProfile,
                onClick = { onFriendClick(myProfile) }, // 내 프로필 클릭 시 동작
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )

            // 구분선
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 친구 목록
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp) // 아이템 간 간격
            ) {
                items(friends) { friend ->
                    FriendListItem(
                        navController,
                        profile = friend,
                        onClick = { onFriendClick(friend) } // 친구 클릭 시 동작
                    )
                }
            }
        }
    }
        ExpandableFabExample(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            navController = navController
        )}
}

@Composable
fun FriendListItem(
    navController: NavController,
    profile: ProfileData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = {navController.navigate("profile")}) // Row 전체를 클릭 가능하게 만듦
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지
        Image(
            painter = if (profile.profileImageUri != null) {
                rememberAsyncImagePainter(
                    model = profile.profileImageUri
                )
            } else {
                painterResource(id = profile.placeholderImageRes)
            },
            contentDescription = "${profile.name} 프로필 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 이름
        Text(
            text = profile.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f) // 이름이 차지할 공간 (남는 공간 모두 차지)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 도착 시간
        Text(
            text = "도착 시간: ${profile.messageArrivalTime.replace(" : ", "시 ")}", // "20 : 00" -> "20시 00"
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

// Preview 함수 (예시 데이터 사용)
