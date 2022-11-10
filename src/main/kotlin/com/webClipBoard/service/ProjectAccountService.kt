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
    fun addAccountToProject(account: Account, projectId: Long, createProjectAccountDTO: CreateProjectAccountDTO) {
        val project = projectRepository.findByIdOrNull(projectId)
                ?: throw ProjectNotFoundException()
        val targetAccount = accountRepository.findByEmail(createProjectAccountDTO.email)
                .orElseThrow(::UserNotFoundException)
        val projectAccount = projectAccountRepository.findByAccountAndProject(account, project)
                ?: throw UnAuthorizedProjectException()
        if (!projectAccount.canAddAccountToProject(createProjectAccountDTO.isAdmin)) {
            throw UnAuthorizedProjectException()
        }
        projectAccountRepository.save(ProjectAccount(
                project = project,
                account = targetAccount,
                projectAccountType = createProjectAccountDTO.accountType()
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

    @Transactional
    fun getProjectAccounts(actorAccount: Account, projectId: Long): List<ProjectAccountDTO> {
        val project = projectRepository.findByIdOrNull(projectId)
                ?: throw ProjectNotFoundException()

        return projectAccountRepository.findByProject(project).map { ProjectAccountDTO.of(it) }
    }

}