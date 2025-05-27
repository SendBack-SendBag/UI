package com.example.sendbacksendbag.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.sendbacksendbag.R
import com.example.sendbacksendbag.ui.profile.ProfileData
import com.example.sendbacksendbag.ui.voting.CommentData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

// SharedPreferences Keys
private const val PREFS_NAME = "ProfilePrefs"
private const val KEY_NAME = "profile_name"
private const val KEY_ARRIVAL_TIME_LABEL = "profile_arrival_time_label"
private const val KEY_ARRIVAL_TIME = "profile_arrival_time"
private const val KEY_STATUS_MESSAGE = "profile_status_message"
private const val KEY_IMAGE_URI_STRING = "profile_image_uri_string"
private const val MAIN_PROFILE_IMAGE_FILENAME_PREFIX = "profile_image_"
private const val KEY_COMMENTS = "voting_comments" // 댓글 저장을 위한 새 키 추가
private const val FRIENDS_JSON_FILENAME = "friends.json" // JSON 파일 이름


class FriendsRepository(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val friendsJsonFile = File(context.filesDir, FRIENDS_JSON_FILENAME)

    // JSON 처리를 위한 Json 인스턴스 (prettyPrint는 디버깅 시 유용)
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    // '내 프로필'을 위한 StateFlow (기존과 동일)
    private val _myProfile = MutableStateFlow<ProfileData>(loadMyProfile())
    val myProfile: StateFlow<ProfileData> = _myProfile.asStateFlow()

    // '친구 목록'을 위한 StateFlow (JSON에서 로드)
    private val _friends = MutableStateFlow<List<ProfileData>>(loadFriendsFromJson())
    val friends: StateFlow<List<ProfileData>> = _friends.asStateFlow()

    private val _comments = MutableStateFlow<List<CommentData>>(loadComments())
    val comments: StateFlow<List<CommentData>> = _comments.asStateFlow()

    // === 데이터 로드 ===
    // 내 프로필 로드
    private fun loadMyProfile(): ProfileData {
        val name = sharedPreferences.getString(KEY_NAME, "내 이름") ?: "내 이름"
        val arrivalTimeLabel = sharedPreferences.getString(KEY_ARRIVAL_TIME_LABEL, "메시지 도착 시각") ?: "메시지 도착 시각"
        val arrivalTime = sharedPreferences.getString(KEY_ARRIVAL_TIME, "20 : 00") ?: "20 : 00"
        val statusMessage = sharedPreferences.getString(KEY_STATUS_MESSAGE, "오늘도 화이팅!") ?: "오늘도 화이팅!"
        val imageUriString = sharedPreferences.getString(KEY_IMAGE_URI_STRING, null)
        val myProfileImageFile = File(context.filesDir, "${MAIN_PROFILE_IMAGE_FILENAME_PREFIX}me.jpg")
        val imageUri = getValidUri(imageUriString, myProfileImageFile, "me")

        // --- 수정: Uri를 받는 생성자 호출 (profileImageUriString 제거) ---
        return ProfileData(
            id = "me",
            name = name,
            messageArrivalTimeLabel = arrivalTimeLabel,
            messageArrivalTime = arrivalTime,
            statusMessage = statusMessage,
            profileImageUri = imageUri, // Uri만 전달
            placeholderImageRes = R.drawable.example_picture
        )
    }
    // 친구 목록 로드 (JSON 사용)
    private fun loadFriendsFromJson(): List<ProfileData> {
        if (!friendsJsonFile.exists()) {
            Log.w("Repository", "$FRIENDS_JSON_FILENAME not found. Returning empty list.")
            return emptyList()
        }

        return try {
            val jsonString = friendsJsonFile.readText()
            if (jsonString.isBlank()) {
                emptyList()
            } else {
                json.decodeFromString<List<ProfileData>>(jsonString).map { friend ->
                    val imageFile = File(context.filesDir, "${MAIN_PROFILE_IMAGE_FILENAME_PREFIX}${friend.id}.jpg")
                    val validUri = getValidUri(friend.profileImageUriString, imageFile, friend.id ?: "unknown")

                    // --- 수정: profileImageUriString만 copy하고, Uri는 get 프로퍼티에 맡김 ---
                    friend.copy(
                        profileImageUriString = validUri?.toString()
                    ).apply {
                        // `init` 블록이나 `get` 프로퍼티가 Uri를 처리하므로,
                        // 여기서 `profileImageUri`를 명시적으로 설정할 필요가 없을 수 있습니다.
                        // 만약 명시적 설정이 필요하다면 아래 라인 추가:
                        this.profileImageUri = validUri
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error loading friends from JSON", e)
            emptyList()
        }
    }
    // 친구 목록 JSON 파일 저장
    private fun saveFriendsToJson(friendsList: List<ProfileData>) {
        try {
            val jsonString = json.encodeToString(friendsList)
            friendsJsonFile.writeText(jsonString)
            Log.d("Repository", "Friends saved to $FRIENDS_JSON_FILENAME")
        } catch (e: Exception) {
            Log.e("Repository", "Error saving friends to JSON", e)
        }
    }


    private fun getValidUri(uriString: String?, file: File, userId: String): Uri? {
        return uriString?.let {
            try {
                val parsedUri = Uri.parse(it)
                if (parsedUri.scheme == "file" && parsedUri.path == file.absolutePath && file.exists()) {
                    Uri.fromFile(file)
                } else if (parsedUri.scheme == "file" && parsedUri.path != null) {
                    val genericFile = File(parsedUri.path!!)
                    if (genericFile.exists()) parsedUri else {
                        Log.w("Repository", "Image file does not exist: ${genericFile.absolutePath}, for $userId. Clearing URI.")
                        null
                    }
                } else {
                    Log.w("Repository", "Invalid or non-file URI found for $userId: $it")
                    null // content URI 등 다른 스킴은 현재 지원 안 함 (필요시 추가)
                }
            } catch (e: Exception) {
                Log.e("Repository", "Failed to parse or check URI for '$userId': $it", e)
                null
            }
        }
    }


    // --- 데이터 업데이트 ---
    fun updateMyProfile(updatedProfile: ProfileData) {
        _myProfile.value = updatedProfile
        // SharedPreferences에 저장
        with(sharedPreferences.edit()) {
            putString(KEY_NAME, updatedProfile.name)
            putString(KEY_ARRIVAL_TIME_LABEL, updatedProfile.messageArrivalTimeLabel)
            putString(KEY_ARRIVAL_TIME, updatedProfile.messageArrivalTime)
            putString(KEY_STATUS_MESSAGE, updatedProfile.statusMessage)
            putString(KEY_IMAGE_URI_STRING, updatedProfile.profileImageUriString)
            apply()
        }
        Log.d("Repository", "My profile updated and saved.")
    }

    // 친구 정보 업데이트 (JSON 저장 호출 추가)
    fun updateFriend(updatedFriend: ProfileData) {
        _friends.update { currentFriends ->
            val updatedList = currentFriends.map { friend ->
                if (friend.id == updatedFriend.id) {
                    updatedFriend
                } else {
                    friend
                }
            }
            saveFriendsToJson(updatedList) // 변경된 목록을 JSON 파일에 저장
            updatedList // 업데이트된 목록 반환
        }
        Log.d("Repository", "Friend updated: ${updatedFriend.id}")
    }

    private fun saveFriendDataToPersistence(friend: ProfileData) {
        val friendPrefs = context.getSharedPreferences("FriendPrefs_${friend.id}", Context.MODE_PRIVATE)
        with(friendPrefs.edit()) {
            putString("name", friend.name)
            putString("status", friend.statusMessage)
            putString("time", friend.messageArrivalTime)
            putString("image_uri", friend.profileImageUriString)
            apply()
        }
    }


    fun getFriendById(userId: String): ProfileData? {
        return _friends.value.find { it.id == userId }
    }

    // 파일 복사 및 URI 반환 헬퍼 함수
    fun copyUriToInternalStorage(uri: Uri, targetFile: File): Uri? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Uri.fromFile(targetFile)
        } catch (e: Exception) {
            Log.e("Repository", "Error copying URI $uri to $targetFile", e)
            null
        }
    }

    /**
     * SharedPreferences에서 댓글 목록을 로드합니다.
     */
    private fun loadComments(): List<CommentData> {
        val jsonString = sharedPreferences.getString(KEY_COMMENTS, null)
        return if (jsonString != null) {
            try {
                json.decodeFromString<List<CommentData>>(jsonString)
            } catch (e: Exception) {
                Log.e("Repository", "Error loading comments from SharedPreferences", e)
                emptyList()
            }
        } else {
            // 초기 댓글 데이터 (기존 ViewModel에 있던 데이터)
            listOf(
                CommentData("춤추는 고양이", "가끔씩 그런 점이 있는듯"),
                CommentData("배고픈 수달", "인정"),
                CommentData("코딩하는 말", "난 아닌거 같던데")
            )
        }
    }
    /**
     * 댓글 목록을 SharedPreferences에 저장합니다.
     */
    private fun saveComments(commentsList: List<CommentData>) {
        try {
            val jsonString = json.encodeToString(commentsList)
            with(sharedPreferences.edit()) {
                putString(KEY_COMMENTS, jsonString)
                apply()
            }
            Log.d("Repository", "Comments saved to SharedPreferences.")
        } catch (e: Exception) {
            Log.e("Repository", "Error saving comments to SharedPreferences", e)
        }
    }

    /**
     * 새 댓글을 추가하고 전체 목록을 저장합니다.
     */
    fun addCommentAndSave(comment: CommentData) {
        _comments.update { currentComments ->
            // AI 댓글이므로 맨 위에 추가
            val updatedList = listOf(comment) + currentComments
            saveComments(updatedList) // 변경된 목록 저장
            updatedList // 업데이트된 목록 반환
        }
    }
}