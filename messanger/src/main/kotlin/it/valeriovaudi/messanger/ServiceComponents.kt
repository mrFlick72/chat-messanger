package it.valeriovaudi.messanger

import io.grpc.ManagedChannel
import it.valeriovaudi.account.model.AccountsServiceGrpc
import it.valeriovaudi.account.model.LoginRequest
import it.valeriovaudi.account.model.LoginResponse
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

class GrpcAccountsService(val channel: ManagedChannel) {
    fun login(request: LoginRequest): Optional<LoginResponse> {
        val accountsServiceBlockingStub = AccountsServiceGrpc.newBlockingStub(channel)
        return Optional.ofNullable(accountsServiceBlockingStub.login(request))
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