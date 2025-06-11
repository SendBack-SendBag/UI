package com.example.sendbacksendbag.data

import android.content.Context
import android.util.Log
import com.example.sendbacksendbag.Message
import com.example.sendbacksendbag.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import java.io.File

class MessageRepository(private val context: Context) {

    // 익명 이름 목록 추가
    private val anonymousNames = listOf(
        "익명의 코알라", "생각하는 펭귄", "노래하는 돌고래", "달리는 거북이",
        "웃는 하마", "용감한 다람쥐", "궁금한 여우", "점프하는 캥거루",
        "수줍은 판다", "똑똑한 부엉이", "날으는 고슴도치", "조용한 고양이",
        "춤추는 고양이", "배고픈 수달", "코딩하는 말", "잠자는 사자",
        "현명한 올빼미", "재빠른 치타", "신비로운 유니콘", "행복한 토끼",
        "활기찬 햄스터", "꿈꾸는 기린", "날씬한 악어", "열정적인 원숭이"
    )

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val sentMessagesFile = File(context.filesDir, "sent_messages.json")
    private val receivedMessagesFile = File(context.filesDir, "received_messages.json")

    // 메시지 상태 관리
    private val _sentMessages = MutableStateFlow<List<Message>>(loadSentMessages())
    val sentMessages: StateFlow<List<Message>> = _sentMessages.asStateFlow()

    private val _receivedMessages = MutableStateFlow<List<Message>>(loadReceivedMessages())
    val receivedMessages: StateFlow<List<Message>> = _receivedMessages.asStateFlow()

    // 초기 메시지 로딩
    private fun loadSentMessages(): List<Message> {
        if (!sentMessagesFile.exists()) {
            // 초기 샘플 데이터 반환
            return listOf(
                Message(
                    id = "msg1",
                    name = "박지열",
                    anonymousName = getRandomAnonymousName(),
                    avatarRes = R.drawable.example,
                    content = "네 말을 끝까지 듣도록 노력할게",
                    transformedContent = "네 말을 끝까지 듣도록 노력할게요. 다음부터는 경청하겠습니다.",
                    time = "오후 1:33",
                    sendingTime = "오후 8시",
                    status = "전송됨",
                    hasActionButton = true
                )
            )
        }

        return try {
            val jsonString = sentMessagesFile.readText()
            if (jsonString.isBlank()) {
                emptyList()
            } else {
                json.decodeFromString<List<Message>>(jsonString)
            }
        } catch (e: Exception) {
            Log.e("MessageRepository", "Error loading sent messages", e)
            emptyList()
        }
    }

    private fun loadReceivedMessages(): List<Message> {
        if (!receivedMessagesFile.exists()) {
            // 초기 샘플 데이터 반환
            return listOf(
                Message(
                    id = "msg2",
                    name = "이승주",
                    anonymousName = getRandomAnonymousName(),
                    avatarRes = R.drawable.example,
                    content = "회의 시간에 핸드폰 좀 그만 봐",
                    transformedContent = "회의 시간에 집중하면 더 좋을 것 같아요.",
                    time = "오후 3:34",
                    sendingTime = "오후 8시",
                    status = "전송됨",
                    hasActionButton = true
                )
            )
        }

        return try {
            val jsonString = receivedMessagesFile.readText()
            if (jsonString.isBlank()) {
                emptyList()
            } else {
                val messages = json.decodeFromString<List<Message>>(jsonString)
                // 오래된 메시지 형식은 anonymousName이 없을 수 있으므로 확인
                messages.map { message ->
                    if (message.anonymousName.isNullOrEmpty()) {
                        message.copy(anonymousName = getRandomAnonymousName())
                    } else {
                        message
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MessageRepository", "Error loading received messages", e)
            emptyList()
        }
    }

    // 메시지 저장
    private fun saveSentMessages(messages: List<Message>) {
        try {
            val jsonString = json.encodeToString(messages)
            sentMessagesFile.writeText(jsonString)
            Log.d("MessageRepository", "Sent messages saved successfully")
        } catch (e: Exception) {
            Log.e("MessageRepository", "Error saving sent messages", e)
        }
    }

    private fun saveReceivedMessages(messages: List<Message>) {
        try {
            val jsonString = json.encodeToString(messages)
            receivedMessagesFile.writeText(jsonString)
            Log.d("MessageRepository", "Received messages saved successfully")
        } catch (e: Exception) {
            Log.e("MessageRepository", "Error saving received messages", e)
        }
    }

    // 랜덤 익명 이름 생성
    fun getRandomAnonymousName(): String {
        return anonymousNames.random()
    }

    // 메시지 추가
    fun addSentMessage(message: Message) {
        _sentMessages.update { currentList ->
            val updatedList = currentList + message
            saveSentMessages(updatedList)
            updatedList
        }
    }

    fun addReceivedMessage(message: Message) {
        _receivedMessages.update { currentList ->
            val updatedList = currentList + message
            saveReceivedMessages(updatedList)
            updatedList
        }
    }
    // 저장된 메시지를 파일에서 다시 로드하는 함수
    fun refreshMessages() {
        _sentMessages.value = loadSentMessages()
        _receivedMessages.value = loadReceivedMessages()
    }
    // ID로 메시지 조회
    fun getSentMessageById(id: String): Message? {
        return _sentMessages.value.find { it.id == id }
    }

    fun getReceivedMessageById(id: String): Message? {
        return _receivedMessages.value.find { it.id == id }
    }

    // 상태 업데이트
    fun updateMessageStatus(messageId: String, newStatus: String) {
        // 보낸 메시지 확인
        _sentMessages.update { currentList ->
            val updated = currentList.map {
                if (it.id == messageId) it.copy(status = newStatus) else it
            }
            saveSentMessages(updated)
            updated
        }

        // 받은 메시지 확인
        _receivedMessages.update { currentList ->
            val updated = currentList.map {
                if (it.id == messageId) it.copy(status = newStatus) else it
            }
            saveReceivedMessages(updated)
            updated
        }
    }

    // 시간 포맷 변환 함수
    fun formatDisplayTime(timeString: String): String {
        return try {
            val parts = timeString.split(" : ")
            if (parts.size == 2) {
                val hour = parts[0].trim().toInt()
                val minute = parts[1].trim().toInt()

                val amPm = if (hour < 12) "오전" else "오후"
                val displayHour = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }

                "$amPm $displayHour:${minute.toString().padStart(2, '0')}"
            } else {
                timeString
            }
        } catch (e: Exception) {
            timeString
        }
    }
}