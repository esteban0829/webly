package com.webClipBoard

import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.HashSet

import org.springframework.security.core.GrantedAuthority




enum class Role(val priority: Long) {
    ADMIN(1),
    USER(2),
}

@Entity
data class Account(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: String,
    val userPassword: String,
    val email: String,
    val name: String,
    @Enumerated(EnumType.STRING)
    val role: Role,
): UserDetails {
    fun toDTO() = AccountDTO(
        id = id!!,
        name = name,
        userId = userId,
        userPassword = userPassword,
        role = role,
    )

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return Role.values()
            .filter { this.role.priority >= it.priority }
            .map { SimpleGrantedAuthority(it.toString()) }
            .toSet()
    }

    override fun getPassword(): String {
        return userPassword
    }

    override fun getUsername(): String {
        return id.toString()
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
@Entity
data class FileEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val filePath: String,
)
