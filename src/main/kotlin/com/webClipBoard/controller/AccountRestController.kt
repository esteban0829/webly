package com.webClipBoard.controller

import com.webClipBoard.AccountCreateDTO
import com.webClipBoard.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/accounts")
class AccountRestController(
    private val accountService: AccountService,
) {

    @Transactional
    @PostMapping("/register")
    fun registerAccount(
        @RequestBody accountCreateDTO: AccountCreateDTO
    ): ResponseEntity<Any> {
        accountService.createAccount(accountCreateDTO)
        return ResponseEntity(HttpStatus.CREATED)
    }

}