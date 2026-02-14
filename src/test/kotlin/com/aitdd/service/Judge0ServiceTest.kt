package com.aitdd.service

import com.aitdd.config.Judge0Properties
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

class Judge0ServiceTest {

    private lateinit var restClient: RestClient
    private lateinit var service: Judge0Service
    private val properties = Judge0Properties(
        apiUrl = "https://judge0-ce.p.rapidapi.com",
        apiKey = "test-key",
        apiHost = "judge0-ce.p.rapidapi.com",
        languageId = 71
    )

    @BeforeEach
    fun setUp() {
        restClient = mock()
        service = Judge0Service(restClient, properties)
    }

    @Test
    fun `execute returns passed result when tests succeed`() {
        val responseJson = """
            {
              "stdout": "OK\n",
              "stderr": null,
              "status": {"id": 3, "description": "Accepted"},
              "compile_output": null
            }
        """.trimIndent()

        mockRestClientPost(responseJson)

        val result = service.execute("print('OK')")

        assertTrue(result.passed)
        assertEquals("OK\n", result.stdout)
        assertEquals("Accepted", result.statusDescription)
    }

    @Test
    fun `execute returns failed result when tests fail`() {
        val responseJson = """
            {
              "stdout": null,
              "stderr": "AssertionError: 1 != 2",
              "status": {"id": 6, "description": "Runtime Error (NZEC)"},
              "compile_output": null
            }
        """.trimIndent()

        mockRestClientPost(responseJson)

        val result = service.execute("assert 1 == 2")

        assertFalse(result.passed)
        assertEquals("AssertionError: 1 != 2", result.stderr)
    }

    @Test
    fun `execute returns failed result on compilation error`() {
        val responseJson = """
            {
              "stdout": null,
              "stderr": null,
              "status": {"id": 6, "description": "Compilation Error"},
              "compile_output": "SyntaxError: invalid syntax"
            }
        """.trimIndent()

        mockRestClientPost(responseJson)

        val result = service.execute("def bad(")

        assertFalse(result.passed)
        assertTrue(result.stderr.contains("SyntaxError"))
    }

    private fun mockRestClientPost(responseJson: String) {
        val requestBodySpec = mock<RestClient.RequestBodySpec>()
        val requestBodyUriSpec = mock<RestClient.RequestBodyUriSpec>()
        val responseSpec = mock<RestClient.ResponseSpec>()

        whenever(restClient.post()).thenReturn(requestBodyUriSpec)
        whenever(requestBodyUriSpec.uri("/submissions?base64_encoded=false&wait=true")).thenReturn(requestBodySpec)
        whenever(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec)
        whenever(requestBodySpec.body(any<String>())).thenReturn(requestBodySpec)
        whenever(requestBodySpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.body(String::class.java)).thenReturn(responseJson)
    }
}
