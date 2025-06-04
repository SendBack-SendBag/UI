package com.example.sendbacksendbag.communication

import android.util.Log // 로그 출력을 위해 추가
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect // Flow 수집을 위해 추가
import java.lang.StringBuilder // StringBuilder 사용을 위해 추가


@Composable
fun SendBack(
    input: String
): Flow<GenerateContentResponse> {
    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-pro") // 모델 이름 확인 (기존 코드 유지)

    val prompt = """You will be provided with feedback text:
{feedback = $input}

Follow these steps to transform the feedback:

1.  **Anonymize:** Remove or change any information that could reveal the author's identity (e.g., specific names, locations, or events only the author would know).
2.  **Soften Language:**
    *   Replace any 모욕적인 표현 (insulting expressions) or 욕설 (swear words) with neutral or positive alternatives.
    *   Rephrase any 상처줄 수 있는 표현 (potentially hurtful statements) to be more 부드럽게 (gentle) and 건설적 (constructive).
3.  **Preserve Message:** Ensure the 요점 (key points) of the feedback are accurately 전달 (conveyed) in the transformed text. The meaning of the feedback should remain the same.
4.  **Language:** Use 한국어 (Korean) as the primary language. If necessary for clarity, you may use other languages.
5.  **Output:** Return only the transformed feedback text.

Example:

Input:
"야, 김XX 너 진짜 일 못한다. 그따위로 할거면 그냥 나가. 완전 짜증나!"

Output:
"김XX님의 업무 방식에 개선이 필요한 부분이 있습니다. 함께 협력하여 더 나은 결과를 만들 수 있도록 노력하면 좋겠습니다.
"""

    val response = model.generateContentStream(prompt)

    return response
}

@Composable
fun Comment(
    input: String
): Flow<GenerateContentResponse> {
    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-pro") // 모델 이름 확인 (기존 코드 유지)

    val prompt = """You will be provided with feedback text:
        {
        feedback_text
        }
        
        Follow these steps to transform the feedback:
        
        1.  **Anonymize:** Remove or change any information that could reveal the author's identity (e.g., specific names, locations, or events only the author would know).
        2.  **Soften Language:**
            *   Replace any 모욕적인 표현 (insulting expressions) or 욕설 (swear words) with neutral or positive alternatives.
            *   Rephrase any 상처줄 수 있는 표현 (potentially hurtful statements) to be more 부드럽게 (gentle) and 건설적 (constructive).
        3.  **Preserve Message:** Ensure the 요점 (key points) of the feedback are accurately 전달 (conveyed) in the transformed text. The meaning of the feedback should remain the same.
        4.  **Language:** Use 한국어 (Korean) as the primary language. If necessary for clarity, you may use other languages.
        5.  **Output:** Return only the transformed feedback text.
        
        Example:
        
        Input:
        "야, 김XX 너 진짜 일 못한다. 그따위로 할거면 그냥 나가. 완전 짜증나!"
        
        Output:
        "업무에 대한 피드백입니다. 김XX님의 업무 방식에 개선이 필요한 부분이 있습니다. 함께 협력하여 더 나은 결과를 만들 수 있도록 노력하면 좋겠습니다.
        
        input = $input
        """
    val response = model.generateContentStream(prompt)

    return response
}

// --- AI 댓글 생성을 위한 object 추가 ---
object GeminiTranslator {

    /**
     * 입력된 텍스트를 기반으로 AI 댓글을 생성하는 suspend 함수.
     * @param userInput 사용자가 입력한 원본 텍스트.
     * @return AI가 생성한 댓글 텍스트.
     */
    suspend fun generateComment(userInput: String): String {
        return try {
            val model = Firebase.ai(backend = GenerativeBackend.googleAI())
                .generativeModel("gemini-1.5-flash") // 빠른 응답을 위해 flash 모델 사용 (또는 pro)

            // AI에게 역할을 부여하고 댓글 생성을 요청하는 프롬프트
            val prompt = """
                당신은 익명의 사용자입니다. 다음 피드백 또는 의견에 대해 당신의 생각을 한두 문장의 짧은 댓글로 작성해주세요.
                너무 긍정적이거나 부정적이지 않게, 중립적이거나 살짝 긍정적인 톤으로 작성해주세요.

                의견: "$userInput"

                댓글:
            """.trimIndent()

            // AI 모델 호출 (스트리밍 방식)
            val responseFlow = model.generateContentStream(prompt)
            val fullResponse = StringBuilder()

            // 스트리밍 응답을 수집하여 하나의 텍스트로 합침
            responseFlow.collect { chunk ->
                chunk.text?.let { fullResponse.append(it) }
            }

            // 결과가 비어있으면 기본 응답 반환, 아니면 생성된 텍스트 반환
            fullResponse.toString().ifBlank { "음... 생각해볼 점이네요." }

        } catch (e: Exception) {
            Log.e("GeminiTranslator", "Error generating comment: ${e.message}", e)
            "댓글 생성 중 오류가 발생했습니다." // 오류 발생 시 메시지
        }
    }
}
// --- object 추가 끝 ---

@Preview
@Composable
fun commentPreview() {
    // Flow<GenerateContentResponse>를 직접 print하는 것은 어려우므로 주석 처리 또는 수정 필요
    // val a : Flow<GenerateContentResponse> = Comment("이것은 테스트입니다.")
    // print(a)
}