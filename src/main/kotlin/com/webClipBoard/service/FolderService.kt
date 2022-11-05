package com.webClipBoard.service

import com.webClipBoard.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FolderService(
    private val projectService: ProjectService,
    private val folderRepository: FolderRepository,
) {

    @Transactional
    fun getFolders(account: Account, projectId: Long, parentId: Long?): List<FolderDTO> {
        val projectAccount = projectService.getProjectAccountById(account, projectId)
        val parentFolder = parentId?.let {
            folderRepository.findByIdOrNull(it)
                ?: throw FolderNotFoundException()
        }
        return folderRepository.findByProjectAndParent(projectAccount.project, parentFolder).map { it.toDTO() }
    }

    @Transactional
    fun createFolder(account: Account, projectId: Long, createFolderDTO: CreateFolderDTO): Long {
        val projectAccount = projectService.getProjectAccountById(account, projectId)
        val parentFolder = createFolderDTO.parentId?.let {
            folderRepository.findByIdOrNull(it)
                ?: throw FolderNotFoundException()
        }
        val folder = Folder(
            name = createFolderDTO.name,
            parent = parentFolder,
            project = projectAccount.project,
        )
        folderRepository.save(folder)

        return folder.id!!
    }

    @Transactional
    fun renameFolder(account: Account, projectId: Long, folderId: Long, newName: String) {
        val projectAccount = projectService.getProjectAccountById(account, projectId)
        val folder = folderRepository.findByIdAndProject(folderId, projectAccount.project)
            ?: throw FolderNotFoundException()

        folder.name = newName
    }

    @Transactional
    fun deleteFolder(account: Account, projectId: Long, folderId: Long) {
        val projectAccount = projectService.getProjectAccountById(account, projectId)
        val folder = folderRepository.findByIdAndProject(folderId, projectAccount.project)
            ?: throw FolderNotFoundException()

        folderRepository.delete(folder)
    }

    @Transactional
    fun moveFolder(account: Account, projectId: Long, folderId: Long, targetParentId: Long?) {
        val projectAccount = projectService.getProjectAccountById(account, projectId)
        val folder = folderRepository.findByIdAndProject(folderId, projectAccount.project)
            ?: throw FolderNotFoundException()
        val targetFolder = targetParentId?.let {
            folderRepository.findByIdAndProject(targetParentId, projectAccount.project)
                ?: throw FolderNotFoundException()
        }
        if (targetFolder != null && targetFolder.isDescendantOf(folder)) {
            throw NotAllowedMoveToChildFolderException()
        }
        folder.parent = targetFolder
    }

    @Transactional
    fun getFolderDetail(account: Account, projectId: Long, folderId: Long): FolderDetailDTO {
        val projectAccount = projectService.getProjectAccountById(account, projectId)
        val folder = folderRepository.findByIdAndProject(folderId, projectAccount.project)
            ?.run {
                val childFolders = childFolders.map { it.toDTO() }
                val childLinks = childLinks.map { it.toDTO() }

                FolderDetailDTO(
                    id = id!!,
                    name = name,
                    parentId = parent?.id,
                    childFolders = childFolders,
                    childLinks = childLinks,
                )
            }
            ?: throw FolderNotFoundException()

        return folder
    }

    @Transactional
    fun getFolderIfAccountHasPermission(account: Account, projectId: Long, folderId: Long): Folder {
        val projectAccount = projectService.getProjectAccountById(account, projectId)
        return folderRepository.findByIdAndProject(folderId, projectAccount.project)
            ?: throw FolderNotFoundException()
    }

}