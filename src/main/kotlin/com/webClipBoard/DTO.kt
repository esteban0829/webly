package com.webClipBoard

import java.time.OffsetDateTime

data class AccountDTO(
    val id: Long,
    val name: String,
    val userId: String,
    val userPassword: String,
    val role: Role,
    val createDateTime: OffsetDateTime,
    val updateDateTime: OffsetDateTime?,
)

data class AccountCreateDTO(
    val userId: String,
    val userPassword: String,
    val userEmail: String,
    val userName: String,
    val role: Role,
)

data class FileCreateDTO(
    val fileName: String,
)

data class FileDTO(
    val id: Long,
    val name: String,
    val filePath: String,
    val status: FileStatus,
    val createDateTime: OffsetDateTime,
    val updateDateTime: OffsetDateTime?,
)

data class FileUserDTO(
    val id: Long,
    val name: String,
    val filePath: String,
    val status: FileStatus,
    val createDateTime: OffsetDateTime,
    val updateDateTime: OffsetDateTime?,
    val presignedUrl: String,
)

data class ProjectDTO(
    val id: Long,
    val name: String,
) {
    companion object {
        fun of(project: Project) = ProjectDTO(
            id = project.id!!,
            name = project.name,
        )
    }
}

data class ProjectAccountDTO(
    val id: Long,
    val accountName: String,
    val accountType: ProjectAccountType,
) {
    companion object {
        fun of(projectAccount: ProjectAccount) = ProjectAccountDTO(
            id = projectAccount.id!!,
            accountName = projectAccount.account.name,
            accountType = projectAccount.projectAccountType,
        )
    }
}

data class CreateProjectAccountDTO(
    val email: String,
    val isAdmin: Boolean,
) {
    fun accountType(): ProjectAccountType {
        return if (isAdmin) {
            ProjectAccountType.ADMIN
        } else {
            ProjectAccountType.USER
        }
    }
}

data class CreateProjectDTO(
    val name: String,
) {
    fun toEntity() = Project(
        name = name,
    )
}

data class FolderDTO(
    val id: Long,
    val name: String,
    val parentId: Long?,
) {
    companion object {
        fun of(folder: Folder) = FolderDTO(
            id = folder.id!!,
            name = folder.name,
            parentId = folder.parent?.id
        )
    }
}

data class FolderDetailDTO(
    val id: Long,
    val name: String,
    val parentId: Long?,
    val childFolders: List<FolderDTO>,
    val childLinks: List<LinkDTO>,
)

data class CreateFolderDTO(
    val name: String,
    val parentId: Long?,
)

data class LinkDTO(
    val id: Long,
    val name: String,
    val url: String,
    val folderId: Long,
) {
    companion object {
        fun of(link: Link) = LinkDTO(
            id = link.id!!,
            name = link.name,
            url = link.url,
            folderId = link.folder!!.id!!
        )
    }
}

data class CreateLinkDTO(
    val name: String,
    val url: String,
)

data class ActionLogDTO(
    val actionType: ActionType,
    val linkId: Long? = null,
    val folderId: Long? = null,
    val oldName: String? = null,
    val newName: String? = null,
    val fromFolderId: Long? = null,
    val toFolderId: Long? = null,
) {
    companion object {
        fun of(actionLog: ActionLog): ActionLogDTO = actionLog.run {
            when (this) {
                is CreateLinkActionLog -> ActionLogDTO(ActionType.CREATE_LINK, linkId = linkId)
                is DeleteLinkActionLog -> ActionLogDTO(ActionType.DELETE_LINK, linkId = linkId)
                is RenameLinkActionLog -> ActionLogDTO(
                        ActionType.RENAME_LINK,
                        linkId = linkId,
                        oldName = oldName,
                        newName = newName,
                )
                is MoveLinkActionLog -> ActionLogDTO(
                        ActionType.MOVE_LINK,
                        linkId = linkId,
                        fromFolderId = fromFolderId,
                        toFolderId = toFolderId,
                )
                is CreateFolderActionLog -> ActionLogDTO(ActionType.CREATE_FOLDER, folderId = folderId)
                is DeleteFolderActionLog -> ActionLogDTO(ActionType.DELETE_FOLDER, folderId = folderId)
                is RenameFolderActionLog -> ActionLogDTO(
                        ActionType.RENAME_FOLDER,
                        folderId = folderId,
                        oldName = oldName,
                        newName = newName,
                )
                is MoveFolderActionLog -> ActionLogDTO(
                        ActionType.MOVE_FOLDER,
                        folderId = folderId,
                        fromFolderId = fromFolderId,
                        toFolderId = toFolderId,
                )
                else -> throw Exception("unknown Action Type")
            }
        }
    }
}