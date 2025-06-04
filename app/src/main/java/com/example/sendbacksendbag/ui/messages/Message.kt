package com.example.sendbacksendbag.ui.messages

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val name: String,
    val anonymousName: String = "", // 익명 이름 필드 추가
    val avatarRes: Int,
    val content: String,
    val transformedContent: String,
    val time: String,
    val sendingTime: String,
    val status: String,
    val hasActionButton: Boolean = true
)
