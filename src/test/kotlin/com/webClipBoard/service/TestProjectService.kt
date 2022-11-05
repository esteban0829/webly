package com.webClipBoard.service

import com.webClipBoard.Account
import com.webClipBoard.CreateProjectDTO
import com.webClipBoard.Project
import com.webClipBoard.ProjectRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TestProjectService(
    private val projectService: ProjectService,
    private val projectRepository: ProjectRepository,
) {

    fun createProject(account: Account): Project {
        val projectId = projectService.createProject(CreateProjectDTO(name = account.name), account)
        return projectRepository.findByIdOrNull(projectId)!!
    }

}