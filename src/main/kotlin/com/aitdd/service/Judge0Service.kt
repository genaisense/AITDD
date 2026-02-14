package com.aitdd.service

import com.aitdd.config.Judge0Properties
import com.aitdd.domain.ExecutionResult
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class Judge0Service(
    @Qualifier("judge0RestClient") private val restClient: RestClient,
    private val properties: Judge0Properties
) {

    private val objectMapper = ObjectMapper()

    fun execute(sourceCode: String): ExecutionResult {
        val requestBody = objectMapper.writeValueAsString(
            mapOf(
                "language_id" to properties.languageId,
                "source_code" to sourceCode
            )
        )

        val responseBody = restClient.post()
            .uri("/submissions?base64_encoded=false&wait=true")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .body(String::class.java)
            ?: throw RuntimeException("Empty response from Judge0")

        val json = objectMapper.readTree(responseBody)
        val statusId = json["status"]["id"].asInt()
        val statusDescription = json["status"]["description"].asText()
        val stdout = json["stdout"]?.takeUnless { it.isNull }?.asText() ?: ""
        val stderr = json["stderr"]?.takeUnless { it.isNull }?.asText() ?: ""
        val compileOutput = json["compile_output"]?.takeUnless { it.isNull }?.asText() ?: ""

        val combinedStderr = listOf(stderr, compileOutput)
            .filter { it.isNotBlank() }
            .joinToString("\n")

        return ExecutionResult(
            stdout = stdout,
            stderr = combinedStderr,
            passed = statusId == 3, // 3 = Accepted
            statusDescription = statusDescription
        )
    }
}
