package it.valeriovaudi.roomservice

import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import it.valeriovaudi.room.model.*
import it.valeriovaudi.room.model.CreateRoomInvitationResponse.*
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
    fun grpcServer(
        serverProperties: ServerProperties,
        roomRepository: RoomRepository
    ): GrpcServerStarter {
        return GrpcServerStarter(serverProperties, roomRepository)
    }

    @Bean
    fun roomRepository(): RoomRepository = InMemoryRoomRepository()
}

class InMemoryRoomRepository : RoomRepository {
    override fun createNewRoom(room: Room): RoomId {
        TODO("Not yet implemented")
    }

    override fun findRoomFor(userName: String, guestUsername: String): RoomId {
        TODO("Not yet implemented")
    }

}

fun main(args: Array<String>) {
    runApplication<RoomServiceApplication>(*args)
}

class GrpcServerStarter(
    private val serverProperties: ServerProperties,
    private val roomRepository: RoomRepository
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(GrpcServerStarter::class.java)

    override fun run(args: ApplicationArguments) {
        logger.info("STARTED...")
        ServerBuilder
            .forPort(serverProperties.port)
            .addService(GrpcRoomService(roomRepository))
            .build().start().awaitTermination()
    }
}

data class Room(val userName: String, val guestUsername: String)

@JvmInline
value class RoomId(val content: String)

@JvmInline
value class Message(val content: String)

interface MessageRepository {
    fun messageFor(room: RoomId): List<Message>

}

interface RoomRepository {
    fun createNewRoom(room: Room): RoomId
    fun findRoomFor(userName: String, guestUsername: String): RoomId

}

class GrpcRoomService(private val roomRepository: RoomRepository) : RoomServiceGrpc.RoomServiceImplBase() {
    override fun createNewRoomInvitation(
        request: CreateRoomInvitationRequest,
        responseObserver: StreamObserver<CreateRoomInvitationResponse>
    ) {
        val roomId = roomRepository.createNewRoom(Room(request.myUsername, request.guestUsername))
        val roomInvitationResponse = newBuilder().setRoomId(roomId.content).build()
        responseObserver.onNext(roomInvitationResponse)
        responseObserver.onCompleted()
    }

    override fun createNewRoomInvitationConfirmation(
        request: CreateRoomInvitationConfirmationRequest,
        responseObserver: StreamObserver<CreateRoomInvitationConfirmationResponse>
    ) {
        super.createNewRoomInvitationConfirmation(request, responseObserver)
    }

}