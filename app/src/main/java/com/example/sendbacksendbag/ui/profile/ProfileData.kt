package com.example.sendbacksendbag.ui.profile

import android.net.Uri // Uri import 추가
import com.example.sendbacksendbag.R

// 데이터 클래스
data class ProfileData(
    val id: String?,
    var name: String,
    var messageArrivalTimeLabel: String = "메시지 도착시간",
    var messageArrivalTime: String = "20 : 00",
    var statusMessage: String = "",
    var profileImageUri: Uri?,
    val placeholderImageRes: Int = R.drawable.example_picture
)