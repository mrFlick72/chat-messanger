package it.valeriovaudi.messanger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MessangerApplication

fun main(args: Array<String>) {
    runApplication<MessangerApplication>(*args)
}
