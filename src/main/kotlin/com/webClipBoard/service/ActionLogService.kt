package com.webClipBoard.service

import com.webClipBoard.*
import com.webClipBoard.repository.ActionLogQuerydslRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActionLogService(
    private val actionLogRepository: ActionLogRepository,
    private val actionLogQuerydslRepository: ActionLogQuerydslRepository,
    private val projectService: ProjectService,
) {

    @Transactional
    fun getActionLogs(account: Account, projectId: Long, lastReadActionId: Long): List<ActionLogDTO> {
        val projectAccount = projectService.getProjectAccountById(account, projectId)
        return actionLogQuerydslRepository.findActionLogAfterLastId(projectAccount.project, lastReadActionId)
            .map(ActionLogDTO::of)
    }

    @Transactional
    fun recentActionId(account: Account, projectId: Long): Long {
        return actionLogQuerydslRepository.findMaxIdOrNull(projectId) ?: -1
    }

    @Transactional
    fun logCreateLink(project: Project, link: Link) {
        actionLogRepository.save(CreateLinkActionLog(
            project = project,
            linkId = link.id!!,
            newName = link.name,
            url = link.url,
            parentId = link.folder?.id!!,
        ))
    }

    @Transactional
    fun logDeleteLink(project: Project, linkId: Long) {
        actionLogRepository.save(DeleteLinkActionLog(
            project = project,
            linkId = linkId
        ))
    }

    @Transactional
    fun logRenameLink(project: Project, linkId: Long, oldName: String, newName: String) {
        actionLogRepository.save(RenameLinkActionLog(
            project = project,
            linkId = linkId,
            oldName = oldName,
            newName = newName,
        ))
    }

    @Transactional
    fun logMoveLink(project: Project, linkId: Long, fromFolderId: Long?, toFolderId: Long?) {
        actionLogRepository.save(MoveLinkActionLog(
            project = project,
            linkId = linkId,
            fromFolderId = fromFolderId,
            toFolderId = toFolderId,
        ))
    }

    @Transactional
    fun logCreateFolder(project: Project, folder: Folder) {
        actionLogRepository.save(CreateFolderActionLog(
            project = project,
            folderId = folder.id!!,
            name = folder.name,
            parentId = folder.parent?.id
        ))
    }

    @Transactional
    fun logDeleteFolder(project: Project, folderId: Long) {
        actionLogRepository.save(DeleteFolderActionLog(
            project = project,
            folderId = folderId,
        ))
    }

    @Transactional
    fun logRenameFolder(project: Project, folderId: Long, oldName: String, newName: String) {
        actionLogRepository.save(RenameFolderActionLog(
            project = project,
            folderId = folderId,
            oldName = oldName,
            newName = newName,
        ))
    }

    @Transactional
    fun logMoveFolder(project: Project, folderId: Long, fromFolderId: Long?, toFolderId: Long?) {
        actionLogRepository.save(MoveFolderActionLog(
            project = project,
            folderId = folderId,
            fromFolderId = fromFolderId,
            toFolderId = toFolderId,
        ))
    }

}