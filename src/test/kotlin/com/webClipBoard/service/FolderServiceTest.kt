package com.webClipBoard.service

import com.webClipBoard.*
import com.webClipBoard.service.testService.AccountType
import com.webClipBoard.service.testService.TestAccountService
import com.webClipBoard.service.testService.TestProjectService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        assertEquals(1, folders.size)
        assertEquals(folderId, folders[0].id)
        assertEquals("root", folders[0].name)
        assertEquals(rootParentId, folders[0].parentId)
    }

    @Test
    fun renameFolder() {
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "old_name",
            parentId = null
        ))

        folderService.renameFolder(owner, projectId, folderId, "new_name")

        val folder = folderService.getFolderDetail(owner, projectId, folderId)
        assertEquals(folder.name, "new_name")
    }

    @Test
    fun deleteFolder() {
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = null
        ))

        folderService.deleteFolder(owner, projectId, folderId)

        assertThrows(FolderNotFoundException::class.java) {
            folderService.getFolderDetail(owner, projectId, folderId)
        }
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
        assertEquals(2, folders.size)
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

        assertThrows(NotAllowedMoveToChildFolderException::class.java) {
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

        assertEquals(parentFolderId, folder.id)
        assertEquals("root", folder.name)
        assertEquals(null, folder.parentId)
        assertEquals(1, folder.childFolders.size)
        assertEquals(childFolderId, folder.childFolders[0].id)
        assertEquals("child", folder.childFolders[0].name)
        assertEquals(parentFolderId, folder.childFolders[0].parentId)
    }

    @Test
    fun getFolderIfAccountHasPermission() {
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = null
        ))
        val stranger = testAccountService.createUser(AccountType.STRANGER)

        assertThrows(UnAuthorizedProjectException::class.java) {
            folderService.getFolderIfAccountHasPermission(stranger, projectId, folderId)
        }
    }
}