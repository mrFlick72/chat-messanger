package it.valeriovaudi.messanger

import it.valeriovaudi.room.model.CreateRoomInvitationRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.security.Principal


@Controller
class IndexController {

    @ModelAttribute("assetBundle")
    fun indexBundle() = "app_bundle.js"

    @GetMapping("/index")
    fun index() = "index"

}


@RestController
class RoomEndPoint(val roomService: GrpcRoomService) {
    @PostMapping("/room")
    fun createNewRoom(
        principal: Principal,
        @RequestBody guestUserName: String
    ) {
        val request = CreateRoomInvitationRequest
            .newBuilder()
            .setMyUsername(principal.name)
            .setGuestUsername(guestUserName)
            .build()
        roomService.createNewRoomFor(request)
        println("DONE")
        ResponseEntity.ok().build<Unit>()
    }
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