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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter

@EnableWebSecurity
class SecurityConfig(
    private val tokenProvider: TokenProvider,
) {

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
        val swaggerPaths = arrayOf("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**")

        http {
            headers {
                addHeaderWriter(XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
            }
            cors {
                disable()
            }
            csrf {
                disable()
            }
            authorizeRequests {

                listOf(
                    "/login", "/signup", "/user", "/hello", "/new-project", "/project", "/project-setting", "/post",
                    "/api/v1/auth/login", "/api/v1/auth/me", "/api/v1/health/hello", "/api/v1/admin/accounts/register",
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
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JwtFilter(tokenProvider))
            addFilterBefore<JwtFilter>(ExceptionHandlerFilter())
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