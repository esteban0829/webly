package com.webClipBoard.controller

import com.webClipBoard.Account
import com.webClipBoard.FolderDTO
import com.webClipBoard.CreateFolderDTO
import com.webClipBoard.FolderDetailDTO
import com.webClipBoard.service.FolderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/projects/{projectId}/folders")
class FolderRestController(
    private val folderService: FolderService
) {

    @GetMapping
    fun getFolders(
        @PathVariable projectId: Long,
        @RequestParam parentId: Long?,
        @AuthenticationPrincipal account: Account
    ): ResponseEntity<List<FolderDTO>> {
        return ResponseEntity
            .ok()
            .body(folderService.getFolders(account, projectId, parentId))
    }

    @GetMapping("/{folderId}")
    fun getFolderDetail(
        @PathVariable projectId: Long,
        @PathVariable folderId: Long,
        @AuthenticationPrincipal account: Account,
    ): ResponseEntity<FolderDetailDTO> {
        return ResponseEntity
            .ok()
            .body(folderService.getFolderDetail(account, projectId, folderId))
    }

    @PostMapping
    fun createFolder(
        @PathVariable projectId: Long,
        @AuthenticationPrincipal account: Account,
        @RequestBody createFolderDTO: CreateFolderDTO,
    ): ResponseEntity<Long> {
        return ResponseEntity
            .ok()
            .body(folderService.createFolder(account, projectId, createFolderDTO))
    }

    @PostMapping("/{folderId}/rename")
    fun renameFolder(
        @PathVariable projectId: Long,
        @PathVariable folderId: Long,
        @AuthenticationPrincipal account: Account,
        @RequestBody newName: String,
    ): ResponseEntity<Unit> {
        folderService.renameFolder(account, projectId, folderId, newName)

        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("/{folderId}")
    fun deleteFolder(
        @PathVariable projectId: Long,
        @PathVariable folderId: Long,
        @AuthenticationPrincipal account: Account,
    ): ResponseEntity<Unit> {
        folderService.deleteFolder(account, projectId, folderId)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/{folderId}/move")
    fun moveFolder(
        @PathVariable projectId: Long,
        @PathVariable folderId: Long,
        @RequestBody targetParentId: Long?,
        @AuthenticationPrincipal account: Account,
    ): ResponseEntity<Unit> {
        folderService.moveFolder(account, projectId, folderId, targetParentId)

        return ResponseEntity(HttpStatus.OK)
    }
}