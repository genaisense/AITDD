package com.aitdd.controller

import com.aitdd.service.PipelineOrchestratorService
import com.aitdd.service.PipelineStore
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException

@Controller
class PipelineController(
    private val orchestratorService: PipelineOrchestratorService,
    private val pipelineStore: PipelineStore
) {

    @PostMapping("/pipeline/start")
    fun startPipeline(@RequestParam description: String): String {
        val pipelineId = orchestratorService.startPipeline(description)
        return "redirect:/pipeline/$pipelineId"
    }

    @GetMapping("/pipeline/{id}")
    fun pipelineDetail(@PathVariable id: String, model: Model): String {
        val run = pipelineStore.get(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pipeline not found")
        model.addAttribute("run", run)
        return "pipeline-detail"
    }

    @GetMapping("/pipeline/{id}/status")
    fun pipelineStatus(@PathVariable id: String, model: Model): String {
        val run = pipelineStore.get(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pipeline not found")
        model.addAttribute("run", run)
        return "fragments/pipeline-status"
    }
}
