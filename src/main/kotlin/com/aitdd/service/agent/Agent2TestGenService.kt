package com.aitdd.service.agent

import com.aitdd.service.ClaudeApiService
import org.springframework.stereotype.Service

@Service
class Agent2TestGenService(
    private val claudeApiService: ClaudeApiService
) {

    fun generateTests(specification: String, architecture: String): String {
        val systemPrompt = """You are a test generation agent. Your role is to write Python unittest test cases
            |based on a specification and architecture design. Requirements:
            |1. Use Python's unittest framework
            |2. Cover all functional requirements from the specification
            |3. Include edge cases and error handling tests
            |4. Tests should be self-contained and runnable
            |5. Output ONLY the Python test code, no markdown fences or explanation
            |
            |The test file must be executable with: python -m unittest""".trimMargin()

        val prompt = """Generate Python unittest test cases for the following:
            |
            |## Specification
            |$specification
            |
            |## Architecture
            |$architecture
            |
            |Output ONLY valid Python test code using unittest. No markdown.""".trimMargin()

        return claudeApiService.sendMessage(prompt, systemPrompt)
    }
}
