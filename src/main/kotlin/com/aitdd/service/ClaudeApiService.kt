package com.aitdd.service

import com.aitdd.config.ClaudeProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class ClaudeApiService(
    @Qualifier("claudeRestClient") private val restClient: RestClient,
    private val properties: ClaudeProperties
) {

    private val objectMapper = ObjectMapper()

    fun sendMessage(prompt: String, systemPrompt: String): String {
        val requestBody = objectMapper.writeValueAsString(
            mapOf(
                "model" to properties.model,
                "max_tokens" to properties.maxTokens,
                "system" to systemPrompt,
                "messages" to listOf(
                    mapOf("role" to "user", "content" to prompt)
                )
            )
        )

        val responseBody = restClient.post()
            .uri("/v1/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .body(String::class.java)
            ?: throw RuntimeException("Empty response from Claude API")

        val json = objectMapper.readTree(responseBody)
        return json["content"][0]["text"].asText()
    }
}
