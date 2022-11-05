package com.webClipBoard.service

import com.webClipBoard.*
import com.webClipBoard.service.testService.AccountType
import com.webClipBoard.service.testService.TestAccountService
import com.webClipBoard.service.testService.TestProjectService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class ActionLogServiceTest {

    @Autowired
    lateinit var actionLogService: ActionLogService
    @Autowired
    lateinit var actionLogRepository: ActionLogRepository
    @Autowired
    lateinit var testAccountService: TestAccountService
    @Autowired
    lateinit var testProjectService: TestProjectService
    @Autowired
    lateinit var folderService: FolderService
    @Autowired
    lateinit var linkService: LinkService

    lateinit var owner: Account
    var projectId: Long = 0
    var folderId: Long = 0
    var targetFolderId: Long = 0

    @BeforeEach
    fun init() {
        owner = testAccountService.createUser(AccountType.OWNER)
        projectId = testProjectService.createProject(owner).id!!
    }

    fun initFolderForLinkTest() {
        folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(name = "owner", parentId = null))
        targetFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(name = "target", parentId = null))
    }

    @Test
    fun logCreateLink() {
        initFolderForLinkTest()
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        val logs = actionLogRepository.findAll().filterIsInstance<CreateLinkActionLog>()
        assertEquals(1, logs.size)
        assertEquals(projectId, logs[0].project.id)
        assertEquals(linkId, logs[0].linkId)
    }

    @Test
    fun logDeleteLink() {
        initFolderForLinkTest()
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        linkService.deleteLink(owner, projectId, folderId, linkId)

        val logs = actionLogRepository.findAll().filterIsInstance<DeleteLinkActionLog>()
        assertEquals(1, logs.size)
        assertEquals(projectId, logs[0].project.id)
        assertEquals(linkId, logs[0].linkId)
    }

    @Test
    fun logRenameLink() {
        initFolderForLinkTest()
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "old_name",
            url = "url",
        ))
        val newName = "new_name"

        linkService.renameLink(owner, projectId, folderId, linkId, newName)

        val logs = actionLogRepository.findAll().filterIsInstance<RenameLinkActionLog>()
        assertEquals(1, logs.size)
        assertEquals(projectId, logs[0].project.id)
        assertEquals(linkId, logs[0].linkId)
        assertEquals("old_name", logs[0].oldName)
        assertEquals("new_name", logs[0].newName)
    }

    @Test
    fun logMoveLink() {
        initFolderForLinkTest()
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        linkService.moveLink(owner, projectId, folderId, linkId, targetFolderId)

        val logs = actionLogRepository.findAll().filterIsInstance<MoveLinkActionLog>()
        assertEquals(1, logs.size)
        assertEquals(projectId, logs[0].project.id)
        assertEquals(linkId, logs[0].linkId)
        assertEquals(folderId, logs[0].fromFolderId)
        assertEquals(targetFolderId, logs[0].toFolderId)
    }

    @Test
    fun logCreateFolder() {
        val rootParentId = null
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = rootParentId
        ))

        val logs = actionLogRepository.findAll().filterIsInstance<CreateFolderActionLog>()
        assertEquals(1, logs.size)
        assertEquals(projectId, logs[0].project.id)
        assertEquals(folderId, logs[0].folderId)
    }

    @Test
    fun logRenameFolder() {
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "old_name",
            parentId = null
        ))

        folderService.renameFolder(owner, projectId, folderId, "new_name")

        val logs = actionLogRepository.findAll().filterIsInstance<RenameFolderActionLog>()
        assertEquals(1, logs.size)
        assertEquals(projectId, logs[0].project.id)
        assertEquals(folderId, logs[0].folderId)
        assertEquals("old_name", logs[0].oldName)
        assertEquals("new_name", logs[0].newName)
    }

    @Test
    fun logDeleteFolder() {
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = null
        ))

        folderService.deleteFolder(owner, projectId, folderId)

        val logs = actionLogRepository.findAll().filterIsInstance<DeleteFolderActionLog>()
        assertEquals(1, logs.size)
        assertEquals(projectId, logs[0].project.id)
        assertEquals(folderId, logs[0].folderId)
    }

    @Test
    fun logMoveFolder() {
        val parentFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = null
        ))
        val childFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "child",
            parentId = parentFolderId
        ))

        folderService.moveFolder(owner, projectId, childFolderId, null)

        val logs = actionLogRepository.findAll().filterIsInstance<MoveFolderActionLog>()
        assertEquals(1, logs.size)
        assertEquals(projectId, logs[0].project.id)
        assertEquals(childFolderId, logs[0].folderId)
        assertEquals(parentFolderId, logs[0].fromFolderId)
        assertEquals(null, logs[0].toFolderId)
    }

}