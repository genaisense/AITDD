package com.aitdd.service

import com.aitdd.domain.PipelineRun
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class PipelineStore {

    private val store = ConcurrentHashMap<String, PipelineRun>()

    fun save(run: PipelineRun) {
        store[run.id] = run
    }

    fun get(id: String): PipelineRun? = store[id]

    fun getAll(): List<PipelineRun> = store.values.toList()
}
