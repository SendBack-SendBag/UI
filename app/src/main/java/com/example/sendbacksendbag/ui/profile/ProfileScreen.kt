package com.example.sendbacksendbag.ui.profile

// import coil3.key.ObjectKey // 사용하지 않음
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.example.sendbacksendbag.ExpandableFabExample
import com.example.sendbacksendbag.R
import com.example.sendbacksendbag.data.FriendsRepository
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

// SharedPreferences Keys
private const val PREFS_NAME = "ProfilePrefs"
private const val KEY_NAME = "profile_name"
private const val KEY_ARRIVAL_TIME_LABEL = "profile_arrival_time_label"
private const val KEY_ARRIVAL_TIME = "profile_arrival_time"
private const val KEY_STATUS_MESSAGE = "profile_status_message"
private const val KEY_IMAGE_URI_STRING = "profile_image_uri_string"

// File Name Constants
private const val MAIN_PROFILE_IMAGE_FILENAME_PREFIX = "profile_image_"
private const val TEMP_PROFILE_IMAGE_FILENAME = "temp_profile_image.jpg"


@Composable
fun ProfileScreenContainer(
    navController: NavController,
    userIdFromNav: String,
    friendsRepository: FriendsRepository // ViewModel 또는 Hilt 주입 권장
) {
    val context = LocalContext.current
    val isMyProfile = userIdFromNav == "me"

    // Repository의 StateFlow를 구독하여 최신 데이터를 받습니다.
    val profileDataFromRepo by if (isMyProfile) {
        friendsRepository.myProfile.collectAsState()
    } else {
        friendsRepository.friends.map { friends ->
            friends.find { it.id == userIdFromNav }
                ?: ProfileData( // 친구가 없을 때 기본값 (String을 받는 생성자 사용)
                    id = userIdFromNav,
                    name = "알 수 없는 사용자",
                    statusMessage = "정보 없음",
                    profileImageUriString = null,
                    placeholderImageRes = R.drawable.example_picture
                )
        }.collectAsState(
            initial = friendsRepository.getFriendById(userIdFromNav) ?: ProfileData(
                id = userIdFromNav,
                name = "알 수 없는 사용자",
                statusMessage = "정보 없음",
                profileImageUriString = null,
                placeholderImageRes = R.drawable.example_picture
            )
        )
    }

    var isEditingState by remember { mutableStateOf(false) }
    var tempProfileData by remember(profileDataFromRepo, isEditingState) {
        mutableStateOf(profileDataFromRepo.copy())
    }

    val tempProfileImageFile = remember { File(context.filesDir, TEMP_PROFILE_IMAGE_FILENAME) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri: Uri? ->
        if (!isEditingState) return@rememberLauncherForActivityResult

        selectedUri?.let { uri ->
            val tempUri = friendsRepository.copyUriToInternalStorage(uri, tempProfileImageFile)
            tempUri?.let {
                // --- 수정: profileImageUriString으로 copy하고, profileImageUri는 set 프로퍼티 사용 ---
                tempProfileData = tempProfileData.copy(profileImageUriString = it.toString()).apply {
                    this.profileImageUri = it // set 프로퍼티 호출
                }
                Log.d("ImagePicker", "Image copied to temp file: $it")
            } ?: Log.e("ImagePicker", "Error copying image to temp file")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (tempProfileImageFile.exists()) {
                tempProfileImageFile.delete()
                Log.d("ProfileScreen", "Temp image file deleted on dispose.")
            }
        }
    }

    ProfileScreen(
        navController,
        profileData = if (isEditingState) tempProfileData else profileDataFromRepo,
        isMyProfile = isMyProfile,
        isEditing = isEditingState,
        onEditClick = {
            tempProfileData = profileDataFromRepo.copy()
            isEditingState = true
        },
        onSaveClick = {
            if (!isEditingState) return@ProfileScreen

            var dataToPersist = tempProfileData.copy()
            val targetFileName = "${MAIN_PROFILE_IMAGE_FILENAME_PREFIX}${dataToPersist.id}.jpg"
            val targetFile = File(context.filesDir, targetFileName)

            // --- 수정: tempProfileData.profileImageUriInternal 대신 tempProfileData.profileImageUri 사용 ---
            if (tempProfileData.profileImageUri == Uri.fromFile(tempProfileImageFile) && tempProfileImageFile.exists()) {
                val finalUri = friendsRepository.copyUriToInternalStorage(Uri.fromFile(tempProfileImageFile), targetFile)
                if (finalUri != null) {
                    // --- 수정: profileImageUriString으로 copy하고, profileImageUri는 set 프로퍼티 사용 ---
                    dataToPersist = dataToPersist.copy(
                        profileImageUriString = finalUri.toString()
                    ).apply {
                        this.profileImageUri = finalUri // set 프로퍼티 호출
                    }
                    Log.d("ProfileSave", "Image saved to: ${targetFile.absolutePath}")
                } else {
                    Log.e("ProfileSave", "Failed to save image. Keeping old image if any.")
                    // --- 수정: profileImageUriString으로 copy하고, profileImageUri는 set 프로퍼티 사용 ---
                    dataToPersist = dataToPersist.copy(
                        profileImageUriString = profileDataFromRepo.profileImageUriString
                    ).apply {
                        this.profileImageUri = profileDataFromRepo.profileImageUri // set 프로퍼티 호출
                    }
                }
            }

            if (isMyProfile) {
                friendsRepository.updateMyProfile(dataToPersist)
            } else {
                friendsRepository.updateFriend(dataToPersist)
            }

            isEditingState = false
            if (tempProfileImageFile.exists()) {
                tempProfileImageFile.delete()
            }
        },
        onCancelClick = {
            isEditingState = false
            if (tempProfileImageFile.exists()) {
                tempProfileImageFile.delete()
            }
        },
        onProfileImageChangeClick = {
            // --- 수정 기능: 내 프로필/친구 프로필 모두 이미지 변경 가능 ---
            if (isEditingState) {
                imagePickerLauncher.launch("image/*")
            }
        },
        onProfileDataChange = { updatedData ->
            if (isEditingState) {
                tempProfileData = updatedData
            }
        }
    )
}

