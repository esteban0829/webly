package com.webClipBoard.service

import com.webClipBoard.*
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    private val accountRepository: AccountRepository,
): UserDetailsService {
    @Transactional
    fun getAll(): List<AccountDTO> {
        return accountRepository.findAll().map { it.toDTO() }
    }

    @Transactional
    fun createAccount(accountCreateDTO: AccountCreateDTO): AccountDTO {
        accountCreateDTO.run {
            return accountRepository.save(Account(
                userId = userId,
                userPassword = BCryptPasswordEncoder().encode(userPassword),
                email = userEmail,
                name = userName,
                role = role,
            )).toDTO()
        }
    }

    @Transactional
    fun checkIfAccountExist(email: String): Boolean {
        return accountRepository.existsByEmail(email)
    }

    override fun loadUserByUsername(email: String): UserDetails {
        try {
            return accountRepository.findByEmail(email).get()
        } catch (e: Exception) {
            throw UsernameNotFoundException("user with email: $email not found", e)
        }
    }
}