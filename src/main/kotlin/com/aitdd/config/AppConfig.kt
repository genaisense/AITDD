package com.aitdd.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@ConfigurationProperties(prefix = "aitdd.claude")
data class ClaudeProperties(
    val apiKey: String = "",
    val model: String = "claude-sonnet-4-5-20250929",
    val baseUrl: String = "https://api.anthropic.com",
    val maxTokens: Int = 4096
)

@ConfigurationProperties(prefix = "aitdd.judge0")
data class Judge0Properties(
    val apiUrl: String = "https://judge0-ce.p.rapidapi.com",
    val apiKey: String = "",
    val apiHost: String = "judge0-ce.p.rapidapi.com",
    val languageId: Int = 71 // Python 3
)

@ConfigurationProperties(prefix = "aitdd.pipeline")
data class PipelineProperties(
    val maxRetries: Int = 5
)

@Configuration
@EnableConfigurationProperties(ClaudeProperties::class, Judge0Properties::class, PipelineProperties::class)
class AppConfig {

    @Bean("claudeRestClient")
    fun claudeRestClient(props: ClaudeProperties): RestClient =
        RestClient.builder()
            .baseUrl(props.baseUrl)
            .defaultHeader("x-api-key", props.apiKey)
            .defaultHeader("anthropic-version", "2023-06-01")
            .defaultHeader("content-type", "application/json")
            .build()

    @Bean("judge0RestClient")
    fun judge0RestClient(props: Judge0Properties): RestClient =
        RestClient.builder()
            .baseUrl(props.apiUrl)
            .defaultHeader("X-RapidAPI-Key", props.apiKey)
            .defaultHeader("X-RapidAPI-Host", props.apiHost)
            .defaultHeader("content-type", "application/json")
            .build()
}
