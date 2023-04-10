package it.valeriovaudi.roomservice

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import it.valeriovaudi.room.model.*
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(ServerProperties::class)
class RoomServiceApplication {
    @Bean
    fun accountRepository(): AccountRepository {
        val channel: ManagedChannel = ManagedChannelBuilder
            .forAddress("localhost", 6060)
            .usePlaintext()
            .build()
        return GrpcAccountRepository(channel)
    }

    @Bean
    fun roomRepository(): RoomRepository = InMemoryRoomRepository()

    @Bean
    fun roomService(accountRepository: AccountRepository, roomRepository: RoomRepository) =
        GrpcRoomService(accountRepository, roomRepository)

    @Bean
    fun grpcServer(
        serverProperties: ServerProperties,
        accountRepository: AccountRepository,
        roomRepository: RoomRepository
    ): GrpcServerStarter = GrpcServerStarter(serverProperties, accountRepository, roomRepository)

}

fun main(args: Array<String>) {
    runApplication<RoomServiceApplication>(*args)
}


class GrpcServerStarter(
    private val serverProperties: ServerProperties,
    private val accountRepository: AccountRepository,
    private val roomRepository: RoomRepository
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(GrpcServerStarter::class.java)

    override fun run(args: ApplicationArguments) {
        logger.info("STARTED...")
        ServerBuilder
            .forPort(serverProperties.port)
            .addService(GrpcRoomService(accountRepository, roomRepository))
            .build().start().awaitTermination()
    }
}

