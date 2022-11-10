package com.webClipBoard.service

import com.webClipBoard.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectAccountService(
    private val accountRepository: AccountRepository,
    private val projectRepository: ProjectRepository,
    private val projectAccountRepository: ProjectAccountRepository,
) {

    @Transactional
    fun addAccountToProject(account: Account, projectId: Long, targetAccountId: Long, isAdmin: Boolean) {
        val project = projectRepository.findByIdOrNull(projectId)
                ?: throw ProjectNotFoundException()
        val targetAccount = accountRepository.findByIdOrNull(targetAccountId)
                ?: throw UserNotFoundException()
        val projectAccount = projectAccountRepository.findByAccountAndProject(account, project)
                ?: throw UnAuthorizedProjectException()
        if (!projectAccount.canAddAccountToProject(isAdmin)) {
            throw UnAuthorizedProjectException()
        }
        val accountType = if (isAdmin) {
            ProjectAccountType.ADMIN
        } else {
            ProjectAccountType.USER
        }
        projectAccountRepository.save(ProjectAccount(
                project = project,
                account = targetAccount,
                projectAccountType = accountType
        ))
    }

    @Transactional
    fun deleteAccountToProject(actorAccount: Account, projectId: Long, accountId: Long) {
        val project = projectRepository.findByIdOrNull(projectId)
                ?: throw ProjectNotFoundException()
        val targetAccount = accountRepository.findByIdOrNull(accountId)
                ?: throw UserNotFoundException()
        val projectAccount = projectAccountRepository.findByAccountAndProject(actorAccount, project)
                ?: throw UnAuthorizedProjectException()
        val targetProjectAccount = projectAccountRepository.findByAccountAndProject(targetAccount, project)
                ?: throw UserNotFoundException()
        if (!projectAccount.canDeleteAccountToProject(targetProjectAccount)) {
            throw UnAuthorizedProjectException()
        }

        projectAccountRepository.delete(targetProjectAccount)
    }

}