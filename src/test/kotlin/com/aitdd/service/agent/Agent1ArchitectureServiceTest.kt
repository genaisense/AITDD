package com.aitdd.service.agent

import com.aitdd.service.ClaudeApiService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class Agent1ArchitectureServiceTest {

    private lateinit var claudeApiService: ClaudeApiService
    private lateinit var agent: Agent1ArchitectureService

    @BeforeEach
    fun setUp() {
        claudeApiService = mock()
        agent = Agent1ArchitectureService(claudeApiService)
    }

    @Test
    fun `generates architecture from specification`() {
        val spec = "## Specification\n- Calculator with add and subtract"
        val expectedArch = "## Architecture\n- Calculator class with add() and subtract() methods"

        whenever(claudeApiService.sendMessage(any(), any())).thenReturn(expectedArch)

        val result = agent.generateArchitecture(spec)

        assertEquals(expectedArch, result)
        verify(claudeApiService).sendMessage(
            argThat { contains(spec) },
            any()
        )
    }
}
