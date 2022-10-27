package com.webClipBoard

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*
import org.springframework.security.core.authority.SimpleGrantedAuthority

import org.springframework.security.core.GrantedAuthority
import java.time.OffsetDateTime


enum class Role(val permissionLevel: Long, val authority: String) {
    ADMIN(1, "ADMIN"),
    USER(2, "USER"),
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
): UserDetails, BaseTimeEntity() {
    fun toDTO() = AccountDTO(
        id = id!!,
        name = name,
        userId = userId,
        userPassword = userPassword,
        role = role,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
    )

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return Role.values()
            .filter { this.role.permissionLevel <= it.permissionLevel }
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

enum class FileStatus {
    UPLOADING, DONE,
}

@Entity
data class File(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val filePath: String,
    @Enumerated(EnumType.STRING)
    var status: FileStatus = FileStatus.UPLOADING
): BaseTimeEntity() {
    fun toDTO() = FileDTO(
        id = id!!,
        name = name,
        filePath = filePath,
        status = status,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime
    )
}

@MappedSuperclass
abstract class BaseTimeEntity(
    @CreationTimestamp
    val createDateTime: OffsetDateTime = OffsetDateTime.now(),
    @UpdateTimestamp
    val updateDateTime: OffsetDateTime? = null,
)