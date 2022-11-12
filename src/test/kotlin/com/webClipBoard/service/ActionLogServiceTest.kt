package com.webClipBoard.service

import com.webClipBoard.*
import com.webClipBoard.service.testService.AccountType
import com.webClipBoard.service.testService.TestAccountService
import com.webClipBoard.service.testService.TestProjectService
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
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
        val lastLogId = actionLogService.recentActionId(owner, projectId)
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        val logs = actionLogService.getActionLogs(owner, projectId, lastLogId)
        assertThat(logs).hasSize(1)
        assertThat(logs[0].actionType).isEqualTo(ActionType.CREATE_LINK)
        assertThat(logs[0].linkId).isEqualTo(linkId)
    }

    @Test
    fun logDeleteLink() {
        initFolderForLinkTest()
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        val lastLogId = actionLogService.recentActionId(owner, projectId)
        linkService.deleteLink(owner, projectId, folderId, linkId)

        val logs = actionLogService.getActionLogs(owner, projectId, lastLogId)
        assertThat(logs).hasSize(1)
        assertThat(logs[0].actionType).isEqualTo(ActionType.DELETE_LINK)
        assertThat(logs[0].linkId).isEqualTo(linkId)
    }

    @Test
    fun logRenameLink() {
        initFolderForLinkTest()
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "old_name",
            url = "url",
        ))
        val newName = "new_name"

        val lastLogId = actionLogService.recentActionId(owner, projectId)
        linkService.renameLink(owner, projectId, folderId, linkId, newName)

        val logs = actionLogService.getActionLogs(owner, projectId, lastLogId)
        assertThat(logs).hasSize(1)
        assertThat(logs[0].actionType).isEqualTo(ActionType.RENAME_LINK)
        assertThat(logs[0].linkId).isEqualTo(linkId)
        assertThat(logs[0].oldName).isEqualTo("old_name")
        assertThat(logs[0].newName).isEqualTo("new_name")
    }

    @Test
    fun logMoveLink() {
        initFolderForLinkTest()
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        val lastLogId = actionLogService.recentActionId(owner, projectId)
        linkService.moveLink(owner, projectId, folderId, linkId, targetFolderId)

        val logs = actionLogService.getActionLogs(owner, projectId, lastLogId)
        assertThat(logs).hasSize(1)
        assertThat(logs[0].actionType).isEqualTo(ActionType.MOVE_LINK)
        assertThat(logs[0].linkId).isEqualTo(linkId)
        assertThat(logs[0].fromFolderId).isEqualTo(folderId)
        assertThat(logs[0].toFolderId).isEqualTo(targetFolderId)
    }

    @Test
    fun logCreateFolder() {
        val lastLogId = actionLogService.recentActionId(owner, projectId)
        val rootParentId = null
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = rootParentId
        ))

        val logs = actionLogService.getActionLogs(owner, projectId, lastLogId)
        assertThat(logs).hasSize(1)
        assertThat(logs[0].actionType).isEqualTo(ActionType.CREATE_FOLDER)
        assertThat(logs[0].folderId).isEqualTo(folderId)
        assertThat(logs[0].parentId).isEqualTo(null)
        assertThat(logs[0].newName).isEqualTo("root")
    }

    @Test
    fun logRenameFolder() {
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "old_name",
            parentId = null
        ))

        val lastLogId = actionLogService.recentActionId(owner, projectId)
        folderService.renameFolder(owner, projectId, folderId, "new_name")

        val logs = actionLogService.getActionLogs(owner, projectId, lastLogId)
        assertThat(logs).hasSize(1)
        assertThat(logs[0].actionType).isEqualTo(ActionType.RENAME_FOLDER)
        assertThat(logs[0].folderId).isEqualTo(folderId)
        assertThat(logs[0].oldName).isEqualTo("old_name")
        assertThat(logs[0].newName).isEqualTo("new_name")
    }

    @Test
    fun logDeleteFolder() {
        val folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(
            name = "root",
            parentId = null
        ))

        val lastLogId = actionLogService.recentActionId(owner, projectId)
        folderService.deleteFolder(owner, projectId, folderId)

        val logs = actionLogService.getActionLogs(owner, projectId, lastLogId)
        assertThat(logs).hasSize(1)
        assertThat(logs[0].actionType).isEqualTo(ActionType.DELETE_FOLDER)
        assertThat(logs[0].folderId).isEqualTo(folderId)
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

        val lastLogId = actionLogService.recentActionId(owner, projectId)
        folderService.moveFolder(owner, projectId, childFolderId, null)

        val logs = actionLogService.getActionLogs(owner, projectId, lastLogId)
        assertThat(logs).hasSize(1)
        assertThat(logs[0].actionType).isEqualTo(ActionType.MOVE_FOLDER)
        assertThat(logs[0].folderId).isEqualTo(childFolderId)
        assertThat(logs[0].fromFolderId).isEqualTo(parentFolderId)
        assertThat(logs[0].toFolderId).isNull()
    }

}