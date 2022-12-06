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
    @Query("select f from File f where f.id = :fileId")
    fun findByIdForUpdate(fileId: Long): File
}

@Repository
interface PostRepository: JpaRepository<Post, Long> {
    fun findByCreatorOrderByUpdateDateTime(creator: Account): List<Post>
}

@Repository
interface ProjectRepository: JpaRepository<Project, Long> {
}

@Repository
interface ProjectAccountRepository: JpaRepository<ProjectAccount, Long> {
    fun findByAccountAndProject(account: Account, project: Project): ProjectAccount?

    fun findByProject(project: Project): List<ProjectAccount>
}

@Repository
interface FolderRepository: JpaRepository<Folder, Long> {
    fun findByProjectAndParent(project: Project, parent: Folder?): List<Folder>
    fun findByIdAndProject(id: Long, project: Project): Folder?
}

@Repository
interface LinkRepository: JpaRepository<Link, Long> {
}

@Repository
interface ActionLogRepository: JpaRepository<ActionLog, Long> {
}
