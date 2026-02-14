package com.aitdd.service.agent

import com.aitdd.service.ClaudeApiService
import org.springframework.stereotype.Service

@Service
class Agent1ArchitectureService(
    private val claudeApiService: ClaudeApiService
) {

    fun generateArchitecture(specification: String): String {
        val systemPrompt = """You are a software architecture agent. Your role is to design the Python module
            |and class structure for a given specification. Include:
            |1. Module and class names
            |2. Method signatures with parameters and return types
            |3. Data structures needed
            |4. Dependencies between components
            |
            |Output the architecture design in clear markdown format. Keep it focused on Python.""".trimMargin()

        val prompt = """Design the software architecture for the following specification:
            |
            |$specification
            |
            |Provide a clear Python class/module design.""".trimMargin()

        return claudeApiService.sendMessage(prompt, systemPrompt)
    }
}
