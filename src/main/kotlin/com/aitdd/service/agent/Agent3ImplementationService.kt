package com.aitdd.service.agent

import com.aitdd.service.ClaudeApiService
import org.springframework.stereotype.Service

@Service
class Agent3ImplementationService(
    private val claudeApiService: ClaudeApiService
) {

    fun generateImplementation(specification: String, architecture: String, testCode: String): String {
        val systemPrompt = """You are an implementation agent. Your role is to write Python code that passes
            |the given test cases. Requirements:
            |1. Implement all classes and functions referenced in the tests
            |2. Follow the architecture design
            |3. Output ONLY the Python implementation code, no markdown fences or explanation
            |4. Do NOT include the test code in your output
            |5. The code must be importable by the test file""".trimMargin()

        val prompt = """Write Python implementation code that passes all the following tests:
            |
            |## Specification
            |$specification
            |
            |## Architecture
            |$architecture
            |
            |## Test Code
            |$testCode
            |
            |Output ONLY valid Python implementation code. No markdown.""".trimMargin()

        return claudeApiService.sendMessage(prompt, systemPrompt)
    }

    fun fixImplementation(testCode: String, failedImplementation: String, errorOutput: String): String {
        val systemPrompt = """You are a code fixing agent. You are given a Python implementation that failed
            |its tests. Analyze the error output and fix the implementation. Requirements:
            |1. Fix ONLY the implementation code, not the tests
            |2. Output ONLY the corrected Python implementation code, no markdown fences or explanation
            |3. Address all failing test cases
            |4. Do NOT include the test code in your output""".trimMargin()

        val prompt = """The following implementation failed its tests. Fix it.
            |
            |## Test Code
            |$testCode
            |
            |## Failed Implementation
            |$failedImplementation
            |
            |## Error Output
            |$errorOutput
            |
            |Output ONLY the corrected Python implementation code. No markdown.""".trimMargin()

        return claudeApiService.sendMessage(prompt, systemPrompt)
    }
}
