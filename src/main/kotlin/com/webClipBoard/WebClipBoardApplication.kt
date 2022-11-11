package com.webClipBoard

import io.awspring.cloud.autoconfigure.context.ContextResourceLoaderAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [ContextResourceLoaderAutoConfiguration::class])
class WebClipBoardApplication

fun main(args: Array<String>) {
	runApplication<WebClipBoardApplication>(*args)
}
