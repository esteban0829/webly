package com.webClipBoard.service

import com.webClipBoard.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    private val accountRepository: AccountRepository,
) {
    @Transactional
    fun getAll(): List<AccountDTO> {
        return accountRepository.findAll().map { it.toDTO() }
    }

    @Transactional
    fun createUserAccount(accountCreateDTO: AccountCreateDTO): AccountDTO {
        accountCreateDTO.run {
            return accountRepository.save(Account(
                userId = userId,
                userPassword = userPassword,
                email = userEmail,
                name = userName,
                role = Role.USER,
            )).toDTO()
        }
    }

    @Transactional
    fun createAdminAccount(accountCreateDTO: AccountCreateDTO): AccountDTO {
        accountCreateDTO.run {
            return accountRepository.save(Account(
                userId = userId,
                userPassword = userPassword,
                email = userEmail,
                name = userName,
                role = Role.ADMIN,
            )).toDTO()
        }
    }
}