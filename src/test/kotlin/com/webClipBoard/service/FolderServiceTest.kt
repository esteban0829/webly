package com.webClipBoard.service

import com.webClipBoard.*
import com.webClipBoard.service.testService.AccountType
import com.webClipBoard.service.testService.TestAccountService
import com.webClipBoard.service.testService.TestProjectService
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest
class FolderServiceTest {

    @Autowired
    lateinit var testAccountService: TestAccountService
    @Autowired
    lateinit var testProjectService: TestProjectService
    @Autowired
    lateinit var folderService: FolderService

    lateinit var owner: Account
    private var projectId: Long = 0

    @BeforeEach
    fun init() {
        owner = testAccountService.createUser(AccountType.OWNER)
        projectId = testProjectService.createProject(owner).id!!
    }

    @Test
    fun createFolder() {
        val rootParentId = null
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = rootParentId
        ))

        val folders = folderService.getFolders(owner, projectId, rootParentId)
        assertThat(folders).hasSize(1)
        assertThat(folders[0].id).isEqualTo(folderId)
        assertThat(folders[0].name).isEqualTo("root")
        assertThat(folders[0].parentId).isEqualTo(rootParentId)
    }

    @Test
    fun renameFolder() {
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "old_name",
            parentId = null
        ))

        folderService.renameFolder(owner, projectId, folderId, "new_name")

        val folder = folderService.getFolderDetail(owner, projectId, folderId)
        assertThat(folder.name).isEqualTo("new_name")
    }

    @Test
    fun deleteFolder() {
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = null
        ))

        folderService.deleteFolder(owner, projectId, folderId)

        assertThrows<FolderNotFoundException> {
            folderService.getFolderDetail(owner, projectId, folderId)
        }
    }

    @Test
    fun `deleteFolder delete children folders`() {
        val parentFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "parent",
            parentId = null
        ))
        val childFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "child",
            parentId = parentFolderId
        ))
        folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "child",
            parentId = childFolderId
        ))

        folderService.deleteFolder(owner, projectId, parentFolderId)

        val result = folderService.getFolders(owner, projectId, null)
        assertThat(result).isEmpty()
    }

    @Test
    fun moveFolder() {
        val parentFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = null
        ))
        val childFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "child",
            parentId = parentFolderId
        ))

        folderService.moveFolder(owner, projectId, childFolderId, null)

        val folders = folderService.getFolders(owner, projectId, null)
        assertThat(folders).hasSize(2)
    }

    @Test
    fun `moveFolder can not move to child folder`() {
        val parentFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = null
        ))
        val childFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "child",
            parentId = parentFolderId
        ))

        assertThrows<NotAllowedMoveToChildFolderException> {
            folderService.moveFolder(owner, projectId, parentFolderId, childFolderId)
        }
    }

    @Test
    fun getFolderDetail() {
        val parentFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = null
        ))
        val childFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "child",
            parentId = parentFolderId
        ))

        val folder = folderService.getFolderDetail(owner, projectId, parentFolderId)

        assertThat(folder.id).isEqualTo(parentFolderId)
        assertThat(folder.name).isEqualTo("root")
        assertThat(folder.parentId).isNull()
        assertThat(folder.childFolders).hasSize(1)
        assertThat(folder.childFolders[0].id).isEqualTo(childFolderId)
        assertThat(folder.childFolders[0].name).isEqualTo("child")
        assertThat(folder.childFolders[0].parentId).isEqualTo(parentFolderId)
    }

    @Test
    fun getFolderIfAccountHasPermission() {
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = null
        ))
        val stranger = testAccountService.createUser(AccountType.STRANGER)

        assertThrows<UnAuthorizedProjectException> {
            folderService.getFolderIfAccountHasPermission(stranger, projectId, folderId)
        }
    }
}