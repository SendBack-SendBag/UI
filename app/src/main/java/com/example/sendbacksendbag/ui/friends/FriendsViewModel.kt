package com.example.sendbacksendbag.ui.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sendbacksendbag.R
import com.example.sendbacksendbag.data.FriendsRepository
import com.example.sendbacksendbag.ui.profile.ProfileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ProfileData에 phoneNumber 필드 추가 (FriendsRepository에서도 수정 필요할 수 있음)
// 여기서는 ViewModel의 가상 데이터에만 임시로 추가합니다.
data class UserWithPhone(
    val profile: ProfileData,
    val phoneNumber: String
)

class FriendsViewModel(private val repository: FriendsRepository) : ViewModel() {

    // --- 가상 사용자 데이터 (전화번호 포함) ---
    // 이 목록은 친구 추가 시 존재 여부 확인에 더 이상 사용되지 않습니다.
    private val allUsersWithPhone = listOf(
        UserWithPhone(ProfileData(id = "user1", name = "김민준", statusMessage = "여행 가고 싶다", placeholderImageRes = R.drawable.example_picture), "010-1111-1111"),
        UserWithPhone(ProfileData(id = "user2", name = "이서연", statusMessage = "독서 중", placeholderImageRes = R.drawable.example_picture), "010-2222-2222"),
        UserWithPhone(ProfileData(id = "user3", name = "박서준", statusMessage = "운동!", placeholderImageRes = R.drawable.example_picture), "010-3333-3333"),
        UserWithPhone(ProfileData(id = "rabbit", name = "잠만 자는 토끼", statusMessage = "쿨쿨", placeholderImageRes = R.drawable.example_picture), "010-4444-4444"),
        UserWithPhone(ProfileData(id = "horse", name = "코딩하는 말", statusMessage = "타닥타닥", placeholderImageRes = R.drawable.example_picture), "010-5555-5555")
    )

    private val _searchResults = MutableStateFlow<List<ProfileData>>(emptyList())
    val searchResults: StateFlow<List<ProfileData>> = _searchResults.asStateFlow()

    val friends: StateFlow<List<ProfileData>> = repository.friends


    // --- 연락처로 친구 추가 로직 ---
    fun addFriendByContact(name: String, phoneNumber: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val formattedPhoneNumber = phoneNumber.replace("-", "") // 하이픈 제거

            // 새로운 친구의 ID로 포맷된 전화번호를 사용합니다.
            // 이 ID는 친구 목록 내에서 고유해야 합니다.
            val newFriendId = formattedPhoneNumber

            // 이미 친구 목록에 해당 ID의 사용자가 있는지 확인합니다.
            val existingFriend = repository.friends.value.find { it.id == newFriendId }
            if (existingFriend != null) {
                onResult(false, "${existingFriend.name}님은 이미 등록된 친구입니다.")
                return@launch
            }

            // 새 친구 ProfileData 객체를 생성합니다.
            // statusMessage와 placeholderImageRes에 대한 기본값을 설정합니다.
            // R.drawable.ic_default_profile과 같은 기본 프로필 이미지를 drawable 리소스에 추가해야 합니다.
            val newFriendProfile = ProfileData(
                id = newFriendId, // 전화번호를 ID로 사용
                name = name,
                statusMessage = "건강한 피드백 부탁해요!", // 기본 상태 메시지 (예: 비워두거나 "새로운 친구")
                placeholderImageRes = R.drawable.example_picture// 기본 프로필 이미지 리소스 ID (실제 리소스로 교체 필요)
            )

            // Repository를 통해 친구를 추가합니다.
            repository.addFriend(newFriendProfile)
            onResult(true, "${newFriendProfile.name}님을 친구로 추가했습니다.")
        }
    }
}

// ... (FriendsViewModelFactory는 이전과 동일) ...
class FriendsViewModelFactory(private val repository: FriendsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}