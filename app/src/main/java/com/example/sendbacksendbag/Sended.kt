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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sendbacksendbag.R

import androidx.compose.runtime.Composable


@Composable
fun Sended(){
    FeedbackDetailScreen(
        userName = "박지열",
        message = "네 말은 중요하지만 상대의 말이 끝난 다음에 이야기해주면 소통이 더 잘될 것 같아.\n상대방의 말을 조금만 더 들어줬으면 좋겠어.",
        onBack = { /* 뒤로가기 */ },
        onFabClick = { /* 메뉴 클릭 */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDetailScreen(
    userName: String,
    message: String,
    time: String = "",
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
                // 말풍선
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFFFFF)
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                // 토글 스위치
                var switched by remember { mutableStateOf(false) }
                Switch(
                    checked = switched,
                    onCheckedChange = { switched = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFFFE082),
                        uncheckedThumbColor = Color(0xFFFFE082)
                    )
                )
            }
        }

        ExpandableFabExample(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}
