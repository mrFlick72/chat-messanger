package it.valeriovaudi.messanger

import it.valeriovaudi.account.model.LoginRequest
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
                    listOf(SimpleGrantedAuthority("USER"))
                )
            }
            .orElseThrow {
                BadCredentialsException("error")
            }
    }

    override fun supports(authentication: Class<*>) = true

}