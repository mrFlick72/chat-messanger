package it.valeriovaudi.accountservice

import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import it.valeriovaudi.account.model.AccountsServiceGrpc
import it.valeriovaudi.account.model.LoginRequest
import it.valeriovaudi.account.model.LoginResponse
import org.slf4j.LoggerFactory

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.concurrent.ConcurrentHashMap

@SpringBootApplication
@EnableConfigurationProperties(ServerProperties::class)
class AccountServiceApplication {

    @Bean
    fun grpcServer(serverProperties: ServerProperties): GrpcServerStarter {
        val storage = ConcurrentHashMap<String, Account>()
        storage["vvaudi"] = Account("vvaudi", "secret")
        return GrpcServerStarter(serverProperties, InMemoryAccountRepository(storage))
    }

}

fun main(args: Array<String>) {
    runApplication<AccountServiceApplication>(*args)
}

data class Account(val username: String, val password: String)

interface AccountRepository {
    fun find(username: String): Account
}


class InMemoryAccountRepository(private val storage: ConcurrentHashMap<String, Account>) : AccountRepository {

    override fun find(username: String): Account = storage[username]!!


}

class GrpcServerStarter(
    private val serverProperties: ServerProperties,
    private val accountRepository: AccountRepository
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(GrpcServerStarter::class.java)

    override fun run(args: ApplicationArguments) {
        logger.info("STARTED...")
        ServerBuilder
            .forPort(serverProperties.port)
            .addService(GrpcAccountsService(accountRepository))
            .build()
            .let { it.start().awaitTermination() }
    }
}

class GrpcAccountsService(private val accountRepository: AccountRepository) :
    AccountsServiceGrpc.AccountsServiceImplBase() {
    override fun login(request: LoginRequest, responseObserver: StreamObserver<LoginResponse>) {
        val account = accountRepository.find(request.username)
        if (account.password == request.password) {
            val reply = LoginResponse.newBuilder().setMessage("SUCCESS").build()
            responseObserver.onNext(reply)
        } else {
            responseObserver.onError(RuntimeException())
        }

        responseObserver.onCompleted()
    }


}