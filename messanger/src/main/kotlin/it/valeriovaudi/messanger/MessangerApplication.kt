package it.valeriovaudi.messanger

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin


@SpringBootApplication
class MessangerApplication {
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
            .compact()
            .build()
    }
    @Bean
    fun client(): GrpcAccountsService {
        val channel: ManagedChannel = ManagedChannelBuilder
            .forAddress("localhost", 6060)
            .usePlaintext()
            .build()
        return GrpcAccountsService(channel)
    }

    @Bean
    fun accountAuthenticationProvider(client: GrpcAccountsService) = AccountServiceAuthenticationProvider(client)
}

fun main(args: Array<String>) {
    runApplication<MessangerApplication>(*args)
}