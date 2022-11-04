package com.webClipBoard.service

import com.webClipBoard.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectAccountRepository: ProjectAccountRepository,
    private val accountRepository: AccountRepository,
) {

    @Transactional
    fun getProjects(account: Account): List<ProjectDTO> {
        return projectRepository.findByAccountId(account.id!!).map { it.toDTO() }
    }

    @Transactional
    fun deleteProject(id: Long, account: Account) {
        val project = projectRepository.findByIdOrNull(id)
            ?: throw ProjectNotFoundException()
        val projectAccount = projectAccountRepository.findByAccountAndProject(account, project)
            ?: throw UnAuthorizedProjectException()
        if (projectAccount.projectAccountType == ProjectAccountType.OWNER) {
            projectAccountRepository.delete(projectAccount)
            projectRepository.delete(project)
        } else {
            throw UnAuthorizedProjectException()
        }
    }

    @Transactional
    fun renameProject(id: Long, newName: String, account: Account) {
        val project = projectRepository.findByIdOrNull(id)
            ?: throw ProjectNotFoundException()
        val projectAccount = projectAccountRepository.findByAccountAndProject(account, project)
            ?: throw UnAuthorizedProjectException()
        if (projectAccount.projectAccountType == ProjectAccountType.OWNER) {
            project.name = newName
        } else {
            throw UnAuthorizedProjectException()
        }
    }

    @Transactional
    fun createProject(createProjectDTO: CreateProjectDTO, account: Account): Long {
        val project = createProjectDTO.toEntity()
        projectRepository.save(project)
        projectAccountRepository.save(ProjectAccount(
            project = project,
            account = account,
            projectAccountType = ProjectAccountType.OWNER,
        ))
        return project.id!!
    }

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

    @Transactional
    fun getProjectAccountById(account: Account, projectId: Long): ProjectAccount {
        val project = projectRepository.findByIdOrNull(projectId)
            ?: throw ProjectNotFoundException()
        val projectAccount = projectAccountRepository.findByAccountAndProject(account, project)
            ?: throw UnAuthorizedProjectException()

        return projectAccount
    }

}