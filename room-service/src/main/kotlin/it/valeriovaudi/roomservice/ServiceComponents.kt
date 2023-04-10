package it.valeriovaudi.roomservice

import io.grpc.stub.StreamObserver
import it.valeriovaudi.room.model.CreateRoomInvitationConfirmationRequest
import it.valeriovaudi.room.model.CreateRoomInvitationRequest
import it.valeriovaudi.room.model.CreateRoomResponse
import it.valeriovaudi.room.model.RoomServiceGrpc


class GrpcRoomService(
    private val accountRepository: AccountRepository,
    private val roomRepository: RoomRepository
) : RoomServiceGrpc.RoomServiceImplBase() {
    override fun createNewRoomInvitation(
        request: CreateRoomInvitationRequest,
        responseObserver: StreamObserver<CreateRoomResponse>
    ) {

        if (accountRepository.exist(request.guestUsername)) {
            val roomId =
                roomRepository.createNewRoom(
                    Room(
                        userName = request.myUsername,
                        guestUsername = request.guestUsername,
                        accepted = false
                    )
                )
            val roomInvitationResponse = CreateRoomResponse.newBuilder().setRoomId(roomId.content).build()
            responseObserver.onNext(roomInvitationResponse)
            responseObserver.onCompleted()
        } else {
            responseObserver.onError(RuntimeException("Guest Account was not found"))
        }

    }

    override fun createNewRoomInvitationConfirmation(
        request: CreateRoomInvitationConfirmationRequest,
        responseObserver: StreamObserver<CreateRoomResponse>
    ) {
        super.createNewRoomInvitationConfirmation(request, responseObserver)
    }

}