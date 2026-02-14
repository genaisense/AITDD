package com.aitdd.service.agent

import com.aitdd.service.ClaudeApiService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class Agent0SpecificationServiceTest {

    private lateinit var claudeApiService: ClaudeApiService
    private lateinit var agent: Agent0SpecificationService

    @BeforeEach
    fun setUp() {
        claudeApiService = mock()
        agent = Agent0SpecificationService(claudeApiService)
    }

    @Test
    fun `generates specification from feature description`() {
        val featureDescription = "Build a calculator that can add and subtract"
        val expectedSpec = "## Specification\n- Addition function\n- Subtraction function"

        whenever(claudeApiService.sendMessage(any(), any())).thenReturn(expectedSpec)

        val result = agent.generateSpecification(featureDescription)

        assertEquals(expectedSpec, result)
        verify(claudeApiService).sendMessage(
            argThat { contains(featureDescription) },
            argThat { contains("specification") || contains("Specification") }
        )
    }

    @Test
    fun `includes feature description in prompt`() {
        whenever(claudeApiService.sendMessage(any(), any())).thenReturn("spec")

        agent.generateSpecification("user authentication with JWT")

        verify(claudeApiService).sendMessage(
            argThat { contains("user authentication with JWT") },
            any()
        )
    }
}
