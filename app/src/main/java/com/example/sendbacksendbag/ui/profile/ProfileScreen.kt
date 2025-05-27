package com.example.sendbacksendbag.ui.profile

// import coil3.key.ObjectKey // 사용하지 않음
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import com.example.sendbacksendbag.ui.theme.SendBackSendBagTheme
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
private const val KEY_IMAGE_URI = "profile_image_uri"

// File Name Constants
private const val MAIN_PROFILE_IMAGE_FILENAME = "profile_image.jpg"
private const val TEMP_PROFILE_IMAGE_FILENAME = "temp_profile_image.jpg"


@Composable
fun ProfileScreenContainer(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    val mainProfileImageFile = remember { File(context.filesDir, MAIN_PROFILE_IMAGE_FILENAME) }
    val tempProfileImageFile = remember { File(context.filesDir, TEMP_PROFILE_IMAGE_FILENAME) }

    var profileDataState by remember {
        val name = sharedPreferences.getString(KEY_NAME, "박지열") ?: "박지열"
        val arrivalTimeLabel = sharedPreferences.getString(KEY_ARRIVAL_TIME_LABEL, "메시지 도착 시각") ?: "메시지 도착 시각"
        val arrivalTime = sharedPreferences.getString(KEY_ARRIVAL_TIME, "20 : 00") ?: "20 : 00"
        val statusMessage = sharedPreferences.getString(KEY_STATUS_MESSAGE, "오늘도 화이팅!") ?: "오늘도 화이팅!"
        val imageUriString = sharedPreferences.getString(KEY_IMAGE_URI, null)

        val initialImageUri = imageUriString?.let {
            try {
                val parsedUri = Uri.parse(it)
                // 저장된 URI가 실제로 mainProfileImageFile을 가리키고 파일이 존재할 때만 유효한 것으로 간주
                if (parsedUri.scheme == "file" && parsedUri.path == mainProfileImageFile.absolutePath && mainProfileImageFile.exists()) {
                    parsedUri
                } else {
                    Log.w("ProfileLoad", "Stored image URI $it is invalid or file missing. Clearing.")
                    // 유효하지 않으면 SharedPreferences에서도 해당 URI 정보를 지울 수 있음 (선택적)
                    // sharedPreferences.edit().remove(KEY_IMAGE_URI).apply()
                    null
                }
            } catch (e: Exception) {
                Log.e("ProfileLoad", "Failed to parse stored URI: $it", e)
                null
            }
        }

        mutableStateOf(
            ProfileData(
                name = name,
                messageArrivalTimeLabel = arrivalTimeLabel,
                messageArrivalTime = arrivalTime,
                statusMessage = statusMessage,
                profileImageUri = initialImageUri, // mainProfileImageFile의 URI 또는 null
                placeholderImageRes = R.drawable.example_picture
            )
        )
    }
    var isEditing by remember { mutableStateOf(false) }
    var tempProfileData by remember { mutableStateOf(profileDataState.copy()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri: Uri? ->
        selectedUri?.let { uri ->
            Log.d("ImagePicker", "Selected image URI from gallery: $uri")
            try {
                // 선택된 이미지를 임시 파일로 복사
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(tempProfileImageFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                // tempProfileData의 이미지 URI를 임시 파일 URI로 업데이트 (UI 즉시 반영 위함)
                tempProfileData = tempProfileData.copy(profileImageUri = Uri.fromFile(tempProfileImageFile))
                Log.d("ImagePicker", "Image copied to temp file: ${tempProfileData.profileImageUri}")
            } catch (e: Exception) {
                Log.e("ImagePicker", "Error copying image to temp file", e)
                // (오류 처리 UI 업데이트 등 - 예: 사용자에게 Toast 메시지 표시)
            }
        }
    }

    ProfileScreen(
        profileData = if (isEditing) tempProfileData else profileDataState,
        isEditing = isEditing,
        onEditClick = {
            // 편집 시작 시, 이전 편집에서 남은 임시 파일이 있다면 삭제
            if (tempProfileImageFile.exists()) {
                tempProfileImageFile.delete()
            }
            tempProfileData = profileDataState.copy() // 현재 저장된 상태로 편집 시작
            isEditing = true
        },
        onSaveClick = {
            var finalDataToSave = tempProfileData.copy() // 저장할 데이터 준비 (복사본으로 작업)

            // 사진이 변경되었는지 확인 (tempProfileData의 URI가 임시 파일 URI를 가리키는지)
            if (tempProfileData.profileImageUri == Uri.fromFile(tempProfileImageFile) && tempProfileImageFile.exists()) {
                try {
                    // 임시 파일을 메인 프로필 이미지 파일로 복사 (덮어쓰기)
                    FileInputStream(tempProfileImageFile).use { input ->
                        FileOutputStream(mainProfileImageFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    // 저장할 데이터의 이미지 URI를 메인 프로필 이미지 파일 URI로 업데이트
                    finalDataToSave = finalDataToSave.copy(profileImageUri = Uri.fromFile(mainProfileImageFile))
                    Log.d("onSaveClick", "Copied temp image to main image: ${finalDataToSave.profileImageUri}")
                } catch (e: Exception) {
                    Log.e("onSaveClick", "Error saving image from temp to main", e)
                    // 이미지 저장 실패 시, 현재 profileDataState의 이미지 URI(이전 이미지)를 유지하도록 설정
                    finalDataToSave = finalDataToSave.copy(profileImageUri = profileDataState.profileImageUri)
                }
            }
            // 사진이 변경되지 않았다면, finalDataToSave.profileImageUri는 이미 profileDataState의 URI를 가지고 있거나,
            // tempProfileData에 있던 (변경되지 않은) main file URI를 가리킴.

            profileDataState = finalDataToSave // 실제 상태 업데이트
            isEditing = false

            // SharedPreferences에 최종 데이터 저장
            with(sharedPreferences.edit()) {
                putString(KEY_NAME, profileDataState.name)
                putString(KEY_ARRIVAL_TIME_LABEL, profileDataState.messageArrivalTimeLabel)
                putString(KEY_ARRIVAL_TIME, profileDataState.messageArrivalTime)
                putString(KEY_STATUS_MESSAGE, profileDataState.statusMessage)
                // profileImageUri가 null일 수 있으므로 null을 저장하거나, null이 아닐 때만 toString() 호출
                putString(KEY_IMAGE_URI, profileDataState.profileImageUri?.toString())
                apply()
            }
            Log.d("ProfileScreen", "Profile data saved. Image URI: ${profileDataState.profileImageUri}")

            // 임시 파일 최종 정리
            if (tempProfileImageFile.exists()) {
                tempProfileImageFile.delete()
            }
        },
        onCancelClick = {
            isEditing = false
            // 편집 취소 시 임시 파일 삭제
            if (tempProfileImageFile.exists()) {
                tempProfileImageFile.delete()
                Log.d("onCancelClick", "Deleted temp image file.")
            }
        },
        onProfileImageChangeClick = {
            imagePickerLauncher.launch("image/*")
        },
        onProfileDataChange = { updatedData ->
            if (isEditing) {
                tempProfileData = updatedData
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileData: ProfileData,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onProfileImageChangeClick: () -> Unit,
    onProfileDataChange: (ProfileData) -> Unit
) {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("프로필", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = onSaveClick) { Icon(Icons.Filled.Check, "저장") }
                        IconButton(onClick = onCancelClick) { Icon(Icons.Filled.Close, "취소") }
                    } else {
                        IconButton(onClick = onEditClick) { Icon(Icons.Filled.Edit, "프로필 수정") }
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
                .verticalScroll(rememberScrollState())
        ) {
            HorizontalDivider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            ProfileCard(
                profileData = profileData,
                isEditing = isEditing,
                onProfileImageChangeClick = onProfileImageChangeClick,
                onProfileDataChange = onProfileDataChange
            )
        }
        Box{
            ExpandableFabExample(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCard(
    profileData: ProfileData,
    isEditing: Boolean,
    onProfileImageChangeClick: () -> Unit,
    onProfileDataChange: (ProfileData) -> Unit
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

    if (showTimePicker) {
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
                .padding(top = 100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0EFFF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 160.dp, bottom = 30.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isEditing) {
                    // 1. 이름
                    OutlinedTextField(
                        value = profileData.name,
                        onValueChange = { onProfileDataChange(profileData.copy(name = it)) },
                        label = { Text("이름") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. 메시지 도착 시간 (이름 아래)
                    OutlinedTextField(
                        value = profileData.messageArrivalTime,
                        onValueChange = { /* 직접 수정 방지 */ },
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
                                painter = painterResource(id = R.drawable.clock),
                                contentDescription = "시간 선택",
                                modifier = Modifier.clickable {
                                    focusManager.clearFocus()
                                    showTimePicker = true
                                }
                            )
                        }
                    )
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
                } else { // 보기 모드
                    // 1. 이름
                    Text(profileData.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. 메시지 도착 시간 (이름 아래)
                    Text(profileData.messageArrivalTimeLabel, fontSize = 14.sp, color = Color.Gray)
                    val displayTime = formatDisplayTime(profileData.messageArrivalTime)
                    Text(displayTime, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. 상태 메시지 (메시지 도착 시간 아래)
                    Text(profileData.statusMessage, fontSize = 16.sp, color = Color.DarkGray)
                }
                Spacer(modifier = Modifier.height(if (isEditing) 30.dp else 250.dp))
                if (!isEditing) {
                    Button(
                        onClick = { /* 피드백 보내기 클릭 시 동작 */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDCDCDC)),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
                    ) {
                        Text("피드백 보내기", color = Color.Black)
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopCenter)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable(enabled = isEditing) { onProfileImageChangeClick() }
        ) {
            Image(
                painter = if (profileData.profileImageUri != null) {
                    rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(profileData.profileImageUri)
                            // ObjectKey 대신 memoryCacheKey를 동적으로 변경하여 새로고침 유도
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
                    modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.3f))
                        .clickable { onProfileImageChangeClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("변경", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

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

@Preview(showBackground = true, name = "Default Preview")
@Composable
fun DefaultPreview() {
    SendBackSendBagTheme {
        val navController = rememberNavController()
        ProfileScreenContainer(navController)
    }
}