import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sending(
    userName: String = "박지열",
    message: String ="니 말만 하지 말고 상대방 말좀 들어. 짜증나게 맨날 자기 얘기만해;;; 말좀 끊지 말고 좀 제발;",
    navController: NavHostController,
    onBack: () -> Unit = {},
    onCategory: () -> Unit = {},
    onSend: () -> Unit = {}
) {Box {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD6E9FA))
    ) {
        // Top Bar
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFFD6E9FA)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Header Text
        Text(
            text = "$userName" + "에게 하고 싶은 말",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp).align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Message Bubble
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .defaultMinSize(minHeight = 120.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFFFFF)
            )
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = onSend,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD6E9FA)
                ),
                modifier = Modifier.size(63.dp, 36.dp)
            ){
                Text(
                    text = "send",
                    color = Color.Black,
                )
            }
        }
    }
    ExpandableFabExample(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
        navController
    )
}
}
