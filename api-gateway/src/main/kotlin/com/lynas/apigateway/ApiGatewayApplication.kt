package com.lynas.apigateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@EnableDiscoveryClient
@SpringBootApplication
class ApiGatewayApplication {

    @Bean
    fun userKeyResolver(): KeyResolver {
        // Currently rate limit is set for all
        // If you want to add rate limit per user then add spring security and un comment following line
        // return KeyResolver { it.request.queryParams.getFirst("user").toMono() }
        return KeyResolver { Mono.just("1") }
    }

    @Bean
    fun redisRateLimiter() : RedisRateLimiter = RedisRateLimiter(2,2)

    @Bean
    fun gateways(rlb: RouteLocatorBuilder): RouteLocator {
        return rlb.routes()
            .route("book-service") {
                it.path("/books/**")
                    .uri("lb://book-service")
            }
            .route("order-service") {
                it.path("/orders/**").uri("lb://order-service")
            }
            .route("bookList"){
                r->r.path("/bookList")
                .filters {
                    f->f.setPath("/books/")
                    .circuitBreaker {
                        cb->cb.setFallbackUri("forward:/serviceUnavailable")
                    }
                    .requestRateLimiter {
                        rL -> rL.apply {
                            rateLimiter = redisRateLimiter()
                            keyResolver = userKeyResolver()
                    }
                    }
                }
                .uri("lb://book-service")

            }
            .build()

    }
}

fun main(args: Array<String>) {
    runApplication<ApiGatewayApplication>(*args)
}

@RestController
class ServiceNotAvailableController {
	@GetMapping("/serviceUnavailable")
	fun serviceUnavailable() = emptyList<String>()
}