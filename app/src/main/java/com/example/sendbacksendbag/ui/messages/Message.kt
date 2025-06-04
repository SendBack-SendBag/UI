package com.example.sendbacksendbag.ui.messages

import kotlinx.serialization.Serializable


// 이거 가져다가
@Serializable
data class Message(
    val id: String,         // 검색 및 네비게이션에 사용
        val name: String,               // 받는 사람 이름
    val anonymousName: String = "", // 익명 이름 필드 추가
    val avatarRes: Int,                // 아바타 리소스 ID 사용할 일 없음
    val content: String,            // 보낸 메시지
    val transformedContent: String, // 변한된 메시지
    val time: String,               // 보낸 시간
    val sendingTime: String,        // 전송 예정 시각
    val status: String,             // 전송 상태
    val hasActionButton: Boolean = true     // 투표 버튼 보이게 하는 지 여부 -> 필요없음.
)
