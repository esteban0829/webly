package com.webClipBoard.service

import com.webClipBoard.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectAccountRepository: ProjectAccountRepository,
) {

    @Transactional
    fun getProjects(account: Account): List<ProjectDTO> {
        return projectRepository.findByAccountId(account.id!!).map { it.toDto() }
    }

    @Transactional
    fun deleteProject(id: Long, account: Account) {
        val project = projectRepository.findById(id).orElseThrow()
        val projectAccount = projectAccountRepository.findByAccountAndProject(account, project)
        if (projectAccount.projectAccountType == ProjectAccountType.OWNER) {
            projectRepository.delete(project)
        } else {
            throw Exception("no auth for delete account")
        }
    }

    @Transactional
    fun renameProject(id: Long, newName: String, account: Account) {
        val project = projectRepository.findById(id).orElseThrow()
        val projectAccount = projectAccountRepository.findByAccountAndProject(account, project)
        if (projectAccount.projectAccountType == ProjectAccountType.OWNER) {
            project.name = newName
        } else {
            throw Exception("no auth for delete account")
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

}