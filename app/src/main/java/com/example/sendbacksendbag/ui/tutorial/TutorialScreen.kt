package com.example.sendbacksendbag.ui.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.sendbacksendbag.R

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class) // HorizontalPager 사용을 위해 필요
@Composable
fun TutorialScreen(
    onTutorialFinished: () -> Unit // 튜토리얼 완료 시 호출될 콜백 함수
) {
    // 튜토리얼에 사용할 이미지 리소스 ID 목록
    // 여기에 실제 이미지 나열
    val tutorialImages = listOf(
        R.drawable.tutorial_1,
        R.drawable.tutorial_2
    )

    // PagerState를 사용하여 현재 페이지, 페이지 수 등을 관리합니다.
    val pagerState = rememberPagerState(pageCount = { tutorialImages.size })
    val scope = rememberCoroutineScope() // 버튼 클릭 시 페이지 전환을 위해 사용합니다.

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 좌우 스크롤이 가능한 이미지 뷰어
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Image(
                    painter = painterResource(id = tutorialImages[page]),
                    contentDescription = "Tutorial Image ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // 이미지가 화면에 맞게 조절되도록 설정
                )
            }

            // 하단 컨트롤 영역 (인디케이터 + 버튼)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 페이지 인디케이터 (점)
                Row(
                    Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(tutorialImages.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .size(10.dp)
                                .let { // 조건부 배경색 설정
                                    if (pagerState.currentPage == iteration) it.background(Color.DarkGray)
                                    else it.background(Color.LightGray)
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 다음 / 완료 버튼
                Button(
                    onClick = {
                        if (pagerState.currentPage < tutorialImages.size - 1) {
                            // 다음 페이지로 스크롤
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            // 마지막 페이지면 튜토리얼 완료 콜백 호출
                            onTutorialFinished()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (pagerState.currentPage < tutorialImages.size - 1) "다음" else "시작하기")
                }
            }
        }
    }
}

// Composable 미리보기
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun TutorialScreenPreview() {
    // 미리보기에서는 R.drawable 대신 임시 값을 사용해야 할 수 있습니다.
    // 여기서는 실제 리소스가 있다고 가정합니다.
    // 만약 리소스가 없다면 이 부분을 주석 처리하거나 임시 Composable로 대체하세요.
     TutorialScreen(onTutorialFinished = {})
    Text("TutorialScreen Preview (실제 리소스 필요)")
}