package com.aitdd.service.agent

import com.aitdd.service.ClaudeApiService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class Agent3ImplementationServiceTest {

    private lateinit var claudeApiService: ClaudeApiService
    private lateinit var agent: Agent3ImplementationService

    @BeforeEach
    fun setUp() {
        claudeApiService = mock()
        agent = Agent3ImplementationService(claudeApiService)
    }

    @Test
    fun `generates implementation from spec, architecture, and tests`() {
        val spec = "Calculator spec"
        val arch = "Calculator class"
        val tests = "import unittest\nclass TestCalc(unittest.TestCase): pass"
        val expectedImpl = "class Calculator:\n    def add(self, a, b): return a + b"

        whenever(claudeApiService.sendMessage(any(), any())).thenReturn(expectedImpl)

        val result = agent.generateImplementation(spec, arch, tests)

        assertEquals(expectedImpl, result)
        verify(claudeApiService).sendMessage(
            argThat { contains(spec) && contains(arch) && contains(tests) },
            any()
        )
    }

    @Test
    fun `fixImplementation sends error context for retry`() {
        val tests = "import unittest\nclass TestCalc(unittest.TestCase): pass"
        val failedImpl = "class Calculator:\n    pass"
        val errorOutput = "AssertionError: expected 3 but got None"
        val fixedImpl = "class Calculator:\n    def add(self, a, b): return a + b"

        whenever(claudeApiService.sendMessage(any(), any())).thenReturn(fixedImpl)

        val result = agent.fixImplementation(tests, failedImpl, errorOutput)

        assertEquals(fixedImpl, result)
        verify(claudeApiService).sendMessage(
            argThat { contains(failedImpl) && contains(errorOutput) && contains(tests) },
            any()
        )
    }
}
