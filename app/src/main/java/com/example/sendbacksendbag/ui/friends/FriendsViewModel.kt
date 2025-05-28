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
    private val allUsersWithPhone = listOf(
        UserWithPhone(ProfileData(id = "user1", name = "김민준", statusMessage = "여행 가고 싶다", placeholderImageRes = R.drawable.example), "010-1111-1111"),
        UserWithPhone(ProfileData(id = "user2", name = "이서연", statusMessage = "독서 중", placeholderImageRes = R.drawable.example2), "010-2222-2222"),
        UserWithPhone(ProfileData(id = "user3", name = "박서준", statusMessage = "운동!", placeholderImageRes = R.drawable.example_picture), "010-3333-3333"),
        UserWithPhone(ProfileData(id = "rabbit", name = "잠만 자는 토끼", statusMessage = "쿨쿨", placeholderImageRes = R.drawable.example2), "010-4444-4444"),
        UserWithPhone(ProfileData(id = "horse", name = "코딩하는 말", statusMessage = "타닥타닥", placeholderImageRes = R.drawable.example2), "010-5555-5555")
    )

    private val _searchResults = MutableStateFlow<List<ProfileData>>(emptyList())
    val searchResults: StateFlow<List<ProfileData>> = _searchResults.asStateFlow()

    val friends: StateFlow<List<ProfileData>> = repository.friends


    // --- 연락처로 친구 추가 로직 ---
    fun addFriendByContact(name: String, phoneNumber: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val formattedPhoneNumber = phoneNumber.replace("-", "") // 하이픈 제거

            // 가상 사용자 목록에서 이름과 전화번호가 일치하는 사용자 찾기
            val userToAdd = allUsersWithPhone.find {
                it.profile.name == name && it.phoneNumber.replace("-", "") == formattedPhoneNumber
            }

            if (userToAdd == null) {
                onResult(false, "일치하는 사용자를 찾을 수 없습니다.")
                return@launch
            }

            // 이미 친구인지 확인
            val isAlreadyFriend = repository.friends.value.any { it.id == userToAdd.profile.id }
            if (isAlreadyFriend) {
                onResult(false, "이미 등록된 친구입니다.")
                return@launch
            }

            // 친구 추가
            repository.addFriend(userToAdd.profile)
            onResult(true, "${userToAdd.profile.name}님을 친구로 추가했습니다.")
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