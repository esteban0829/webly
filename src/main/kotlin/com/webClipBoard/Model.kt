package com.webClipBoard

import javax.persistence.*

enum class Role(val roleName: String) {
    USER("USER"),
    ADMIN("ADMIN"),
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
) {
    fun toDTO() = AccountDTO(
        id = id!!,
        name = name,
        userId = userId,
        userPassword = userPassword,
        role = role,
    )
}

@Entity
data class FileEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val filePath: String,
)
