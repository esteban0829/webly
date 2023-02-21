package com.webClipBoard

import io.awspring.cloud.autoconfigure.context.ContextResourceLoaderAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*
import javax.annotation.PostConstruct

@SpringBootApplication(exclude = [ContextResourceLoaderAutoConfiguration::class])
class WebClipBoardApplication {
	@PostConstruct
	fun started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}

fun main(args: Array<String>) {
	runApplication<WebClipBoardApplication>(*args)
}
