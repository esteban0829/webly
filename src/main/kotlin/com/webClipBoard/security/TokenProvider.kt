package com.webClipBoard.security

import com.webClipBoard.AccountRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*


@Component
class TokenProvider(
    private val jwtConfig: JwtConfig,
    private val accountRepository: AccountRepository,
) {

    lateinit var key: Key

    init {
        val keyBytes = Decoders.BASE64.decode(jwtConfig.jwtSecret)
        this.key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun getAuthentication(token: String): Authentication {
        val claims = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        val principal = accountRepository.findByEmail(claims.subject).get()
        return UsernamePasswordAuthenticationToken(principal, "", principal.authorities)
    }

    fun createToken(userDetails: UserDetails): String {
        val now = Date().time
        val validity = Date(now + 24 * 60 * 60 * 1000)

        return Jwts
            .builder()
            .setSubject(userDetails.username)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact()
    }
}
