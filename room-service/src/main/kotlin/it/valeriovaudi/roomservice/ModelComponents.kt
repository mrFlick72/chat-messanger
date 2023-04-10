package it.valeriovaudi.roomservice

data class Room(val id: RoomId = RoomId.empty(), val userName: String, val guestUsername: String, val accepted: Boolean)

@JvmInline
value class RoomId(val content: String) {
    companion object {
        fun empty() = RoomId("")
    }
}

@JvmInline
value class Message(val content: String)
