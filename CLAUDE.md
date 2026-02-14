# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kotlin Spring Boot 3.3 full-stack web application using Thymeleaf for server-side rendering. Built with Gradle (Kotlin DSL) and Java 21.

## Build Commands

- **Build**: `./gradlew build`
- **Run**: `./gradlew bootRun` (serves on http://localhost:8080)
- **Test all**: `./gradlew test`
- **Test single class**: `./gradlew test --tests "com.aitdd.HomeControllerTest"`
- **Test single method**: `./gradlew test --tests "com.aitdd.HomeControllerTest.home page returns index view"`
- **Clean**: `./gradlew clean`

## Architecture

- **Package**: `com.aitdd` under `src/main/kotlin/com/aitdd/`
- **Entry point**: `AitddApplication.kt` — standard Spring Boot application
- **Controllers**: Spring MVC controllers returning Thymeleaf view names
- **Templates**: Thymeleaf HTML templates in `src/main/resources/templates/`
- **Config**: `src/main/resources/application.yml`
- **Tests**: Mirror source structure under `src/test/kotlin/com/aitdd/`
  - Use `@SpringBootTest` for integration tests
  - Use `@WebMvcTest` with `MockMvc` for controller tests
