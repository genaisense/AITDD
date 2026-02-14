package com.aitdd.controller

import com.aitdd.domain.AgentPhase
import com.aitdd.domain.ExecutionResult
import com.aitdd.domain.PipelineRun
import com.aitdd.domain.PipelineStatus
import com.aitdd.service.PipelineOrchestratorService
import com.aitdd.service.PipelineStore
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PipelineController::class)
class PipelineControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var orchestratorService: PipelineOrchestratorService

    @MockBean
    private lateinit var pipelineStore: PipelineStore

    @Test
    fun `POST pipeline start creates pipeline and redirects`() {
        whenever(orchestratorService.startPipeline(any())).thenReturn("test-id")

        mockMvc.perform(
            post("/pipeline/start")
                .param("description", "Build a calculator")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/pipeline/test-id"))
    }

    @Test
    fun `GET pipeline detail returns detail view`() {
        val run = PipelineRun(
            id = "test-id",
            featureDescription = "Build a calculator",
            status = PipelineStatus.RUNNING,
            currentPhase = AgentPhase.SPECIFICATION
        )
        whenever(pipelineStore.get("test-id")).thenReturn(run)

        mockMvc.perform(get("/pipeline/test-id"))
            .andExpect(status().isOk)
            .andExpect(view().name("pipeline-detail"))
            .andExpect(model().attributeExists("run"))
    }

    @Test
    fun `GET pipeline detail returns 404 for unknown id`() {
        whenever(pipelineStore.get("unknown")).thenReturn(null)

        mockMvc.perform(get("/pipeline/unknown"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `GET pipeline status returns status fragment`() {
        val run = PipelineRun(
            id = "test-id",
            featureDescription = "Build a calculator",
            status = PipelineStatus.PASSED,
            currentPhase = AgentPhase.COMPLETED,
            specification = "spec",
            architecture = "arch",
            testCode = "tests",
            implementation = "impl",
            executionResult = ExecutionResult("OK", "", true, "Accepted")
        )
        whenever(pipelineStore.get("test-id")).thenReturn(run)

        mockMvc.perform(get("/pipeline/test-id/status"))
            .andExpect(status().isOk)
            .andExpect(view().name("fragments/pipeline-status"))
            .andExpect(model().attributeExists("run"))
    }
}
