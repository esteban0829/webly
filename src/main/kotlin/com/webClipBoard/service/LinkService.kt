package com.webClipBoard.service

import com.webClipBoard.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LinkService(
    private val folderService: FolderService,
    private val linkRepository: LinkRepository,
) {

    @Transactional
    fun createLink(account: Account, projectId: Long, folderId: Long, createLinkDTO: CreateLinkDTO): Long {
        val folder = folderService.getFolderIfAccountHasPermission(account, projectId, folderId)
        val link = Link(
            name = createLinkDTO.name,
            url = createLinkDTO.url,
            folder = folder,
        )
        linkRepository.save(link)

        return link.id!!
    }

    @Transactional
    fun deleteLink(account: Account, projectId: Long, folderId: Long, linkId: Long) {
        val folder = folderService.getFolderIfAccountHasPermission(account, projectId, folderId)
        val link = linkRepository.findByIdOrNull(linkId)
        if (link == null || link.folder != folder) {
            throw LinkNotFoundException()
        }
        linkRepository.delete(link)
    }

    @Transactional
    fun renameLink(account: Account, projectId: Long, folderId: Long, linkId: Long, newName: String) {
        val folder = folderService.getFolderIfAccountHasPermission(account, projectId, folderId)
        val link = linkRepository.findByIdOrNull(linkId)
        if (link == null || link.folder != folder) {
            throw LinkNotFoundException()
        }
        link.name = newName
    }

    @Transactional
    fun moveLink(account: Account, projectId: Long, folderId: Long, linkId: Long, targetFolderId: Long) {
        val folder = folderService.getFolderIfAccountHasPermission(account, projectId, folderId)
        val link = linkRepository.findByIdOrNull(linkId)
        if (link == null || link.folder != folder) {
            throw LinkNotFoundException()
        }
        val targetFolder = folderService.getFolderIfAccountHasPermission(account, projectId, targetFolderId)
        link.folder = targetFolder
    }

    @Transactional
    fun getLink(account: Account, projectId: Long, folderId: Long, linkId: Long): LinkDTO {
        val folder = folderService.getFolderIfAccountHasPermission(account, projectId, folderId)
        val link = linkRepository.findByIdOrNull(linkId)
        if (link == null || link.folder != folder) {
            throw LinkNotFoundException()
        }
        return link.toDTO()
    }
}