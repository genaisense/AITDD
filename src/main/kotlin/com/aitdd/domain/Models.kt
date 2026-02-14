package com.aitdd.domain

import java.time.Instant
import java.util.UUID

enum class PipelineStatus {
    PENDING, RUNNING, PASSED, FAILED
}

enum class AgentPhase {
    SPECIFICATION, ARCHITECTURE, TEST_GENERATION, IMPLEMENTATION, EXECUTION, RETRY, COMPLETED, FAILED
}

data class FeatureRequest(
    val description: String
)

data class AgentResult(
    val phase: AgentPhase,
    val output: String,
    val timestamp: Instant = Instant.now()
)

data class ExecutionResult(
    val stdout: String,
    val stderr: String,
    val passed: Boolean,
    val statusDescription: String
)

data class RetryAttempt(
    val attemptNumber: Int,
    val implementation: String,
    val executionResult: ExecutionResult,
    val timestamp: Instant = Instant.now()
)

data class PipelineRun(
    val id: String = UUID.randomUUID().toString(),
    val featureDescription: String,
    var status: PipelineStatus = PipelineStatus.PENDING,
    var currentPhase: AgentPhase = AgentPhase.SPECIFICATION,
    var specification: String? = null,
    var architecture: String? = null,
    var testCode: String? = null,
    var implementation: String? = null,
    var executionResult: ExecutionResult? = null,
    val retryAttempts: MutableList<RetryAttempt> = mutableListOf(),
    var errorMessage: String? = null,
    val createdAt: Instant = Instant.now()
)
