package com.webClipBoard.service

import com.webClipBoard.AccountRepository
import com.webClipBoard.LoginFailException
import com.webClipBoard.security.TokenProvider
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val tokenProvider: TokenProvider,
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun getToken(username: String, password: String): String? {
        val account = accountRepository.findByEmail(username).orElse(null)
        if (account != null && passwordEncoder.matches(password, account.password)) {
            return tokenProvider.createToken(account)
        }
        throw LoginFailException()
    }
}