package com.example.sendbacksendbag.ui.profile

import android.net.Uri // Uri import 추가
import com.example.sendbacksendbag.R

// 데이터 클래스
data class ProfileData(
    val id: String = "user123",
    var name: String = "박지열",
    var messageArrivalTimeLabel: String = "메시지 도착 시각",
    var messageArrivalTime: String = "08 : 00", // HH : mm 형식
    var statusMessage: String = "오늘도 화이팅!",
    var profileImageUri: Uri? = null,
    val placeholderImageRes: Int = R.drawable.example_picture
)