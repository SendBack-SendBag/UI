package com.example.sendbacksendbag.ui.login

import android.app.Activity // Activity 임포트 추가
import android.widget.Toast
import androidx.compose.foundation.BorderStroke // BorderStroke 임포트 추가
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // Google 아이콘 사용 시 필요
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.sendbacksendbag.R // Google 아이콘 리소스 ID (예시)
import com.example.sendbacksendbag.authentication.AuthViewModel

// 탭 제목 정의
private val TABS = listOf("로그인", "회원가입")

// ViewModel에서 AuthState를 관찰하여 UI 업데이트
@Composable
fun AuthScreen(navController: NavController,authViewModel: AuthViewModel = viewModel()) {
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    // 에러 메시지 표시
    LaunchedEffect(authState.error) {
        authState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            authViewModel.clearError() // 에러 메시지 표시 후 초기화
        }
    }

    // 로그인 성공 시 처리 (예: 메인 화면으로 이동)
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            Toast.makeText(context, "${authState.userEmail}님, 환영합니다!", Toast.LENGTH_SHORT).show()
            // 여기에 메인 화면으로 이동하는 로직 추가
            navController.navigate("home")
        }
    }

    // *** Google 로그인 요청 처리 ***
//    LaunchedEffect(key1 = authState.requiresGoogleSignIn) {
//        if (authState.requiresGoogleSignIn) {
//            val activity = context as? Activity // Context를 Activity로 캐스팅
//            if (activity == null) {
//                Toast.makeText(context, "Google 로그인을 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
//                // ViewModel 상태를 리셋해야 할 수 있음 (ViewModel에서 오류 처리 시 리셋됨)
//            } else {
//                launch { // LaunchedEffect는 CoroutineScope이므로 launch 사용 가능
//                    try {
////                        val response = authViewModel.signInWithGoogleUsingCredentialManager(activity)
////                        response?.let {
////                            authViewModel.handleGoogleSignInCredential(it.credential)
////                        }
//                        // response가 null인 경우, ViewModel 내부에서 handleGoogleSignInError가 호출되어
//                        // requiresGoogleSignIn이 false로 설정될 것입니다.
//                    } catch (e: Exception) {
//                        // signInWithGoogleUsingCredentialManager 내에서 예외를 처리하지만,
//                        // 만약을 대비하여 여기서도 처리할 수 있습니다.
//                        Toast.makeText(context, "Google 로그인 중 오류: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
//                        // ViewModel에서 오류 처리 및 상태 리셋 필요
//                    }
//                }
//            }
//        }
//    }


    // 로딩 중일 때 로딩 인디케이터 표시
    if (authState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LoginSignUpScreenContent(authViewModel)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginSignUpScreenContent(authViewModel: AuthViewModel) {
    val pagerState = rememberPagerState { TABS.size }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp, vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 탭 구현
        AuthTabs(
            selectedTabIndex = pagerState.currentPage,
            onTabSelected = { index ->
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )

        Spacer(modifier = Modifier.height(50.dp))

        // 2. 페이저 구현
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                when (page) {
                    0 -> LoginForm(authViewModel)
                    1 -> SignUpForm(authViewModel)
                }
            }
        }

//        // 3. Google 로그인 버튼 (수정된 부분)
//        Spacer(modifier = Modifier.height(24.dp))
//        OrDivider()
//        Spacer(modifier = Modifier.height(24.dp))
//        GoogleSignInButton(
//            // pagerState.currentPage가 1이면 (회원가입 탭이면) true를 전달
//            isSignUp = pagerState.currentPage == 1,
//            onClick = {
//                authViewModel.prepareGoogleSignIn()
//            }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
    }
}

// '또는' 구분선 Composable
@Composable
fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        Text(
            text = "또는",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray,
            fontSize = 14.sp
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
    }
}

// Google 로그인 버튼 Composable 수정
@Composable
fun GoogleSignInButton(
    isSignUp: Boolean, // 현재 회원가입 탭인지 여부를 받음
    onClick: () -> Unit
) {
    // isSignUp 값에 따라 버튼 텍스트를 결정
    val buttonText = if (isSignUp) "Google로 회원가입" else "Google로 로그인"

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp), // 둥근 모서리
        border = BorderStroke(1.dp, Color.LightGray), // 테두리 설정
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black,
            containerColor = Color.White // 배경색
        )
    ) {
        // Google 로고 아이콘 (선택 사항)
        // Image(...)

        Text(text = buttonText, fontSize = 16.sp) // 동적으로 결정된 텍스트 사용
    }
}


// --- 기존 LoginForm, SignUpForm, AuthTabs, AuthTextField, AuthButton 등은 변경 없이 유지 ---
// (단, LoginForm/SignUpForm 내의 Column 구조는 LoginSignUpScreenContent에서 처리하므로
//  LoginForm/SignUpForm은 요소만 포함하도록 단순화될 수 있습니다.)

@Composable
fun LoginForm(authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // 요소 간 간격
    ) {
        // 이메일 입력 필드
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "example@example.com"
        )

        // 비밀번호 입력 필드
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Enter Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 로그인 버튼
        AuthButton(
            text = "로그인",
            onClick = {
                authViewModel.loginWithEmailPassword(email, password)
            }
        )

        // 비밀번호 찾기
        TextButton(onClick = { /* TODO: 비밀번호 찾기 구현 */ }) {
            Text(
                text = "Forgot password?",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SignUpForm(authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 이메일 입력 필드
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "example@example.com"
        )

        // 비밀번호 입력 필드
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "Enter Password",
            isPassword = true
        )

        // 비밀번호 확인 입력 필드
        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            placeholder = "Confirm Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 회원가입 버튼
        AuthButton(
            text = "회원가입",
            onClick = {
                if (password == confirmPassword) {
                    authViewModel.signUpWithEmailPassword(email, password)
                } else {
                    Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
fun AuthTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White, // 배경색
        contentColor = Color.Black, // 텍스트 색상
        indicator = { tabPositions ->
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                height = 3.dp,
                color = Color.Black // 선택된 탭 아래 선 색상
            )
        },
        divider = {} // 기본 구분선 제거
    ) {
        TABS.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTabIndex == index) Color.Black else Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = label,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email
        ),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = Color.Black
        ),
        textStyle = TextStyle(fontSize = 16.sp)
    )
}

@Composable
fun AuthButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFF3E0),
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}