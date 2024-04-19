package com.webClipBoard.controller

import com.webClipBoard.Account
import com.webClipBoard.AccountDTO
import com.webClipBoard.LoginRequestDto
import com.webClipBoard.LoginSuccessDto
import com.webClipBoard.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
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
    fun login(@RequestBody request: LoginRequestDto): ResponseEntity<LoginSuccessDto> {
        return ResponseEntity.ok(
            authService.getToken(request.username, request.password))
    }

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal account: Account): ResponseEntity<AccountDTO> {
        return ResponseEntity.ok(account.toDTO())
    }
}