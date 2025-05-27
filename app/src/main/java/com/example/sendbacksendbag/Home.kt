import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
                .padding(16.dp)
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
                }
            )
            BlackHorizontalLine()
            Spacer(modifier = Modifier.height(30.dp))
            // Notification Card
            Card(
                colors = CardDefaults.cardColors(containerColor = lightBlue),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(headerGray)
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "알림", fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        notifications.forEach { note ->
                            Text(text = "• $note", fontWeight = FontWeight.Normal, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Options Card
            Card(
                colors = CardDefaults.cardColors(containerColor = lightBlue),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(headerGray)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(150.dp)
                                .background(Color.LightGray, RoundedCornerShape(8.dp))
                                .clickable(onClick = onReceivedClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("받은 메시지", fontWeight = FontWeight.Black, fontSize = 22.sp)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(150.dp)
                                .background(Color.LightGray, RoundedCornerShape(8.dp))
                                .clickable(onClick = onSentClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("보낸 메시지", fontWeight = FontWeight.Black,fontSize = 22.sp)
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
