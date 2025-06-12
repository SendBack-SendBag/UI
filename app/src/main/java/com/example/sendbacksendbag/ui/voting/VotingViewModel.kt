package com.example.sendbacksendbag.ui.voting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sendbacksendbag.communication.GeminiTranslator
import com.example.sendbacksendbag.data.FriendsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// --- @Serializable 추가 ---
@Serializable
data class CommentData(val author: String, val text: String, val isLoading: Boolean = false)
// --- 추가 끝 ---

@Serializable
data class PollData(
    val id: String,
    val title: String,
    val comments: List<CommentData> = emptyList()
)

class VotingViewModel(
    private val repository: FriendsRepository // FriendsRepository 주입 받도록 수정
) : ViewModel() {

    // --- 랜덤 익명 이름 목록 ---
    private val anonymousNames = listOf(
        "익명의 코알라", "생각하는 펭귄", "노래하는 돌고래", "달리는 거북이",
        "웃는 하마", "용감한 다람쥐", "궁금한 여우", "점프하는 캥거루",
        "수줍은 판다", "똑똑한 부엉이", "날으는 고슴도치", "조용한 고양이",
        "춤추는 고양이", "배고픈 수달", "코딩하는 말", "잠자는 사자",
        "현명한 올빼미", "재빠른 치타", "신비로운 유니콘"
    )
    // 모든 투표 화면 데이터를 관리하는 맵
    private val _pollsData = MutableStateFlow<Map<String, PollData>>(emptyMap())
    val pollsData: StateFlow<Map<String, PollData>> = _pollsData.asStateFlow()

    // 현재 선택된 투표 화면의 ID
    private val _currentPollId = MutableStateFlow<String?>(null)
    val currentPollId: StateFlow<String?> = _currentPollId.asStateFlow()

    // 현재 선택된 투표 화면의 댓글 목록
    val comments : StateFlow<List<CommentData>> = repository.comments

    // 현재 선택된 투표 화면에 댓글 추가
    fun addComment(userInput: String) {
        val randomAuthor = anonymousNames.random()

        viewModelScope.launch {
            val generatedText = GeminiTranslator.newComment(userInput)
            val newComment = CommentData(randomAuthor, generatedText, false)

            // 현재 선택된 투표 화면에 댓글 추가 요청
            repository.addCommentAndSave(newComment)
        }
    }

}