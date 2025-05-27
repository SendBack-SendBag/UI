package com.example.sendbacksendbag.authentication
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.*
import androidx.credentials.exceptions.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sendbacksendbag.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userEmail: String? = null,
    val requiresGoogleSignIn: Boolean = false // Google 로그인 요청 상태 추가
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var credentialManager: CredentialManager // CredentialManager 인스턴스

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private lateinit var WEB_CLIENT_ID: String

    fun init(context: Context) {
        credentialManager = CredentialManager.create(context)
//        WEB_CLIENT_ID = context.getString(R.string.default_web_client_id)
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        _authState.update {
            it.copy(
                isAuthenticated = currentUser != null,
                userEmail = currentUser?.email
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
                _authState.update { AuthState(isAuthenticated = true, userEmail = auth.currentUser?.email) }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "로그인 실패"
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
                _authState.update { AuthState(isAuthenticated = true, userEmail = auth.currentUser?.email) }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "회원가입 실패"
                    )
                }
            }
        }
    }

    // Google 로그인 시작
//    fun prepareGoogleSignIn() {
//        _authState.update { it.copy(isLoading = true, error = null, requiresGoogleSignIn = true) }
//    }

//    // Activity/Fragment에서 Credential Manager 결과를 받았을 때 호출
//    fun handleGoogleSignInCredential(credential: Credential) {
//        _authState.update { it.copy(requiresGoogleSignIn = false) } // Google 로그인 요청 상태 해제
//        if (credential is GoogleIdTokenCredential) {
//            val googleIdToken = credential.idToken
//            firebaseAuthWithGoogle(googleIdToken)
//        } else {
//            _authState.update {
//                it.copy(
//                    isLoading = false,
//                    error = "Google 로그인에 실패했습니다. (잘못된 Credential 타입)"
//                )
//            }
//        }
//    }
//
//    fun handleGoogleSignInError(e: GetCredentialException) {
//        _authState.update { it.copy(requiresGoogleSignIn = false) } // Google 로그인 요청 상태 해제
//        val errorMessage = when (e) {
//            is GetCredentialCancellationException -> "Google 로그인이 사용자에 의해 취소되었습니다."
//            is GetCredentialInterruptedException -> "Google 로그인 중 인터럽트가 발생했습니다."
//            is GetCredentialUnsupportedException -> "Google 로그인이 지원되지 않는 기기입니다."
//            is GetCredentialUnknownException -> "Google 로그인 중 알 수 없는 오류가 발생했습니다."
//            is NoCredentialException -> "저장된 Google 계정을 찾을 수 없습니다. 계정을 추가하거나 다른 로그인 방식을 시도해주세요."
//            else -> "Google 로그인 실패: ${e.localizedMessage}"
//        }
//        _authState.update {
//            it.copy(
//                isLoading = false,
//                error = errorMessage
//            )
//        }
//    }
//
//
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        viewModelScope.launch {
//            try {
//                val credential = GoogleAuthProvider.getCredential(idToken, null)
//                auth.signInWithCredential(credential).await()
//                _authState.update { AuthState(isAuthenticated = true, userEmail = auth.currentUser?.email) }
//            } catch (e: Exception) {
//                _authState.update {
//                    it.copy(
//                        isLoading = false,
//                        error = "Firebase Google 인증 실패: ${e.localizedMessage}"
//                    )
//                }
//            }
//        }
//    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
            // Credential Manager를 사용한 Google 로그아웃 (선택적: 앱에서 완전히 로그아웃하여 다음 로그인 시 계정 선택을 다시 표시)
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: ClearCredentialException) {
                Log.e("AuthViewModel", "Google 로그아웃 실패 (ClearCredentialState): ${e.message}")
            }
            _authState.value = AuthState()
        }
    }

    fun clearError() {
        _authState.update { it.copy(error = null) }
    }

    // ViewModel에서 CredentialManager를 직접 호출하기 위한 GetGoogleIdOption 생성
    fun getGoogleIdSignInOptions(): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // false로 설정하여 계정 선택기를 표시할 수 있습니다. true면 자동 로그인 시도.
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(false) // 명시적으로 false로 하여 항상 계정 선택기가 뜨도록 할 수 있습니다.
            .build()
    }

    // ViewModel에서 CredentialManager를 직접 호출
//    suspend fun signInWithGoogleUsingCredentialManager(activity: Activity): GetCredentialResponse? {
//        val googleIdOption: GetGoogleIdOption = getGoogleIdSignInOptions()
//        val request: GetCredentialRequest = GetCredentialRequest.Builder()
//            .addCredentialOption(googleIdOption)
//            .build()
//
//        return try {
//            credentialManager.getCredential(activity, request) // activity context 필요
//        } catch (e: GetCredentialException) {
//            handleGoogleSignInError(e)
//            null
//        }
//    }
}