package com.webClipBoard.controller

import com.webClipBoard.Account
import com.webClipBoard.CreateProjectDTO
import com.webClipBoard.ProjectDTO
import com.webClipBoard.service.ProjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/projects")
class ProjectRestController(
    private val projectService: ProjectService
) {

    @PostMapping
    fun createProject(
        @AuthenticationPrincipal account: Account,
        @RequestBody createProjectDTO: CreateProjectDTO,
    ): ResponseEntity<Long> {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.createProject(createProjectDTO, account))
    }

    @GetMapping
    fun getProjects(
        @AuthenticationPrincipal account: Account,
    ): List<ProjectDTO> {
        return projectService.getProjects(account)
    }

    @DeleteMapping("/{id}")
    fun deleteProject(
        @AuthenticationPrincipal account: Account,
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        projectService.deleteProject(id, account)

        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/rename")
    fun getProject(
        @AuthenticationPrincipal account: Account,
        @PathVariable id: Long,
        @RequestBody newName: String
    ): ResponseEntity<Unit> {
        projectService.renameProject(id, newName, account)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/{projectId}/accounts/{accountId}")
    fun addAccount(
        @AuthenticationPrincipal account: Account,
        @PathVariable projectId: Long,
        @PathVariable accountId: Long,
        @RequestBody isAdmin: Boolean,
    ): ResponseEntity<Unit> {
        projectService.addAccountToProject(account, projectId, accountId, isAdmin)

        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("/{projectId}/accounts/{accountId}")
    fun deleteAccount(
        @AuthenticationPrincipal account: Account,
        @PathVariable projectId: Long,
        @PathVariable accountId: Long,
    ): ResponseEntity<Unit> {
        projectService.deleteAccountToProject(account, projectId, accountId)

        return ResponseEntity(HttpStatus.OK)
    }
}