package com.webClipBoard.service

import com.webClipBoard.Account
import com.webClipBoard.CreateFolderDTO
import com.webClipBoard.CreateLinkDTO
import com.webClipBoard.LinkNotFoundException
import com.webClipBoard.service.testService.AccountType
import com.webClipBoard.service.testService.TestAccountService
import com.webClipBoard.service.testService.TestProjectService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class LinkServiceTest {

    @Autowired
    lateinit var linkService: LinkService
    @Autowired
    lateinit var testAccountService: TestAccountService
    @Autowired
    lateinit var testProjectService: TestProjectService
    @Autowired
    lateinit var folderService: FolderService

    lateinit var owner: Account
    var projectId: Long = 0
    var folderId: Long = 0
    var targetFolderId: Long = 0

    @BeforeEach
    fun init() {
        owner = testAccountService.createUser(AccountType.OWNER)
        projectId = testProjectService.createProject(owner).id!!
        folderId = folderService.createFolder(owner, projectId, CreateFolderDTO(name = "owner", parentId = null))
        targetFolderId = folderService.createFolder(owner, projectId, CreateFolderDTO(name = "target", parentId = null))
    }

    @Test
    fun createLink() {
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        val link = linkService.getLink(owner, projectId, folderId, linkId)
        assertEquals(linkId, link.id)
        assertEquals("link", link.name)
        assertEquals("url", link.url)
        assertEquals(folderId, link.folderId)
    }

    @Test
    fun deleteLink() {
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        linkService.deleteLink(owner, projectId, folderId, linkId)

        assertThrows(LinkNotFoundException::class.java) {
            linkService.getLink(owner, projectId, folderId, linkId)
        }
    }

    @Test
    fun renameLink() {
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))
        val newName = "new_name"

        linkService.renameLink(owner, projectId, folderId, linkId, newName)

        val link = linkService.getLink(owner, projectId, folderId, linkId)
        assertEquals(linkId, link.id)
        assertEquals(newName, link.name)
        assertEquals("url", link.url)
    }

    @Test
    fun moveLink() {
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        linkService.moveLink(owner, projectId, folderId, linkId, targetFolderId)

        val link = linkService.getLink(owner, projectId, targetFolderId, linkId)
        assertEquals(targetFolderId, link.folderId)
    }
}