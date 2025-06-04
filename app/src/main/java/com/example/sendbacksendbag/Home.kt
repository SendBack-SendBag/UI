import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sendbacksendbag.BlackHorizontalLine
import com.example.sendbacksendbag.ExpandableFabExample

@Composable
fun HomeScreen(
    navController: NavController,
    notifications: List<String>,
    onSettingsClick: () -> Unit,
    onReceivedClick: () -> Unit,
    onSentClick: () -> Unit
) {
    val lightBlue = Color(0xFFD6E9FA)
    val headerGray = Color(0xFFCCCCCC)

    Box{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)

        ) {
            TopAppBar(
                title = { Text(text = "send back", fontWeight = FontWeight.Black,fontSize = 30.sp) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black                )
            )
            BlackHorizontalLine()
            Spacer(modifier = Modifier.height(30.dp))
            // Notification Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF4FF))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 제목 + 날짜
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "알림",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color(0xFFB0BEC5), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(12.dp))
                    // 내용 리스트
                    Column {
                        notifications.forEachIndexed { index, note ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Circle,
                                    contentDescription = null,
                                    modifier = Modifier.size(8.dp),
                                    tint = Color(0xFF007AFF)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = note,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 20.sp
                                )
                            }
                            if (index < notifications.lastIndex) {
                                Divider(color = Color(0xFFD1D1D1), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Options Card
            Column(modifier = Modifier.padding(16.dp)) {
                // 제목
                Text(
                    text = "메시지",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 두 개의 옵션 카드를 가로로 배치
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    // 받은 메시지 카드
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onReceivedClick),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "받은 메시지",
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF1976D2)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "받은 메시지",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }

                    // 보낸 메시지 카드
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onSentClick),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "보낸 메시지",
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "보낸 메시지",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
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
