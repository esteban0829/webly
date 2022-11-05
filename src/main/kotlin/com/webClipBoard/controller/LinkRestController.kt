package com.webClipBoard.controller

import com.webClipBoard.Account
import com.webClipBoard.CreateLinkDTO
import com.webClipBoard.LinkDTO
import com.webClipBoard.service.LinkService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/v1/projects/{projectId}/folders/{folderId}/links")
class LinkRestController(
    private val linkService: LinkService,
) {

    @GetMapping("/{linkId}")
    fun getLink(
        @PathVariable projectId: Long,
        @PathVariable folderId: Long,
        @PathVariable linkId: Long,
        @AuthenticationPrincipal account: Account,
    ): ResponseEntity<LinkDTO> {
        return ResponseEntity
            .ok()
            .body(linkService.getLink(account, projectId, folderId, linkId))
    }

    @PostMapping
    fun createLink(
        @PathVariable projectId: Long,
        @PathVariable folderId: Long,
        @AuthenticationPrincipal account: Account,
        @RequestBody createLinkDTO: CreateLinkDTO,
    ): ResponseEntity<Long> {
        return ResponseEntity
            .ok()
            .body(linkService.createLink(account, projectId, folderId, createLinkDTO))
    }

    @DeleteMapping("/{linkId}")
    fun deleteLink(
        @PathVariable projectId: Long,
        @PathVariable folderId: Long,
        @PathVariable linkId: Long,
        @AuthenticationPrincipal account: Account,
    ): ResponseEntity<Unit> {
        linkService.deleteLink(account, projectId, folderId, linkId)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/{linkId}/rename")
    fun renameLink(
        @PathVariable projectId: Long,
        @PathVariable folderId: Long,
        @PathVariable linkId: Long,
        @AuthenticationPrincipal account: Account,
        @RequestBody newName: String,
    ): ResponseEntity<Unit> {
        linkService.renameLink(account, projectId, folderId, linkId, newName)

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/{linkId}/move")
    fun moveLink(
        @PathVariable projectId: Long,
        @PathVariable folderId: Long,
        @PathVariable linkId: Long,
        @AuthenticationPrincipal account: Account,
        @RequestBody targetFolderId: Long,
    ): ResponseEntity<Unit> {
        linkService.moveLink(account, projectId, folderId, linkId, targetFolderId)

        return ResponseEntity(HttpStatus.OK)
    }
}