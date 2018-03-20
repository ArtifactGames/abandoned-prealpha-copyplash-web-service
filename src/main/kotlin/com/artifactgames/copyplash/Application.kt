package com.artifactgames.copyplash

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import com.fasterxml.jackson.module.kotlin.*

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