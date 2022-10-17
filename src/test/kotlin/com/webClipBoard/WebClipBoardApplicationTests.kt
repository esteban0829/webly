package com.webClipBoard

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WebClipBoardApplicationTests {

	companion object {
		val disableEc2Metadata = System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true")
	}

	@Test
	fun contextLoads() {
	}

}
