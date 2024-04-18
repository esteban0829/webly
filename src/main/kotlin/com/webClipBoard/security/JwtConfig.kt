package com.webClipBoard.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class JwtConfig {
    @Value("\${jwt.secret}")
    lateinit var jwtSecret: String
}
