package com.webClipBoard

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository: JpaRepository<Account, Long> {
    fun findByUserId(userId: String): Optional<Account>
}