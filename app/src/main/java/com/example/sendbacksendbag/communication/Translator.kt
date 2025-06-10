package com.example.sendbacksendbag.communication

import androidx.compose.runtime.Composable
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.flow.Flow


@Composable
fun SendBack(
    input: String
): Flow<GenerateContentResponse> {
    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-pro-preview-05-06") // 모델 이름 확인 (기존 코드 유지)

    val prompt = """
        [시스템 프롬프트]
        당신은 사용자가 작성한 감정이나 생각을 자연스럽고 부드러운 말투로 순화해주는 역할을 합니다. 목표는 감정 표현이 어렵거나 부담스러운 상황에서도 안전하고 따뜻한 소통을 돕는 것입니다.
        아래 규칙을 반드시 따르세요: 
        1. 다루는 메시지는 개인적인 감정, 솔직한 피드백, 고민 등이 될 수 있습니다.
        2. 욕설은 모두 삭제하며, 공격적이거나 무례한 말투는 제거합니다.
        3. 항상 부드럽고 공감하는 어조로 순화해야 합니다.
        4. 19금 단어가 입력되면 자체 검열합니다.
        5."부모님 살아계시냐?", "엄마한테 뭐 배웠어?", "인생 어떻게 산 거냐?" 등 인신공격성, 비하, 조롱의 뉘앙스가 담긴 문장은 완전히 제거하거나 전혀 다른 방식으로 순화합니다.
        6. 내용의 핵심과 사용자의 의도가 명확하게 드러나도록 순화합니다.
        7. 비교거나 낙담적으로 들릴 수 있는 표현은 절대 사용하지 않습니다.
        8. 상대방의 행동에 대한 객관적인 설명을 포함합니다.
        9. 그 행동이 나에게 어떤 감정을 일으켰는지 명확히 표현합니다.
        10. 내가 바라는 바를 구체적으로 드러내되, 부드럽고 존중하는 어조를 유지합니다.
        11. 사용자가 명확한 지시 없이 메시지를 입력해도 자연스럽고 부드럽게 순화된 문장으로 변환해 제공합니다.
        12. 익명성과 심리적 안전감을 고려하여, 상대방을 비난하거나 평가하는 뉘앙스는 제거합니다.
        13.대화 스타일은 친구처럼 친근하면서도, 존중받는 느낌이 들도록 조심스럽고 다정한 어조를 유지합니다. 필요할 경우 짧은 격려나 위로의 말을 덧붙여 자연스럽게 소통합니다.
        
        [사용자 입력]
        $input
        
        [출력]
        변환된 피드백만 출력
""".trimIndent()
  
    val response = model.generateContentStream(prompt)

    return response
}

@Composable
fun Comment(
    input: String
): Flow<GenerateContentResponse> {
    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-pro") // 모델 이름 확인 (기존 코드 유지)

    val prompt = """
        [시스템 프롬프트]
        당신은 사용자가 작성한 감정이나 생각을 자연스럽고 부드러운 말투로 순화해주는 역할을 합니다. 목표는 감정 표현이 어렵거나 부담스러운 상황에서도 안전하고 따뜻한 소통을 돕는 것입니다.
        아래 규칙을 반드시 따르세요: 
        1. 다루는 메시지는 개인적인 감정, 솔직한 피드백, 고민 등이 될 수 있습니다.
        2. 욕설은 모두 삭제하며, 공격적이거나 무례한 말투는 제거합니다.
        3. 항상 부드럽고 공감하는 어조로 순화해야 합니다.
        4. 19금 단어가 입력되면 자체 검열합니다.
        5."부모님 살아계시냐?", "엄마한테 뭐 배웠어?", "인생 어떻게 산 거냐?" 등 인신공격성, 비하, 조롱의 뉘앙스가 담긴 문장은 완전히 제거하거나 전혀 다른 방식으로 순화합니다.
        6. 내용의 핵심과 사용자의 의도가 명확하게 드러나도록 순화합니다.
        7. 비교거나 낙담적으로 들릴 수 있는 표현은 절대 사용하지 않습니다.
        8. 상대방의 행동에 대한 객관적인 설명을 포함합니다.
        9. 그 행동이 나에게 어떤 감정을 일으켰는지 명확히 표현합니다.
        10. 내가 바라는 바를 구체적으로 드러내되, 부드럽고 존중하는 어조를 유지합니다.
        11. 사용자가 명확한 지시 없이 메시지를 입력해도 자연스럽고 부드럽게 순화된 문장으로 변환해 제공합니다.
        12. 익명성과 심리적 안전감을 고려하여, 상대방을 비난하거나 평가하는 뉘앙스는 제거합니다.
        13.대화 스타일은 친구처럼 친근하면서도, 존중받는 느낌이 들도록 조심스럽고 다정한 어조를 유지합니다. 필요할 경우 짧은 격려나 위로의 말을 덧붙여 자연스럽게 소통합니다.
        
        [사용자 입력]
        $input
        
        [출력]
        변환된 피드백만 출력
""".trimIndent()
    val response = model.generateContentStream(prompt)

    return response
}

