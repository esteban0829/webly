package com.webClipBoard

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.OffsetDateTime
import javax.persistence.*


enum class Role(val permissionLevel: Long, val authority: String) {
    ADMIN(1, "ADMIN"),
    USER(2, "USER"),
}

@Entity
class Account(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: String,
    val userPassword: String,
    val email: String,
    val name: String,
    @Enumerated(EnumType.STRING)
    val role: Role,
): UserDetails, BaseTimeEntity() {
    fun toDTO() = AccountDTO(
        id = id!!,
        name = name,
        userId = userId,
        userPassword = userPassword,
        role = role,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
    )

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return Role.values()
            .filter { this.role.permissionLevel <= it.permissionLevel }
            .map { SimpleGrantedAuthority(it.toString()) }
            .toSet()
    }

    override fun getPassword(): String {
        return userPassword
    }

    override fun getUsername(): String {
        return id.toString()
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}

enum class FileStatus {
    UPLOADING, DONE,
}

@Entity
class File(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val filePath: String,
    @Enumerated(EnumType.STRING)
    var status: FileStatus = FileStatus.UPLOADING
): BaseTimeEntity() {
    fun toDTO() = FileDTO(
        id = id!!,
        name = name,
        filePath = filePath,
        status = status,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime
    )
}

@MappedSuperclass
abstract class BaseTimeEntity(
    @CreationTimestamp
    val createDateTime: OffsetDateTime = OffsetDateTime.now(),
    @UpdateTimestamp
    val updateDateTime: OffsetDateTime? = null,
)

@Entity
class Project(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var name: String,
) : BaseTimeEntity()

enum class ProjectAccountType {
    OWNER,
    ADMIN,
    USER,
}

@Entity
class ProjectAccount(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    val account: Account,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: Project,

    @Enumerated(EnumType.STRING)
    val projectAccountType: ProjectAccountType,
) : BaseTimeEntity() {

    fun canAddAccountToProject(targetIsAdmin: Boolean): Boolean {
        return projectAccountType == ProjectAccountType.OWNER ||
                projectAccountType == ProjectAccountType.ADMIN && !targetIsAdmin
    }

    fun canDeleteAccountToProject(targetAccount: ProjectAccount): Boolean {
        return projectAccountType == ProjectAccountType.OWNER &&
                targetAccount.projectAccountType != ProjectAccountType.OWNER
    }
}

@Entity
class Folder(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    var parent: Folder?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: Project,

    @OneToMany(mappedBy = "parent")
    val childFolders: MutableList<Folder> = ArrayList(),

    @OneToMany(mappedBy = "folder")
    val childLinks: MutableList<Link> = ArrayList(),
) : BaseTimeEntity() {
    fun isDescendantOf(folder: Folder): Boolean {
        return when(parent) {
            null -> false
            folder -> true
            else -> parent!!.isDescendantOf(folder)
        }
    }
}

@Entity
class Link(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var name: String,
    val url: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    var folder: Folder? = null,
) : BaseTimeEntity()

enum class ActionType(val value: String) {
    CREATE_LINK(Values.CreateLink),
    DELETE_LINK(Values.DeleteLink),
    RENAME_LINK(Values.RenameLink),
    MOVE_LINK(Values.MoveLink),
    CREATE_FOLDER(Values.CreateFolder),
    DELETE_FOLDER(Values.DeleteFolder),
    RENAME_FOLDER(Values.RenameFolder),
    MOVE_FOLDER(Values.MoveFolder);

    class Values {
        companion object {
            const val CreateLink = "CREATE_LINK"
            const val DeleteLink = "DELETE_LINK"
            const val RenameLink = "RENAME_LINK"
            const val MoveLink = "MOVE_LINK"
            const val CreateFolder = "CREATE_FOLDER"
            const val DeleteFolder = "DELETE_FOLDER"
            const val RenameFolder = "RENAME_FOLDER"
            const val MoveFolder = "MOVE_FOLDER"
        }
    }
}

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "action_type", discriminatorType = DiscriminatorType.STRING)
abstract class ActionLog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "action_type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    val actionType: ActionType? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    val project: Project
) : BaseTimeEntity()

@Entity
@DiscriminatorValue(value = ActionType.Values.CreateLink)
class CreateLinkActionLog(
    project: Project,
    val linkId: Long,
) : ActionLog(project = project)

@Entity
@DiscriminatorValue(value = ActionType.Values.DeleteLink)
class DeleteLinkActionLog(
    project: Project,
    val linkId: Long,
) : ActionLog(project = project)

@Entity
@DiscriminatorValue(value = ActionType.Values.RenameLink)
class RenameLinkActionLog(
    project: Project,
    val linkId: Long,
    val oldName: String,
    val newName: String,
) : ActionLog(project = project)

@Entity
@DiscriminatorValue(value = ActionType.Values.MoveLink)
class MoveLinkActionLog(
    project: Project,
    val linkId: Long,
    val fromFolderId: Long?,
    val toFolderId: Long?,
) : ActionLog(project = project)

@Entity
@DiscriminatorValue(value = ActionType.Values.CreateFolder)
class CreateFolderActionLog(
    project: Project,
    val folderId: Long,
) : ActionLog(project = project)

@Entity
@DiscriminatorValue(value = ActionType.Values.DeleteFolder)
class DeleteFolderActionLog(
    project: Project,
    val folderId: Long,
) : ActionLog(project = project)

@Entity
@DiscriminatorValue(value = ActionType.Values.RenameFolder)
class RenameFolderActionLog(
    project: Project,
    val folderId: Long,
    val oldName: String,
    val newName: String,
) : ActionLog(project = project)

@Entity
@DiscriminatorValue(value = ActionType.Values.MoveFolder)
class MoveFolderActionLog(
    project: Project,
    val folderId: Long,
    val fromFolderId: Long?,
    val toFolderId: Long?,
) : ActionLog(project = project)
