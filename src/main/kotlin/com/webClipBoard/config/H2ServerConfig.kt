package com.webClipBoard.config

import org.h2.tools.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Profile("local")
@Configuration
class H2ServerConfig {
    @Bean
    fun h2TcpServer(): Server {
        return Server.createTcpServer().start()
    }
}