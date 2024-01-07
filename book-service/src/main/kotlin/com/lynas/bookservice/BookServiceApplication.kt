package com.lynas.bookservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@EnableDiscoveryClient
@SpringBootApplication
class BookServiceApplication

fun main(args: Array<String>) {
    runApplication<BookServiceApplication>(*args)
}

@RestController
@RequestMapping("/books")
class BookRestController{

    @GetMapping("/")
    fun getAllBooks(): Map<String, Book> {
        return getAllBooksMap()
    }

    @GetMapping("/{id}")
    fun getOneBook(@PathVariable id: String) = getAllBooksMap()[id]
}


class Book(
    val id: String,
    val name: String
)

fun getAllBooksMap(): Map<String, Book> = mapOf(
    "325dac11" to Book("325dac11", "Harry Potter2"),
    "a1447057" to Book("a1447057", "Spring in Action2"),
    "4d48c52d" to Book("4d48c52d", "The great expectation2"),
    "5b3d68d7" to Book("5b3d68d7", "The Wind Rises2")

)