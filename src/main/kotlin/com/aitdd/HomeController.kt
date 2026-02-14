package com.aitdd

import com.aitdd.service.PipelineStore
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController(
    private val pipelineStore: PipelineStore
) {

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("message", "Welcome to AITDD")
        model.addAttribute("runs", pipelineStore.getAll())
        return "index"
    }
}
