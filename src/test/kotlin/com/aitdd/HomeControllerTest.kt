package com.aitdd

import com.aitdd.service.PipelineStore
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@WebMvcTest(HomeController::class)
class HomeControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var pipelineStore: PipelineStore

    @Test
    fun `home page returns index view`() {
        whenever(pipelineStore.getAll()).thenReturn(emptyList())

        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(view().name("index"))
    }
}
