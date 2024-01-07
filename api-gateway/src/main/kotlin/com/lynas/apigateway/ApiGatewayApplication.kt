package com.lynas.apigateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@EnableDiscoveryClient
@SpringBootApplication
class ApiGatewayApplication{

	@Bean
	fun gateways(rlb: RouteLocatorBuilder) : RouteLocator{
		return rlb.routes()
			.route("book-service"){
				it.path("/books/**").uri("lb://book-service")
			}
			.route("order-service"){
				it.path("/orders/**").uri("lb://order-service")
			}
			.build()

	}
}

fun main(args: Array<String>) {
	runApplication<ApiGatewayApplication>(*args)
}
