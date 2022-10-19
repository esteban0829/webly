package com.webClipBoard

import io.awspring.cloud.autoconfigure.context.ContextResourceLoaderAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

//@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ContextResourceLoaderAutoConfiguration::class])
@SpringBootApplication(exclude = [ContextResourceLoaderAutoConfiguration::class])
class WebClipBoardApplication

fun main(args: Array<String>) {
	runApplication<WebClipBoardApplication>(*args)
}
