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

@Repository
interface ProjectRepository: JpaRepository<Project, Long> {
    @Query("select p.project from ProjectAccount p join p.project where p.account.id = :accountId")
    fun findByAccountId(accountId: Long): List<Project>
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
    fun findByParent(parent: Folder?): List<Folder>
}

@Repository
interface LinkRepository: JpaRepository<Link, Long> {
}

@Repository
interface ActionLogRepository: JpaRepository<ActionLog, Long> {
    @Query("select max(a.id) from ActionLog a where a.project.id = :projectId")
    fun findMaxIdOrNullByProjectId(projectId: Long): Long?
    fun findByIdAfterAndProjectOrderById(id: Long, project: Project): List<ActionLog>
}
