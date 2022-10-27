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

        if (accountService.checkIfAccountExist(userCreateDTO.email)) {
            throw Exception("email already existing")
        }

        userCreateDTO.run {
            accountService.createAccount(
                AccountCreateDTO(
                    userId = userId,
                    userPassword = password,
                    userEmail = email,
                    userName = userName,
                    role = role,
                )
            )
        }
        return "redirect:/login"
    }
}

data class UserCreateDTO(
    val email: String,
    val password: String,
    val role: Role,
    val userId: String,
    val userName: String,
)