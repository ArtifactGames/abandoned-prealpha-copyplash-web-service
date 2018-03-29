package com.artifactgames.copyplash

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer



@Component
@Configuration
class ApiConfig {

    @Value("#{'\${allowedOrigins}'.split(',')}")
    private val allowedOrigins: List<String> = listOf()

    @Bean
    fun corsConfigurer() = object : WebMvcConfigurer {
        override fun addCorsMappings(registry: CorsRegistry?) {
            registry!!
                    .addMapping("/**")
                    .allowedOrigins(*allowedOrigins.toTypedArray())
        }
    }

}