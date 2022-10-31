package com.webClipBoard.controller

import com.webClipBoard.Account
import com.webClipBoard.CreateProjectDTO
import com.webClipBoard.ProjectDTO
import com.webClipBoard.service.ProjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/api/v1")
class ProjectRestController(
    private val projectService: ProjectService
) {

    @PostMapping("/project")
    fun createProject(
        @AuthenticationPrincipal account: Account,
        @RequestBody createProjectDTO: CreateProjectDTO,
    ): ResponseEntity<Long> {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.createProject(createProjectDTO, account))
    }

    @GetMapping("/projects")
    fun getProjects(
        @AuthenticationPrincipal account: Account,
    ): List<ProjectDTO> {
        return projectService.getProjects(account)
    }

    @DeleteMapping("/project/{id}")
    fun deleteProject(
        @AuthenticationPrincipal account: Account,
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        projectService.deleteProject(id, account)

        return ResponseEntity.noContent().build()
    }

    @PostMapping("/project/{id}/rename")
    fun getProject(
        @AuthenticationPrincipal account: Account,
        @PathVariable id: Long,
        @RequestBody newName: String
    ): ResponseEntity<Unit> {
        projectService.renameProject(id, newName, account)

        return ResponseEntity(HttpStatus.OK)
    }
}