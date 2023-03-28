package it.valeriovaudi.messanger

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute

@SpringBootApplication
class MessangerApplication

fun main(args: Array<String>) {
    runApplication<MessangerApplication>(*args)
}

@Controller
class IndexController {

    @ModelAttribute("assetBundle")
    fun indexBundle() = "login_bundle.js"

    @GetMapping("/index")
    fun index() = "index"
}

@ControllerAdvice
class BaseUiModelInjector(
    @Value("\${assetServer.baseUrl:http://localhost:\${server.port}/asset}") private val assetServerBaseUrl: String
) {

    @ModelAttribute("assetServerBaseUrl")
    fun assetServerBaseUrl() = assetServerBaseUrl

}