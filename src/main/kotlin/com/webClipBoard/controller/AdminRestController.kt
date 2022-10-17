package com.webClipBoard.controller

import com.webClipBoard.AccountCreateDTO
import com.webClipBoard.AccountDTO
import com.webClipBoard.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/")
class AdminRestController(
    private val accountService: AccountService,
){

    @Transactional
    @PostMapping("/accounts/register")
    fun registerAccount(
        @RequestBody accountCreateDTO: AccountCreateDTO
    ): ResponseEntity<AccountDTO> {
        return ResponseEntity(
            accountService.createAdminAccount(accountCreateDTO),
            HttpStatus.CREATED
        )
    }

    @GetMapping("/accounts")
    fun getAccounts(): ResponseEntity<List<AccountDTO>> {
        return ResponseEntity(
            accountService.getAll(),
            HttpStatus.ACCEPTED
        )
    }
}