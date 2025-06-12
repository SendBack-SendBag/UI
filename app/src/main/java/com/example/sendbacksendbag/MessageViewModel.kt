package com.example.sendbacksendbag

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sendbacksendbag.communication.GeminiTranslator
import com.example.sendbacksendbag.data.MessageRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*

class MessageViewModel(application: Application) : AndroidViewModel(application) {

    private val messageRepository = MessageRepository(application.applicationContext)

    // 분리된 메시지 리스트 제공
    val sentMessages = messageRepository.sentMessages
    val receivedMessages = messageRepository.receivedMessages

    // 기존 통합 메시지 목록은 유지 (이전 코드와의 호환성 위해)
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    // 로딩 및 오류 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _messageSent = MutableStateFlow(false)
    val messageSent: StateFlow<Boolean> = _messageSent

    internal val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadAllMessages()
    }

    internal fun loadAllMessages() {
        viewModelScope.launch {
            // Repository의 두 StateFlow를 결합하여 하나의 목록으로 만듦
            combine(
                messageRepository.sentMessages,
                messageRepository.receivedMessages
            ) { sent, received ->
                (sent + received).sortedByDescending { it.time }
            }.collect { combinedMessages ->
                _messages.value = combinedMessages
            }
        }
    }


    fun sendMessage(receiverName: String, content: String, sendingTime: String): Job {
        return viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _messageSent.value = false // 초기화

            try {
                // 기존 코드
                val calendar = Calendar.getInstance()
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                val currentMinute = calendar.get(Calendar.MINUTE)
                val currentTimeString = "$currentHour : $currentMinute"
                val formattedTime = messageRepository.formatDisplayTime(currentTimeString)

                val anonymousName = messageRepository.getRandomAnonymousName()

                val transformedContent = GeminiTranslator.send(content)

                val newMessage = Message(
                    id = UUID.randomUUID().toString(),
                    name = receiverName,
                    anonymousName = anonymousName,
                    avatarRes = R.drawable.example_picture,
                    content = content,
                    transformedContent = transformedContent,
                    time = formattedTime,
                    sendingTime = sendingTime,
                    status = "전송됨"
                )

                // Repository에 메시지 추가
                messageRepository.addSentMessage(newMessage)

                messageRepository.addReceivedMessage(newMessage)
                // 전송 완료 알림
                _messageSent.value = true

            } catch (e: Exception) {
                _error.value = "메시지 전송 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    // 메시지 상태 업데이트
    fun updateMessageStatus(messageId: String, newStatus: String) {
        messageRepository.updateMessageStatus(messageId, newStatus)
    }

    // 메시지를 파일에서 다시 로드하는 함수
    fun refreshMessages() {
        viewModelScope.launch {
            messageRepository.refreshMessages()
        }
    }

    fun resetMessageSent() {
        _messageSent.value = false
    }

    // 오류 삭제
    fun clearError() {
        _error.value = null
    }
}