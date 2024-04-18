package com.webClipBoard.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/health")
class HelloRestController {

    @PostMapping("/hello")
    fun hello(): ResponseEntity<String> {
        return ResponseEntity(
            "hello",
            HttpStatus.OK
        )
    }
}