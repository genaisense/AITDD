package com.aitdd.service.agent

import com.aitdd.service.ClaudeApiService
import org.springframework.stereotype.Service

@Service
class Agent0SpecificationService(
    private val claudeApiService: ClaudeApiService
) {

    fun generateSpecification(featureDescription: String): String {
        val systemPrompt = """You are a software specification agent. Your role is to take a feature description
            |and produce a detailed, unambiguous specification document. Include:
            |1. Functional requirements
            |2. Input/output descriptions
            |3. Edge cases and error handling
            |4. Constraints and assumptions
            |
            |Output the specification in clear markdown format. The implementation will be in Python.""".trimMargin()

        val prompt = """Generate a detailed specification for the following feature:
            |
            |$featureDescription
            |
            |Provide a comprehensive specification that a developer can use to implement this feature.""".trimMargin()

        return claudeApiService.sendMessage(prompt, systemPrompt)
    }
}
