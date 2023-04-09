package it.valeriovaudi.messanger

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping


@Controller
class IndexController {

    @ModelAttribute("assetBundle")
    fun indexBundle() = "app_bundle.js"

    @GetMapping("/index")
    fun index() = "index"

    @PostMapping("/create-new-room")
    fun createNewRoom() = ResponseEntity.ok().build<Unit>()
}

@Controller
class LoginController {

    @ModelAttribute("assetBundle")
    fun indexBundle() = "login_bundle.js"

    @GetMapping("/login")
    fun login() = "index"

}

@ControllerAdvice
class BaseUiModelInjector(
    @Value("\${assetServer.baseUrl:http://localhost:\${server.port}/asset}") private val assetServerBaseUrl: String
) {

    @ModelAttribute("assetServerBaseUrl")
    fun assetServerBaseUrl() = assetServerBaseUrl

}