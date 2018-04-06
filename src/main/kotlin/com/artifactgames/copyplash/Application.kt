package com.artifactgames.copyplash

import com.artifactgames.copyplash.model.GameMode
import com.artifactgames.copyplash.model.fromJsonFile
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.io.ClassPathResource

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}

@Bean
@Primary
fun objectMapper() = ObjectMapper().apply {
    registerModule(KotlinModule())
}


@Configuration
class AppConfig {
    @Bean
    fun messageSource() = ReloadableResourceBundleMessageSource().apply {
        setBasenames("classpath:i18n/game_modes")
        setUseCodeAsDefaultMessage(true)
        setDefaultEncoding("UTF-8")
        setCacheSeconds(0)
        println("messageSource registered")
    }

    @Bean
    fun gameModes()= ClassPathResource("settings/game-modes.json").file.fromJsonFile<Array<GameMode>>().toList()
}