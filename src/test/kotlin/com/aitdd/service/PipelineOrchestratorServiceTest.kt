package com.aitdd.service

import com.aitdd.config.PipelineProperties
import com.aitdd.domain.AgentPhase
import com.aitdd.domain.ExecutionResult
import com.aitdd.domain.PipelineRun
import com.aitdd.domain.PipelineStatus
import com.aitdd.service.agent.Agent0SpecificationService
import com.aitdd.service.agent.Agent1ArchitectureService
import com.aitdd.service.agent.Agent2TestGenService
import com.aitdd.service.agent.Agent3ImplementationService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class PipelineOrchestratorServiceTest {

    private lateinit var agent0: Agent0SpecificationService
    private lateinit var agent1: Agent1ArchitectureService
    private lateinit var agent2: Agent2TestGenService
    private lateinit var agent3: Agent3ImplementationService
    private lateinit var judge0Service: Judge0Service
    private lateinit var pipelineStore: PipelineStore
    private lateinit var orchestrator: PipelineOrchestratorService
    private val properties = PipelineProperties(maxRetries = 5)

    @BeforeEach
    fun setUp() {
        agent0 = mock()
        agent1 = mock()
        agent2 = mock()
        agent3 = mock()
        judge0Service = mock()
        pipelineStore = PipelineStore()
        orchestrator = PipelineOrchestratorService(
            agent0, agent1, agent2, agent3, judge0Service, pipelineStore, properties
        )
    }

    @Test
    fun `successful pipeline run completes all phases`() {
        whenever(agent0.generateSpecification(any())).thenReturn("spec")
        whenever(agent1.generateArchitecture(any())).thenReturn("arch")
        whenever(agent2.generateTests(any(), any())).thenReturn("tests")
        whenever(agent3.generateImplementation(any(), any(), any())).thenReturn("impl")
        whenever(judge0Service.execute(any())).thenReturn(
            ExecutionResult("OK", "", true, "Accepted")
        )

        val run = PipelineRun(featureDescription = "calculator")
        pipelineStore.save(run)

        orchestrator.executePipeline(run.id)

        val result = pipelineStore.get(run.id)!!
        assertEquals(PipelineStatus.PASSED, result.status)
        assertEquals(AgentPhase.COMPLETED, result.currentPhase)
        assertEquals("spec", result.specification)
        assertEquals("arch", result.architecture)
        assertEquals("tests", result.testCode)
        assertEquals("impl", result.implementation)
        assertTrue(result.executionResult!!.passed)
    }

    @Test
    fun `pipeline retries on test failure and succeeds`() {
        whenever(agent0.generateSpecification(any())).thenReturn("spec")
        whenever(agent1.generateArchitecture(any())).thenReturn("arch")
        whenever(agent2.generateTests(any(), any())).thenReturn("tests")
        whenever(agent3.generateImplementation(any(), any(), any())).thenReturn("bad impl")
        whenever(judge0Service.execute(any()))
            .thenReturn(ExecutionResult("", "AssertionError", false, "NZEC"))
            .thenReturn(ExecutionResult("OK", "", true, "Accepted"))
        whenever(agent3.fixImplementation(any(), any(), any())).thenReturn("fixed impl")

        val run = PipelineRun(featureDescription = "calculator")
        pipelineStore.save(run)

        orchestrator.executePipeline(run.id)

        val result = pipelineStore.get(run.id)!!
        assertEquals(PipelineStatus.PASSED, result.status)
        assertEquals(1, result.retryAttempts.size)
        assertEquals("fixed impl", result.implementation)
    }

    @Test
    fun `pipeline fails after max retries exceeded`() {
        whenever(agent0.generateSpecification(any())).thenReturn("spec")
        whenever(agent1.generateArchitecture(any())).thenReturn("arch")
        whenever(agent2.generateTests(any(), any())).thenReturn("tests")
        whenever(agent3.generateImplementation(any(), any(), any())).thenReturn("bad impl")
        whenever(judge0Service.execute(any())).thenReturn(
            ExecutionResult("", "error", false, "NZEC")
        )
        whenever(agent3.fixImplementation(any(), any(), any())).thenReturn("still bad")

        val run = PipelineRun(featureDescription = "calculator")
        pipelineStore.save(run)

        orchestrator.executePipeline(run.id)

        val result = pipelineStore.get(run.id)!!
        assertEquals(PipelineStatus.FAILED, result.status)
        assertEquals(AgentPhase.FAILED, result.currentPhase)
        assertEquals(5, result.retryAttempts.size)
    }

    @Test
    fun `pipeline handles agent exception gracefully`() {
        whenever(agent0.generateSpecification(any())).thenThrow(RuntimeException("API error"))

        val run = PipelineRun(featureDescription = "calculator")
        pipelineStore.save(run)

        orchestrator.executePipeline(run.id)

        val result = pipelineStore.get(run.id)!!
        assertEquals(PipelineStatus.FAILED, result.status)
        assertEquals(AgentPhase.FAILED, result.currentPhase)
        assertTrue(result.errorMessage!!.contains("API error"))
    }

    @Test
    fun `startPipeline creates run and returns id`() {
        whenever(agent0.generateSpecification(any())).thenReturn("spec")
        whenever(agent1.generateArchitecture(any())).thenReturn("arch")
        whenever(agent2.generateTests(any(), any())).thenReturn("tests")
        whenever(agent3.generateImplementation(any(), any(), any())).thenReturn("impl")
        whenever(judge0Service.execute(any())).thenReturn(
            ExecutionResult("OK", "", true, "Accepted")
        )

        val id = orchestrator.startPipeline("some feature")

        val run = pipelineStore.get(id)
        assertNotNull(run)
        assertEquals("some feature", run!!.featureDescription)
    }
}
