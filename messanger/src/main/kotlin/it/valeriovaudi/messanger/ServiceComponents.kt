package it.valeriovaudi.messanger

import io.grpc.ManagedChannel
import it.valeriovaudi.account.model.AccountsServiceGrpc
import it.valeriovaudi.account.model.GenericResponse
import it.valeriovaudi.account.model.LoginRequest
import it.valeriovaudi.room.model.CreateRoomInvitationRequest
import it.valeriovaudi.room.model.CreateRoomResponse
import it.valeriovaudi.room.model.RoomServiceGrpc
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

class GrpcAccountsService(val channel: ManagedChannel) {
    fun login(request: LoginRequest): Optional<GenericResponse> {
        val accountsServiceBlockingStub = AccountsServiceGrpc.newBlockingStub(channel)
        return Optional.ofNullable(accountsServiceBlockingStub.login(request))
    }

}
class GrpcRoomService(val channel: ManagedChannel) {
    fun createNewRoomFor(request: CreateRoomInvitationRequest): Optional<CreateRoomResponse> {
        val accountsServiceBlockingStub = RoomServiceGrpc.newBlockingStub(channel)
        return Optional.ofNullable(accountsServiceBlockingStub.createNewRoomInvitation(request))
    }

}

@Service
class GeneratorTest(val kafkaTemplate: KafkaTemplate<String, ByteArray>) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        val loginRequest = LoginRequest.newBuilder()
            .setUsername("vvaudi01")
            .setPassword("secret01")
            .build()
        val messagePayload = loginRequest.toByteArray()
        println("send message")
        println(messagePayload)
        kafkaTemplate.send("account-invitation", "${loginRequest.username}-${UUID.randomUUID().toString()}", messagePayload)
    }

}