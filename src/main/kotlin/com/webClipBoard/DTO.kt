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
)

fun Link.toDTO(): LinkDTO {
    return LinkDTO(
        id = id!!,
        name = name,
        url = url,
    )
}

class CreateLinkDTO(
    val name: String,
    val url: String,
)