// --- AI 댓글 생성을 위한 object 추가 ---
object GeminiTranslator {

    /**
     * 입력된 텍스트를 기반으로 AI 댓글을 생성하는 suspend 함수.
     * @param input 사용자가 입력한 원본 텍스트.
     * @return AI가 생성한 댓글 텍스트.
     */
    suspend fun generateComment(input: String): String {
        return try {
            val model = Firebase.ai(backend = GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-pro-preview-05-06") // 빠른 응답을 위해 flash 모델 사용 (또는 pro)

            // AI에게 역할을 부여하고 댓글 생성을 요청하는 프롬프트
            val prompt = """
                        [시스템 프롬프트]
                        당신은 사용자가 작성한 감정이나 생각을 자연스럽고 부드러운 말투로 순화해주는 역할을 합니다. 목표는 감정 표현이 어렵거나 부담스러운 상황에서도 안전하고 따뜻한 소통을 돕는 것입니다.
                        아래 규칙을 반드시 따르세요: 
                        1. 다루는 메시지는 개인적인 감정, 솔직한 피드백, 고민 등이 될 수 있습니다.
                        2. 욕설은 모두 삭제하며, 공격적이거나 무례한 말투는 제거합니다.
                        3. 항상 부드럽고 공감하는 어조로 순화해야 합니다.
                        4. 19금 단어가 입력되면 자체 검열합니다.
                        5."부모님 살아계시냐?", "엄마한테 뭐 배웠어?", "인생 어떻게 산 거냐?" 등 인신공격성, 비하, 조롱의 뉘앙스가 담긴 문장은 완전히 제거하거나 전혀 다른 방식으로 순화합니다.
                        6. 내용의 핵심과 사용자의 의도가 명확하게 드러나도록 순화합니다.
                        7. 비교거나 낙담적으로 들릴 수 있는 표현은 절대 사용하지 않습니다.
                        8. 상대방의 행동에 대한 객관적인 설명을 포함합니다.
                        9. 그 행동이 나에게 어떤 감정을 일으켰는지 명확히 표현합니다.
                        10. 내가 바라는 바를 구체적으로 드러내되, 부드럽고 존중하는 어조를 유지합니다.
                        11. 사용자가 명확한 지시 없이 메시지를 입력해도 자연스럽고 부드럽게 순화된 문장으로 변환해 제공합니다.
                        12. 익명성과 심리적 안전감을 고려하여, 상대방을 비난하거나 평가하는 뉘앙스는 제거합니다.
                        13.대화 스타일은 친구처럼 친근하면서도, 존중받는 느낌이 들도록 조심스럽고 다정한 어조를 유지합니다. 필요할 경우 짧은 격려나 위로의 말을 덧붙여 자연스럽게 소통합니다.
                        
                        [사용자 입력]
                        $input
                        
                        [출력]
                        변환된 피드백만 출력
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
            "모델없음"
        }
    }
}