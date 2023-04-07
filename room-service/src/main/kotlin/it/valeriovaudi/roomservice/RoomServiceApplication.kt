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

fun main(args: Array<String>) {
    runApplication<RoomServiceApplication>(*args)
}

class InMemoryRoomRepository : RoomRepository {
    override fun createNewRoom(room: Room): RoomId {
        TODO("Not yet implemented")
    }

    override fun findRoomFor(userName: String, guestUsername: String): Room {
        TODO("Not yet implemented")
    }


    override fun findRoomFor(userName: String): List<Room> {
        TODO("Not yet implemented")
    }

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

data class Room(val id: RoomId = RoomId.empty(), val userName: String, val guestUsername: String, val accepted: Boolean)

@JvmInline
value class RoomId(val content: String) {
    companion object {
        fun empty() = RoomId("")
    }
}

@JvmInline
value class Message(val content: String)

interface MessageRepository {
    fun messageFor(room: RoomId): List<Message>

}

interface RoomRepository {
    fun createNewRoom(room: Room): RoomId
    fun findRoomFor(userName: String, guestUsername: String): Room
    fun findRoomFor(userName: String): List<Room>

}

class GrpcRoomService(private val roomRepository: RoomRepository) : RoomServiceGrpc.RoomServiceImplBase() {
    override fun createNewRoomInvitation(
        request: CreateRoomInvitationRequest,
        responseObserver: StreamObserver<CreateRoomInvitationResponse>
    ) {
        val roomId =
            roomRepository.createNewRoom(
                Room(
                    userName = request.myUsername,
                    guestUsername = request.guestUsername,
                    accepted = false
                )
            )
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