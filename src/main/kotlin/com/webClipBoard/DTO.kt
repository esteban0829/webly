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

class FileDTO(
    val id: Long,
    val name: String,
    val filePath: String,
    val status: FileStatus,
    val createDateTime: OffsetDateTime,
    val updateDateTime: OffsetDateTime?,
)

class FileUserDTO(
    val id: Long,
    val name: String,
    val filePath: String,
    val status: FileStatus,
    val createDateTime: OffsetDateTime,
    val updateDateTime: OffsetDateTime?,
    val presignedUrl: String,
)

class ProjectDTO(
    val id: Long,
    val name: String,
)

fun Project.toDTO(): ProjectDTO {
    return ProjectDTO(
        id = this.id!!,
        name = this.name,
    )
}

class CreateProjectDTO(
    val name: String,
) {
    fun toEntity() = Project(
        name = name,
    )
}

class FolderDTO(
    val id: Long,
    val name: String,
    val parentId: Long?,
)

fun Folder.toDTO(): FolderDTO {
    return FolderDTO(
        id = id!!,
        name = name,
        parentId = parent?.id
    )
}

class FolderDetailDTO(
    val id: Long,
    val name: String,
    val parentId: Long?,
    val childFolders: List<FolderDTO>,
    val childLinks: List<LinkDTO>,
)

class CreateFolderDTO(
    val name: String,
    val parentId: Long?,
)

class LinkDTO(
    val id: Long,
    val name: String,
    val url: String,
    val folderId: Long,
)

fun Link.toDTO(): LinkDTO {
    return LinkDTO(
        id = id!!,
        name = name,
        url = url,
        folderId = folder!!.id!!
    )
}

class CreateLinkDTO(
    val name: String,
    val url: String,
)

class ActionLogDTO(
    val actionType: ActionType,
    val linkId: Long? = null,
    val folderId: Long? = null,
    val oldName: String? = null,
    val newName: String? = null,
    val fromFolderId: Long? = null,
    val toFolderId: Long? = null,
)

fun ActionLog.toDTO(): ActionLogDTO = when (this) {
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