package it.valeriovaudi.messanger

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import it.valeriovaudi.account.model.AccountsServiceGrpc.newBlockingStub
import it.valeriovaudi.account.model.LoginRequest
import it.valeriovaudi.account.model.LoginResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.SecurityFilterChain
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import java.util.*

@SpringBootApplication
class MessangerApplication {
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

@Controller
class IndexController {

    @ModelAttribute("assetBundle")
    fun indexBundle() = "app_bundle.js"

    @GetMapping("/index")
    fun index() = "index"
}

@Controller
class LoginController {

    @ModelAttribute("assetBundle")
    fun indexBundle() = "login_bundle.js"

    @GetMapping("/login")
    fun login() = "index"

}

@ControllerAdvice
class BaseUiModelInjector(
    @Value("\${assetServer.baseUrl:http://localhost:\${server.port}/asset}") private val assetServerBaseUrl: String
) {

    @ModelAttribute("assetServerBaseUrl")
    fun assetServerBaseUrl() = assetServerBaseUrl

}

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
class SecurityConfig(
    val accountAuthenticationProvider: AuthenticationProvider
) {

    @Bean
    fun defaultSecurityFilterChain(
        http: HttpSecurity,
    ): SecurityFilterChain {
        http.authenticationProvider(accountAuthenticationProvider)
        http.csrf().disable().headers().frameOptions().disable()

        http.formLogin()
            .loginProcessingUrl("/login")
            .usernameParameter("username")
            .passwordParameter("password")
            .loginPage("/login")
            .defaultSuccessUrl("/index")
            .permitAll()

        http.authorizeHttpRequests { authz ->
            authz.requestMatchers("/asset/**").permitAll()
            authz.requestMatchers("/login").permitAll()
            authz.requestMatchers("/index").authenticated()
        }

        return http.build()
    }
}

class AccountServiceAuthenticationProvider(private val accountService: GrpcAccountsService) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        println("$authentication")

        val login = accountService.login(
            LoginRequest.newBuilder()
                .setUsername(authentication.name)
                .setPassword(authentication.credentials as String)
                .build()
        )
        return login
            .map {
                UsernamePasswordAuthenticationToken.authenticated(
                    authentication.name,
                    "",
                    listOf(SimpleGrantedAuthority("ROLE_USER"))
                )
            }
            .orElseThrow {
                BadCredentialsException("error")
            }
    }

    override fun supports(authentication: Class<*>) = true

}

class GrpcAccountsService(val channel: ManagedChannel) {
    fun login(request: LoginRequest): Optional<LoginResponse> {
        val accountsServiceBlockingStub = newBlockingStub(channel)
        return Optional.ofNullable(accountsServiceBlockingStub.login(request))
    }

}