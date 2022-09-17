package com.webClipBoard.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.SecurityFilterChain
import kotlin.jvm.Throws
import org.springframework.security.provisioning.InMemoryUserDetailsManager

import org.springframework.security.core.userdetails.UserDetails

import org.springframework.security.core.userdetails.UserDetailsService

enum class Role(val roleName: String) {
    USER("USER"),
}


@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf().disable()
            .authorizeHttpRequests{ requests ->
                requests
                    .antMatchers("/", "/home").permitAll()
                    .antMatchers("/api/**").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .permitAll()
            }.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService? {
        val user: UserDetails = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles(Role.USER.roleName)
            .build()
        return InMemoryUserDetailsManager(user)
    }
}