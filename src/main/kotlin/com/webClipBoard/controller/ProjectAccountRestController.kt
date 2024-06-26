package com.webClipBoard.controller

import com.webClipBoard.Account
import com.webClipBoard.CreateProjectAccountDTO
import com.webClipBoard.ProjectAccountDTO
import com.webClipBoard.service.ProjectAccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/projects/{projectId}/accounts")
class ProjectAccountRestController(
    private val projectAccountService: ProjectAccountService,
) {

    @GetMapping
    fun getAccounts(
        @AuthenticationPrincipal account: Account,
        @PathVariable projectId: Long,
    ): ResponseEntity<List<ProjectAccountDTO>> {
        return ResponseEntity
                .ok()
                .body(projectAccountService.getProjectAccounts(account, projectId))
    }

    @PostMapping
    fun addAccount(
        @AuthenticationPrincipal account: Account,
        @PathVariable projectId: Long,
        @RequestBody createProjectAccountDTO: CreateProjectAccountDTO,
    ): ResponseEntity<Long> {
        return ResponseEntity.ok()
                .body(projectAccountService.addAccountToProject(account, projectId, createProjectAccountDTO))
    }

    @DeleteMapping("/{accountId}")
    fun deleteAccount(
        @AuthenticationPrincipal account: Account,
        @PathVariable projectId: Long,
        @PathVariable accountId: Long,
    ): ResponseEntity<Unit> {
        projectAccountService.deleteAccountToProject(account, projectId, accountId)

        return ResponseEntity(HttpStatus.OK)
    }
}