package com.aitdd.service

import com.aitdd.domain.PipelineRun
import com.aitdd.domain.PipelineStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PipelineStoreTest {

    private lateinit var store: PipelineStore

    @BeforeEach
    fun setUp() {
        store = PipelineStore()
    }

    @Test
    fun `save and get pipeline run`() {
        val run = PipelineRun(featureDescription = "calculator feature")
        store.save(run)

        val retrieved = store.get(run.id)
        assertNotNull(retrieved)
        assertEquals("calculator feature", retrieved!!.featureDescription)
    }

    @Test
    fun `get returns null for unknown id`() {
        assertNull(store.get("nonexistent"))
    }

    @Test
    fun `getAll returns all saved runs`() {
        val run1 = PipelineRun(featureDescription = "feature 1")
        val run2 = PipelineRun(featureDescription = "feature 2")
        store.save(run1)
        store.save(run2)

        val all = store.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `save overwrites existing run with same id`() {
        val run = PipelineRun(featureDescription = "original")
        store.save(run)

        val updated = run.copy(status = PipelineStatus.RUNNING)
        store.save(updated)

        val retrieved = store.get(run.id)
        assertEquals(PipelineStatus.RUNNING, retrieved!!.status)
    }
}
