package it.valeriovaudi.accountservice

import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import it.valeriovaudi.account.model.AccountsServiceGrpc
import it.valeriovaudi.account.model.GenericResponse
import it.valeriovaudi.account.model.LoginRequest
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import java.util.concurrent.ConcurrentHashMap

@SpringBootApplication
@EnableConfigurationProperties(ServerProperties::class)
class AccountServiceApplication {

    @Bean
    fun admin(): KafkaAdmin {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        return KafkaAdmin(configs)
    }

    @Bean
    fun topic1(): NewTopic {
        return TopicBuilder.name("account-invitation")
            .partitions(1)
            .replicas(1)
            .build()
    }

    @Bean
    fun listener() = AccountInvitationRequestListener()

    @Bean
    fun grpcServer(serverProperties: ServerProperties): GrpcServerStarter {
        val storage = ConcurrentHashMap<String, Account>()
        storage["admin"] = Account("admin", "secret")
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
            .build().start().awaitTermination()
    }
}

class GrpcAccountsService(private val accountRepository: AccountRepository) :
    AccountsServiceGrpc.AccountsServiceImplBase() {
    override fun login(request: LoginRequest, responseObserver: StreamObserver<GenericResponse>) {
        val account = accountRepository.find(request.username)
        if (account.password == request.password) {
            val reply = GenericResponse.newBuilder().setMessage("SUCCESS").build()
            responseObserver.onNext(reply)
        } else {
            responseObserver.onError(RuntimeException())
        }

        responseObserver.onCompleted()
    }
}

class AccountInvitationRequestListener {

    private val logger = LoggerFactory.getLogger(AccountInvitationRequestListener::class.java)

    @KafkaListener(topics = ["account-invitation"], groupId = "test")
    fun listen(@Header(KafkaHeaders.GROUP_ID) groupId: String, body: ByteArray) {
        logger.info("LoginRequest.parseFrom(body).toString()")
        logger.info(LoginRequest.parseFrom(body).toString())
    }
}