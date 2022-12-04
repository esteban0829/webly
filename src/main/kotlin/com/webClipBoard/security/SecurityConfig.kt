package com.webClipBoard.security

import com.webClipBoard.Role
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter

@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer  {
        return WebSecurityCustomizer {
            it.ignoring().antMatchers("/css/**", "/js/**", "/img/**")
        }
    }

    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        val h2ConsolePaths = "/h2-console/**"
        val csrfIgnorePaths = arrayOf(h2ConsolePaths, "/swagger-ui/**")
        val swaggerPaths = arrayOf("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**")

        http {
            headers {
                addHeaderWriter(XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
            }
            csrf {
                ignoringAntMatchers(*csrfIgnorePaths)
            }
            authorizeRequests {

                listOf(
                    "/login", "/signup", "/user", "/hello", "/new-project", "/project", "/project-setting",
                    *swaggerPaths, h2ConsolePaths
                ).forEach {
                    authorize(it, permitAll)
                }

                listOf("/", "/api/v1/files/**").forEach {
                    authorize(it, hasAuthority(Role.USER.authority))
                }

                authorize("/admin", hasAuthority(Role.ADMIN.authority)) // only ADMIN can access
                authorize(anyRequest, authenticated)
            }
            formLogin {
                loginPage = "/login"
                defaultSuccessUrl("/", alwaysUse = false)
            }
            logout {
                logoutSuccessUrl = "/login"
                invalidateHttpSession = true
            }
        }
        return http.build()
    }
}