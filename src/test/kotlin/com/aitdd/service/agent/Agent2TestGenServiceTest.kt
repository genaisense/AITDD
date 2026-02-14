package com.aitdd.service.agent

import com.aitdd.service.ClaudeApiService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class Agent2TestGenServiceTest {

    private lateinit var claudeApiService: ClaudeApiService
    private lateinit var agent: Agent2TestGenService

    @BeforeEach
    fun setUp() {
        claudeApiService = mock()
        agent = Agent2TestGenService(claudeApiService)
    }

    @Test
    fun `generates test code from specification and architecture`() {
        val spec = "Calculator with add and subtract"
        val arch = "Calculator class"
        val expectedTests = "import unittest\n\nclass TestCalculator(unittest.TestCase):\n    pass"

        whenever(claudeApiService.sendMessage(any(), any())).thenReturn(expectedTests)

        val result = agent.generateTests(spec, arch)

        assertEquals(expectedTests, result)
        verify(claudeApiService).sendMessage(
            argThat { contains(spec) && contains(arch) },
            any()
        )
    }
}
