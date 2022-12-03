package com.webClipBoard.service

import com.webClipBoard.Account
import com.webClipBoard.CreateFolderDTO
import com.webClipBoard.CreateLinkDTO
import com.webClipBoard.LinkNotFoundException
import com.webClipBoard.service.testService.AccountType
import com.webClipBoard.service.testService.TestAccountService
import com.webClipBoard.service.testService.TestProjectService
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import kotlin.properties.Delegates.notNull

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
    var projectId by notNull<Long>()
    var folderId by notNull<Long>()
    var targetFolderId by notNull<Long>()

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
        assertThat(link.id).isEqualTo(linkId)
        assertThat(link.name).isEqualTo("link")
        assertThat(link.url).isEqualTo("url")
        assertThat(link.folderId).isEqualTo(folderId)
    }

    @Test
    fun deleteLink() {
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        linkService.deleteLink(owner, projectId, folderId, linkId)

        assertThrows<LinkNotFoundException> {
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
        assertThat(link.id).isEqualTo(linkId)
        assertThat(link.name).isEqualTo(newName)
        assertThat(link.url).isEqualTo("url")
    }

    @Test
    fun moveLink() {
        val linkId = linkService.createLink(owner, projectId, folderId, CreateLinkDTO(
            name = "link",
            url = "url",
        ))

        linkService.moveLink(owner, projectId, folderId, linkId, targetFolderId)

        val link = linkService.getLink(owner, projectId, targetFolderId, linkId)
        assertThat(link.folderId).isEqualTo(targetFolderId)
    }
}