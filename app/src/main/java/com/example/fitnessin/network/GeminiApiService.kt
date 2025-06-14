package com.example.fitnessin.network

import com.example.fitnessin.util.Constants
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface GeminiApi {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content,
    @SerializedName("finishReason") val finishReason: String
)

@Singleton
class GeminiApiService @Inject constructor(
    private val geminiApi: GeminiApi
) {
    
    suspend fun generateContent(prompt: String): String {
        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            )
        )
        
        val response = geminiApi.generateContent(Constants.GEMINI_API_KEY, request)
        return response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text 
            ?: "Maaf, tidak dapat menghasilkan rekomendasi saat ini."
    }
}