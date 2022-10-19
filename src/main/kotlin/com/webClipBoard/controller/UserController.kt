package com.webClipBoard.controller

import com.webClipBoard.AccountCreateDTO
import com.webClipBoard.Role
import com.webClipBoard.service.AccountService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping

@Controller
class UserController(
    private val accountService: AccountService,
) {

    @PostMapping("/user")
    fun saveUser(userCreateDTO: UserCreateDTO): String {
        userCreateDTO.let {
            when (it.role) {
                Role.ADMIN -> accountService.createAdminAccount(
                    AccountCreateDTO(
                        userId = "",
                        userPassword = it.password,
                        userEmail = it.email,
                        userName = ""
                    )
                )
                Role.USER -> accountService.createUserAccount(
                    AccountCreateDTO(
                        userId = "",
                        userPassword = it.password,
                        userEmail = it.email,
                        userName = ""
                    )
                )
            }
        }
        return "redirect:/login"
    }
}

data class UserCreateDTO(
    val email: String,
    val password: String,
    val role: Role,
)