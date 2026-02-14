package com.aitdd.service

import com.aitdd.config.PipelineProperties
import com.aitdd.domain.*
import com.aitdd.service.agent.Agent0SpecificationService
import com.aitdd.service.agent.Agent1ArchitectureService
import com.aitdd.service.agent.Agent2TestGenService
import com.aitdd.service.agent.Agent3ImplementationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class PipelineOrchestratorService(
    private val agent0: Agent0SpecificationService,
    private val agent1: Agent1ArchitectureService,
    private val agent2: Agent2TestGenService,
    private val agent3: Agent3ImplementationService,
    private val judge0Service: Judge0Service,
    private val pipelineStore: PipelineStore,
    private val pipelineProperties: PipelineProperties
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val executor: ExecutorService = Executors.newCachedThreadPool()

    fun startPipeline(featureDescription: String): String {
        val run = PipelineRun(featureDescription = featureDescription)
        pipelineStore.save(run)
        executor.submit { executePipeline(run.id) }
        return run.id
    }

    fun executePipeline(pipelineId: String) {
        val run = pipelineStore.get(pipelineId) ?: return

        try {
            run.status = PipelineStatus.RUNNING

            // Phase 1: Specification
            run.currentPhase = AgentPhase.SPECIFICATION
            pipelineStore.save(run)
            run.specification = agent0.generateSpecification(run.featureDescription)
            pipelineStore.save(run)

            // Phase 2: Architecture
            run.currentPhase = AgentPhase.ARCHITECTURE
            pipelineStore.save(run)
            run.architecture = agent1.generateArchitecture(run.specification!!)
            pipelineStore.save(run)

            // Phase 3: Test Generation
            run.currentPhase = AgentPhase.TEST_GENERATION
            pipelineStore.save(run)
            run.testCode = agent2.generateTests(run.specification!!, run.architecture!!)
            pipelineStore.save(run)

            // Phase 4: Implementation
            run.currentPhase = AgentPhase.IMPLEMENTATION
            pipelineStore.save(run)
            run.implementation = agent3.generateImplementation(
                run.specification!!, run.architecture!!, run.testCode!!
            )
            pipelineStore.save(run)

            // Phase 5: Execution
            run.currentPhase = AgentPhase.EXECUTION
            pipelineStore.save(run)
            val combinedCode = "${run.implementation}\n\n${run.testCode}\n\nimport unittest\nunittest.main()"
            var executionResult = judge0Service.execute(combinedCode)
            run.executionResult = executionResult
            pipelineStore.save(run)

            // Retry loop
            var retryCount = 0
            while (!executionResult.passed && retryCount < pipelineProperties.maxRetries) {
                retryCount++
                run.currentPhase = AgentPhase.RETRY
                pipelineStore.save(run)

                val errorOutput = listOf(executionResult.stdout, executionResult.stderr)
                    .filter { it.isNotBlank() }
                    .joinToString("\n")

                val fixedImpl = agent3.fixImplementation(
                    run.testCode!!, run.implementation!!, errorOutput
                )

                run.retryAttempts.add(
                    RetryAttempt(
                        attemptNumber = retryCount,
                        implementation = run.implementation!!,
                        executionResult = executionResult
                    )
                )

                run.implementation = fixedImpl
                val fixedCode = "${fixedImpl}\n\n${run.testCode}\n\nimport unittest\nunittest.main()"
                executionResult = judge0Service.execute(fixedCode)
                run.executionResult = executionResult
                pipelineStore.save(run)
            }

            if (executionResult.passed) {
                run.status = PipelineStatus.PASSED
                run.currentPhase = AgentPhase.COMPLETED
            } else {
                run.status = PipelineStatus.FAILED
                run.currentPhase = AgentPhase.FAILED
                run.errorMessage = "All ${pipelineProperties.maxRetries} retry attempts failed"
            }
            pipelineStore.save(run)

        } catch (e: Exception) {
            logger.error("Pipeline $pipelineId failed", e)
            run.status = PipelineStatus.FAILED
            run.currentPhase = AgentPhase.FAILED
            run.errorMessage = e.message ?: "Unknown error"
            pipelineStore.save(run)
        }
    }
}
