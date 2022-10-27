package com.webClipBoard

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.LockModeType

@Repository
interface AccountRepository: JpaRepository<Account, Long> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): Optional<Account>
}

@Repository
interface FileRepository: JpaRepository<File, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from File f where id = ?1")
    fun findByIdForUpdate(fileId: Long): File
}