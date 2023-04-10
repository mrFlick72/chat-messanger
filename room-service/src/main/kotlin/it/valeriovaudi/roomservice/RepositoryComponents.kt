package it.valeriovaudi.roomservice

import io.grpc.ManagedChannel
import it.valeriovaudi.account.model.AccountsServiceGrpc
import it.valeriovaudi.account.model.ExistRequest


interface MessageRepository {
    fun messageFor(room: RoomId): List<Message>

}

interface RoomRepository {
    fun createNewRoom(room: Room): RoomId
    fun findRoomFor(userName: String, guestUsername: String): Room
    fun findRoomFor(userName: String): List<Room>

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

interface AccountRepository {
    fun exist(userName: String): Boolean

}

class GrpcAccountRepository(private val channel: ManagedChannel) : AccountRepository {

    override fun exist(userName: String): Boolean {

        val accountsServiceBlockingStub = AccountsServiceGrpc.newBlockingStub(channel)
        accountsServiceBlockingStub.exist(ExistRequest.newBuilder().setUsername(userName).build())
        return true
    }

}