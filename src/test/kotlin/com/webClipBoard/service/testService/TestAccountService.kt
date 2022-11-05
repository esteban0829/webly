package com.webClipBoard.service.testService

import com.webClipBoard.Account
import com.webClipBoard.AccountRepository
import com.webClipBoard.Role
import org.springframework.stereotype.Service;

enum class AccountType(name: String) {
    OWNER("owner"),
    STRANGER("stranger"),
    ANOTHER_STRANGER("another_stranger")
}

@Service
class TestAccountService(
    private val accountRepository: AccountRepository,
) {

    fun createUser(accountType: AccountType): Account {
        val name = accountType.name
        val account = Account(
            email = name,
            userId = name,
            userPassword = "1234",
            name = name,
            role = Role.USER
        )
        return accountRepository.save(account)
    }

}
