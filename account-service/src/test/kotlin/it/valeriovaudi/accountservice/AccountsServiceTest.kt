package it.valeriovaudi.accountservice

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.ServerBuilder
import it.valeriovaudi.account.model.AccountsServiceGrpc.newBlockingStub
import it.valeriovaudi.account.model.LoginRequest
import it.valeriovaudi.account.model.LoginResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap


class AccountsServiceTest {
    private lateinit var server: Server
    private lateinit var channel: ManagedChannel

    @BeforeEach
    fun setUp() {
        val storage = ConcurrentHashMap<String, Account>()
        storage["username"] = Account("username", "secret")
        server = ServerBuilder
            .forPort(8888)
            .addService(GrpcAccountsService(InMemoryAccountRepository(storage)))
            .build()
            .let { it.start() }

        channel = ManagedChannelBuilder
            .forAddress("localhost", 8888)
            .usePlaintext()
            .build()
    }

    @AfterEach
    fun tearDown() {
        channel.shutdownNow()
        server.shutdownNow()
    }


    @Test
    fun `hen a login goes fine`() {
        val client = newBlockingStub(channel)
        val response = client
            .login(
                LoginRequest.newBuilder()
                    .setUsername("username")
                    .setPassword("secret")
                    .build()
            )

        Assertions.assertEquals(response, LoginResponse.newBuilder().setMessage("SUCCESS").build())
    }
}