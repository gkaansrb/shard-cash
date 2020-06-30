package com.kakaopay.demo.api.share

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CashShareConfiguration {
    @Bean
    fun cashShareRouter(handler: CashShareHandler) = cashShareCoRouter {
        POST("/", handler::create)
        PUT("/{token}", handler::receipt)
        GET("/cash/share/{token}", handler::find)
    }

    @Bean
    fun helloRouter(handler: CashShareHandler) = coRouter {
        GET("/hello", handler::hello)
    }

    fun cashShareCoRouter(block: (CoRouterFunctionDsl.() -> Unit)) = coRouter {
        ("/cash/share" and accept(MediaType.APPLICATION_JSON)).nest(block)
    }
}