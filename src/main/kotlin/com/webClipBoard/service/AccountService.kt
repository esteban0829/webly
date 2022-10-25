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
    fun createUserAccount(accountCreateDTO: AccountCreateDTO): AccountDTO {
        accountCreateDTO.run {
            return accountRepository.save(Account(
                userId = userId,
                userPassword = BCryptPasswordEncoder().encode(userPassword),
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
                userPassword = BCryptPasswordEncoder().encode(userPassword),
                email = userEmail,
                name = userName,
                role = Role.ADMIN,
            )).toDTO()
        }
    }

    @Transactional
    fun checkIfAccountExist(email: String): Boolean {
        return accountRepository.existsByEmail(email)
    }

    override fun loadUserByUsername(id: String): UserDetails {
        try {
            return accountRepository.findById(id.toLong()).get()
        } catch (e: Exception) {
            throw UsernameNotFoundException("user not found id: $id", e)
        }
    }
}