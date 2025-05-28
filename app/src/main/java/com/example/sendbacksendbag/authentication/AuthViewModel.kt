package com.example.sendbacksendbag.authentication

import android.content.Context
import android.util.Log
import androidx.credentials.*
import androidx.credentials.exceptions.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sendbacksendbag.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
// import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential // 사용 안 함
import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.auth.GoogleAuthProvider // 사용 안 함
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userEmail: String? = null,
    val requiresGoogleSignIn: Boolean = false, // Google 로그인 관련 (현재 주석 처리됨)
    val authCheckCompleted: Boolean = false // 인증 상태 확인 완료 여부
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var credentialManager: CredentialManager

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var WEB_CLIENT_ID: String = ""

    fun init(context: Context) {
        // credentialManager는 Google One Tap 등 Credential Manager API 사용 시 필요
        // 현재 Google 로그인이 주석 처리되어 있으므로, 필수 초기화는 아닐 수 있음
        if (!::credentialManager.isInitialized) {
            credentialManager = CredentialManager.create(context)
        }
        try {
            WEB_CLIENT_ID = context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Failed to get WEB_CLIENT_ID. Google Sign-In might not work.", e)
        }
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        _authState.update {
            it.copy(
                isLoggedIn = currentUser != null,
                userEmail = currentUser?.email,
                authCheckCompleted = true // 현재 사용자 확인 완료
            )
        }
    }

    fun loginWithEmailPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.update { it.copy(error = "이메일과 비밀번호를 입력해주세요.") }
            return
        }
        _authState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.update {
                    AuthState( // 성공 시 상태를 완전히 새로 설정 (authCheckCompleted 포함)
                        isLoggedIn = true,
                        userEmail = auth.currentUser?.email,
                        authCheckCompleted = true
                    )
                }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "로그인 실패",
                        authCheckCompleted = true // 시도 후 확인 완료
                    )
                }
            }
        }
    }

    fun signUpWithEmailPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.update { it.copy(error = "이메일과 비밀번호를 입력해주세요.") }
            return
        }
        _authState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.update {
                    AuthState( // 성공 시 상태를 완전히 새로 설정
                        isLoggedIn = true,
                        userEmail = auth.currentUser?.email,
                        authCheckCompleted = true
                    )
                }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "회원가입 실패",
                        authCheckCompleted = true // 시도 후 확인 완료
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
            // Google One Tap 사용 시 clearCredentialState 호출
            if (::credentialManager.isInitialized && WEB_CLIENT_ID.isNotBlank()) { // WEB_CLIENT_ID 체크 추가
                try {
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: ClearCredentialException) {
                    Log.e("AuthViewModel", "Google 로그아웃 실패 (ClearCredentialState): ${e.message}")
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Google 로그아웃 중 일반 오류 (ClearCredentialState): ${e.message}")
                }
            }
            // 로그아웃 시 초기 상태로 리셋 (authCheckCompleted는 false가 되어 splash에서 다시 로직을 탈 수 있게 함)
            _authState.value = AuthState(authCheckCompleted = false) // 혹은 true로 유지하고 isLoggedIn만 false로 할지 정책에 따라 결정
                                                                    // 여기서는 false로 하여 Splash가 다시 동작하도록 유도
        }
    }

    fun clearError() {
        _authState.update { it.copy(error = null) }
    }

    fun getGoogleIdSignInOptions(): GetGoogleIdOption? {
        if (WEB_CLIENT_ID.isBlank()) {
            Log.e("AuthViewModel", "WEB_CLIENT_ID is not set. Cannot get Google ID Sign-In Options.")
            _authState.update { it.copy(error = "Google 로그인을 설정할 수 없습니다. (클라이언트 ID 오류)") }
            return null
        }
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(false) // 자동 선택 비활성화 권장
            .build()
    }
}