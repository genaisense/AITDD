package com.aitdd.service

import com.aitdd.config.ClaudeProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

class ClaudeApiServiceTest {

    private lateinit var restClient: RestClient
    private lateinit var service: ClaudeApiService
    private val properties = ClaudeProperties(
        apiKey = "test-key",
        model = "claude-sonnet-4-5-20250929",
        baseUrl = "https://api.anthropic.com",
        maxTokens = 4096
    )

    @BeforeEach
    fun setUp() {
        restClient = mock()
        service = ClaudeApiService(restClient, properties)
    }

    @Test
    fun `sendMessage returns extracted text from response`() {
        val responseJson = """
            {
              "content": [{"type": "text", "text": "Generated specification"}],
              "role": "assistant"
            }
        """.trimIndent()

        val requestBodySpec = mock<RestClient.RequestBodySpec>()
        val requestBodyUriSpec = mock<RestClient.RequestBodyUriSpec>()
        val responseSpec = mock<RestClient.ResponseSpec>()

        whenever(restClient.post()).thenReturn(requestBodyUriSpec)
        whenever(requestBodyUriSpec.uri("/v1/messages")).thenReturn(requestBodySpec)
        whenever(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec)
        whenever(requestBodySpec.body(any<String>())).thenReturn(requestBodySpec)
        whenever(requestBodySpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.body(String::class.java)).thenReturn(responseJson)

        val result = service.sendMessage("Generate a specification", "You are an agent")

        assertEquals("Generated specification", result)
    }

    @Test
    fun `sendMessage throws when response body is null`() {
        val requestBodySpec = mock<RestClient.RequestBodySpec>()
        val requestBodyUriSpec = mock<RestClient.RequestBodyUriSpec>()
        val responseSpec = mock<RestClient.ResponseSpec>()

        whenever(restClient.post()).thenReturn(requestBodyUriSpec)
        whenever(requestBodyUriSpec.uri("/v1/messages")).thenReturn(requestBodySpec)
        whenever(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec)
        whenever(requestBodySpec.body(any<String>())).thenReturn(requestBodySpec)
        whenever(requestBodySpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.body(String::class.java)).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            service.sendMessage("prompt", "system")
        }
    }
}
