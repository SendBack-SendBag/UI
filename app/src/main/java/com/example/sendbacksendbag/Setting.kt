import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sendbacksendbag.ExpandableFabExample

@Composable
fun SettingsScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    onSearch: (String) -> Unit,
    onAccountClick: () -> Unit,
    onItemClick: (String) -> Unit
) {
    val query = remember { mutableStateOf("") }
    Box {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("설정", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.align(Alignment.Center)) },

                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.White)
                ) {
                    // Search field
                    TextField(
                        value = query.value,
                        onValueChange = { query.value = it },
                        placeholder = { Text("search") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .height(48.dp)
                            .background(Color(0xFFE0F7FF), RoundedCornerShape(24.dp)),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0xFFE0F7FF),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    // Account section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0x00000000)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAccountClick() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Account",
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Color.LightGray, CircleShape)
                                    .padding(8.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("계정", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text("비밀번호, 보안, 개인정보", color = Color.Gray, fontSize = 12.sp)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                    // Section 1
                    SettingsItem(icon = Icons.Default.Bookmark, label = "보관") { onItemClick("보관") }
                    SettingsItem(icon = Icons.Default.History, label = "시간 관리") { onItemClick("시간 관리") }
                    SettingsItem(icon = Icons.Default.Notifications, label = "알림") { onItemClick("알림") }
                    SettingsItem(icon = Icons.Default.Feedback, label = "내 활동") { onItemClick("내 활동") }

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    // Section 2
                    SettingsItem(icon = Icons.Default.Feedback, label = "피드백") { onItemClick("피드백") }
                    SettingsItem(icon = Icons.Default.Group, label = "친구 관리") { onItemClick("친구 관리") }
                }
            }
        )
        ExpandableFabExample(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            navController = navController
        )
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF555555), modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(label, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}