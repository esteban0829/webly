package com.webClipBoard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class WebClipBoardApplication

fun main(args: Array<String>) {
	runApplication<WebClipBoardApplication>(*args)
}