// ProfileScreen, ProfileCard, formatDisplayTime 함수는 이전과 동일하게 유지합니다.
// ProfileCard의 OutlinedTextField에서 profileData.copy(...) 호출은
// messageArrivalTime, name, statusMessage 등 주 생성자 파라미터만 사용하므로
// 문제가 없습니다. 이미지 로더 부분도 그대로 유지합니다.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileData: ProfileData,
    isMyProfile: Boolean,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onProfileImageChangeClick: () -> Unit,
    onProfileDataChange: (ProfileData) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isMyProfile) "내 프로필" else profileData.name , fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = onSaveClick) {
                            Icon(Icons.Filled.Check, contentDescription = "저장")
                        }
                        IconButton(onClick = onCancelClick) {
                            Icon(Icons.Filled.Close, contentDescription = "취소")
                        }
                    } else {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Filled.Edit, contentDescription = "프로필 수정")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileCard(
                    profileData = profileData,
                    isMyProfile = isMyProfile,
                    isEditing = isEditing,
                    onProfileImageChangeClick = onProfileImageChangeClick,
                    onProfileDataChange = onProfileDataChange,
                    onSendFeedbackClick = {
                        if (!isMyProfile && profileData.id != null && profileData.id != "me") {
                            navController.navigate("feedback/${profileData.id}")
                        }
                    }
                )
                // Add bottom padding to ensure FAB doesn't overlap excessively
                Spacer(modifier = Modifier.height(80.dp))
            }

            ExpandableFabExample(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                navController = navController
            )
        }
    Box {
        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = {
                        androidx.compose.material3.Text(
                            text = "프로필",
                            fontWeight = FontWeight.Black,
                            fontSize = 25.sp
                        )
                    },
                    navigationIcon = {
                        androidx.compose.material3.IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            androidx.compose.material.Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            },
            containerColor = Color.White
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                HorizontalDivider(
                    color = Color.Black,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                ProfileCard(
                    navController,
                    profileData = profileData,
                    isEditing = isEditing,
                    onProfileImageChangeClick = onProfileImageChangeClick,
                    onProfileDataChange = onProfileDataChange
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCard(
    profileData: ProfileData,
    isMyProfile: Boolean,
    isEditing: Boolean,
    onProfileImageChangeClick: () -> Unit,
    onProfileDataChange: (ProfileData) -> Unit,
    onSendFeedbackClick: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val currentTimeParts = profileData.messageArrivalTime.split(" : ")
    val initialHour = if (currentTimeParts.size == 2) currentTimeParts[0].toIntOrNull() ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY) else Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val initialMinute = if (currentTimeParts.size == 2) currentTimeParts[1].toIntOrNull() ?: Calendar.getInstance().get(Calendar.MINUTE) else Calendar.getInstance().get(Calendar.MINUTE)

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    // 시간 선택 다이얼로그
    if (showTimePicker && isEditing) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("시간 선택") },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newTime = String.format("%02d : %02d", timePickerState.hour, timePickerState.minute)
                        onProfileDataChange(profileData.copy(messageArrivalTime = newTime))
                        showTimePicker = false
                    }
                ) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("취소") }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp), // 이미지 오버랩을 위한 패딩
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0EFFF)) // 연한 파란색 배경
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // --- MODIFICATION: Increased top padding for more space ---
                    .padding(top = 160.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isEditing) {
                    // --- 편집 모드 ---
                    OutlinedTextField(
                        value = profileData.name,
                        onValueChange = { onProfileDataChange(profileData.copy(name = it)) },
                        label = { Text("이름") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (isMyProfile){
                        OutlinedTextField(
                            value = formatDisplayTime(profileData.messageArrivalTime),
                            onValueChange = { /* Do nothing, read-only */ },
                            label = { Text(profileData.messageArrivalTimeLabel) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    focusManager.clearFocus()
                                    showTimePicker = true
                                },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.clock), // 시계 아이콘 (리소스 필요)
                                    contentDescription = "시간 선택",
                                    modifier = Modifier.clickable {
                                        focusManager.clearFocus()
                                        showTimePicker = true
                                    }
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. 상태 메시지 (메시지 도착 시간 아래)
                    OutlinedTextField(
                        value = profileData.statusMessage,
                        onValueChange = { onProfileDataChange(profileData.copy(statusMessage = it)) },
                        label = { Text("상태 메시지") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )

                } else {
                    // --- 보기 모드 (이미지 디자인 + 상태 메시지 반영) ---
                    Text(profileData.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. 메시지 도착 시간 (이름 아래)
                    Text(profileData.messageArrivalTimeLabel, fontSize = 14.sp, color = Color.Gray)
                    Text(formatDisplayTime(profileData.messageArrivalTime), fontSize = 14.sp, color = Color.Gray)

                    // --- MODIFICATION: Added Status Message back ---
                    Text(profileData.statusMessage, fontSize = 16.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(16.dp)) // 상태 메시지와 시간 사이 간격
                }

                // --- Spacer ---
                val spacerHeight = if (isEditing) 30.dp
                else  250.dp

                Spacer(modifier = Modifier.height(spacerHeight))

                // --- 피드백 보내기 버튼 ---
                if (!isMyProfile && !isEditing) {
                    Button(
                        onClick = onSendFeedbackClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDCDCDC)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    ) {
                        Text("피드백 보내기", color = Color.Black)
                    }
                }
            }
        }

        // --- 프로필 이미지 ---
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopCenter)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable(enabled = isEditing) {
                    if (isEditing) onProfileImageChangeClick()
                }
        ) {
            Image(
                painter = if (profileData.profileImageUri != null) {
                    rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(profileData.profileImageUri)
                            .memoryCacheKey(profileData.profileImageUri.toString() + "#" + System.currentTimeMillis())
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .build()
                    )
                } else {
                    painterResource(id = profileData.placeholderImageRes)
                },
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (isEditing) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { onProfileImageChangeClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "변경",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// formatDisplayTime 함수는 그대로 유지합니다.
fun formatDisplayTime(timeString: String): String {
    return try {
        val parts = timeString.split(" : ")
        if (parts.size == 2) {
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            val displayHour = if (calendar.get(Calendar.HOUR_OF_DAY) == 0) 12
            else if (calendar.get(Calendar.HOUR_OF_DAY) > 12) calendar.get(Calendar.HOUR_OF_DAY) - 12
            else calendar.get(Calendar.HOUR_OF_DAY)
            val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "오전" else "오후"
            String.format("%s %d:%02d", amPm, displayHour, minute)
        } else { timeString }
    } catch (e: Exception) { timeString }
}
