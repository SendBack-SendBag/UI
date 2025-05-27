package com.example.sendbacksendbag.communication

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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
        .generativeModel("gemini-2.5-pro")

    val prompt = "$input"
    val response = model.generateContentStream(prompt)

    return response
}

@Composable
fun Comment(
    input: String
): Flow<GenerateContentResponse> {
    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-pro")

    val prompt = "$input"
    val response = model.generateContentStream(prompt)

    return response
}

@Preview
@Composable
fun commentPreview() {
    val a : Flow<GenerateContentResponse> = Comment("이것은 테스트입니다.")
    print(a)
}


