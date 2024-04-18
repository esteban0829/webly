package com.webClipBoard.controller

import com.webClipBoard.LoginRequestDto
import com.webClipBoard.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequestDto): ResponseEntity<String> {
        return ResponseEntity.ok(
            authService.getToken(request.username, request.password))
    }
}