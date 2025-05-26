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
import coil3.compose.rememberAsyncImagePainter
import com.example.sendbacksendbag.R
import com.example.sendbacksendbag.ui.profile.ProfileData
import com.example.sendbacksendbag.ui.theme.SendBackSendBagTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    myProfile: ProfileData,
    friends: List<ProfileData>,
    onFriendClick: (ProfileData) -> Unit, // 친구 클릭 시 호출될 람다
    onAddFriendClick: () -> Unit // 친구 추가 아이콘 클릭 시 호출될 람다
) {
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
                        profile = friend,
                        onClick = { onFriendClick(friend) } // 친구 클릭 시 동작
                    )
                }
            }
        }
    }
}

@Composable
fun FriendListItem(
    profile: ProfileData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Row 전체를 클릭 가능하게 만듦
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
@Preview(showBackground = true, name = "Friends Screen Preview")
@Composable
fun FriendsScreenPreview() {
    // 예시 데이터 생성
    val myProfile = ProfileData(
        id = "me",
        name = "김승우",
        messageArrivalTime = "20 : 00",
        profileImageUri = null, // Uri.parse("...") 또는 null
        placeholderImageRes = R.drawable.example_picture // R.drawable.kim_seung_woo 등 실제 이미지 리소스 사용
    )

    val friendsList = listOf(
        ProfileData(
            id = "friend1",
            name = "박지열",
            messageArrivalTime = "20 : 00",
            profileImageUri = null,
            placeholderImageRes = R.drawable.example_picture // R.drawable.park_ji_yeol
        ),
        ProfileData(
            id = "friend2",
            name = "원숭이",
            messageArrivalTime = "20 : 00",
            profileImageUri = null,
            placeholderImageRes = R.drawable.example_picture // R.drawable.won_soong_i
        ),
        ProfileData(
            id = "friend3",
            name = "이승주",
            messageArrivalTime = "20 : 00",
            profileImageUri = null,
            placeholderImageRes = R.drawable.example_picture // R.drawable.lee_seung_ju
        ),
        ProfileData(
            id = "friend4",
            name = "나이병",
            messageArrivalTime = "20 : 00",
            profileImageUri = null,
            placeholderImageRes = R.drawable.example_picture // R.drawable.na_i_byeong
        )
    )

    SendBackSendBagTheme {
        FriendsScreen(
            myProfile = myProfile,
            friends = friendsList,
            onFriendClick = { profile ->
                println("Clicked on: ${profile.name}") // 클릭 시 로그 출력 (네비게이션 로직 대체)
            },
            onAddFriendClick = {
                println("Add Friend Clicked!") // 친구 추가 버튼 클릭 시 로그 출력
            }
        )
    }
}