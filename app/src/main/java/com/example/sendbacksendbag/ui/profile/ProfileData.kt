package com.example.sendbacksendbag.ui.profile

import android.net.Uri
import com.example.sendbacksendbag.R // R 클래스 import 확인
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ProfileData(
    val id: String?,
    var name: String,
    var messageArrivalTimeLabel: String = "메시지 도착시간",
    var messageArrivalTime: String = "20 : 00",
    var statusMessage: String = "",
    var profileImageUriString: String? = null, // URI를 문자열로 저장 (JSON 직렬화 대상)
    val placeholderImageRes: Int = R.drawable.example_picture // 기본 플레이스홀더 사용
) {
    @Transient
    private var _profileImageUri: Uri? = null // 실제 Uri 값을 저장할 내부 (private) 백킹 필드

    @Transient
    var profileImageUri: Uri?
        get() {
            // _profileImageUri가 설정되지 않았고, profileImageUriString이 있다면 파싱해서 설정
            if (_profileImageUri == null && profileImageUriString != null) {
                _profileImageUri = try {
                    Uri.parse(profileImageUriString)
                } catch (e: Exception) {
                    null
                }
            }
            return _profileImageUri
        }
        set(value) {
            // Uri 설정 시 내부 필드와 String 필드 모두 업데이트
            _profileImageUri = value
            profileImageUriString = value?.toString()
        }

    init {
        // 객체 생성(및 역직렬화) 시 String -> Uri 변환을 시도하여 _profileImageUri 초기화
        // 여기서 profileImageUri (set 접근자)를 호출하여 _profileImageUri와 profileImageUriString을 동기화
        profileImageUri = profileImageUriString?.let {
            try {
                Uri.parse(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    // Uri를 직접 받는 보조 생성자
    constructor(
        id: String?,
        name: String,
        messageArrivalTimeLabel: String = "메시지 도착시간",
        messageArrivalTime: String = "20 : 00",
        statusMessage: String = "",
        profileImageUri: Uri?, // 이 파라미터는 Uri 타입
        placeholderImageRes: Int = R.drawable.example_picture
    ) : this(
        id,
        name,
        messageArrivalTimeLabel,
        messageArrivalTime,
        statusMessage,
        profileImageUri?.toString(), // String으로 변환하여 주 생성자 호출
        placeholderImageRes
        // init 블록에서 profileImageUri (커스텀 setter)를 통해 _profileImageUri가 설정됨
    ) {
        // 주 생성자 호출 후, 명시적으로 커스텀 setter를 통해 _profileImageUri도 설정
        this.profileImageUri = profileImageUri
    }
}