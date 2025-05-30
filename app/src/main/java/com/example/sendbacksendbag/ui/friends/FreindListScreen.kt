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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.example.sendbacksendbag.ExpandableFabExample
import com.example.sendbacksendbag.R
import com.example.sendbacksendbag.ui.profile.ProfileData
import androidx.compose.ui.platform.LocalContext // Coil에 필요
import com.example.sendbacksendbag.BlackHorizontalLine
import com.example.sendbacksendbag.ui.profile.formatDisplayTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    navController: NavController,
    myProfile: ProfileData, // StateFlow에서 온 최신 데이터
    friends: List<ProfileData>, // StateFlow에서 온 최신 데이터
    onFriendClick: (ProfileData) -> Unit,
    onAddFriendClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) { // FAB를 위해 Box 사용
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("친구", fontWeight = FontWeight.Bold, fontSize = 30.sp) },
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
                BlackHorizontalLine()
            },
            containerColor = Color.White
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                BlackHorizontalLine()
                Spacer(modifier = Modifier.height(8.dp)) // 간격

                // 나의 프로필
                FriendListItem(
                    profile = myProfile,
                    onClick = { onFriendClick(myProfile) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
                BlackHorizontalLine()
                Spacer(modifier = Modifier.height(8.dp)) // 간격

                Text( // 친구 목록 라벨
                    text = "친구 목록 ${friends.size}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                // 친구 목록
                LazyColumn(
                    modifier = Modifier.weight(1f), // 남은 공간 모두 차지
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp) // 아이템 간 간격
                ) {
                    items(friends, key = { it.id!! }) { friend ->
                        FriendListItem(
                            profile = friend,
                            onClick = { onFriendClick(friend) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(80.dp)) // FAB 공간 확보
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
fun FriendListItem(
    profile: ProfileData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp), // 패딩 조정
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = if (profile.profileImageUri != null) {
                rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profile.profileImageUri)
                        // ** FriendsScreen에서도 캐시 정책 고려 **
                        .memoryCacheKey(profile.profileImageUri.toString() + "#" + System.currentTimeMillis()) // 키 변경
                        .memoryCachePolicy(CachePolicy.DISABLED) // 캐시 비활성화 (또는 적절한 정책 설정)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .build()
                )
            } else {
                painterResource(id = profile.placeholderImageRes)
            },
            contentDescription = "${profile.name} 프로필 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp) // 이미지 크기 조정
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = profile.name,
                fontSize = 16.sp, // 이름 크기 조정
                fontWeight = FontWeight.SemiBold, // 굵기 조정
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
        }


        Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "도착 시간: ${formatDisplayTime(profile.messageArrivalTime)}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }