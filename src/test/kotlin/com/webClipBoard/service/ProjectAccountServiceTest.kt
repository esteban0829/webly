package com.webClipBoard.service

import com.webClipBoard.CreateProjectDTO
import com.webClipBoard.ProjectNotFoundException
import com.webClipBoard.ProjectRepository
import com.webClipBoard.UnAuthorizedProjectException
import com.webClipBoard.service.testService.AccountType
import com.webClipBoard.service.testService.TestAccountService
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProjectAccountServiceTest {

    @Autowired
    lateinit var projectService: ProjectService
    @Autowired
    lateinit var projectAccountService: ProjectAccountService
    @Autowired
    lateinit var testAccountService: TestAccountService

    @Test
    fun addAccountToProject() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        projectAccountService.addAccountToProject(owner, projectId, stranger.id!!, true)

        val projects = projectService.getProjects(stranger)
        AssertionsForInterfaceTypes.assertThat(projects).hasSize(1)
        assertEquals(projects[0].name, "owner_project")
    }

    @Test
    fun `addAccountToProject throw ProjectNotFoundException if project not exists`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val unavailableId = 987654321L

        org.junit.jupiter.api.assertThrows<ProjectNotFoundException> {
            projectAccountService.addAccountToProject(owner, unavailableId, stranger.id!!, true)
        }
    }

    @Test
    fun `addAccountToProject only owner can add admin otherwise throws UnAuthorizedProjectException`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val anotherStranger = testAccountService.createUser(AccountType.ANOTHER_STRANGER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        org.junit.jupiter.api.assertThrows<UnAuthorizedProjectException> {
            projectAccountService.addAccountToProject(anotherStranger, projectId, stranger.id!!, true)
        }
    }

    @Test
    fun deleteAccountToProject() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)
        projectAccountService.addAccountToProject(owner, projectId, stranger.id!!, true)

        projectAccountService.deleteAccountToProject(owner, projectId, stranger.id!!)

        val projects = projectService.getProjects(stranger)
        AssertionsForInterfaceTypes.assertThat(projects).isEmpty()
    }

    @Test
    fun `deleteAccountToProject throw ProjectNotFoundException if project not exists`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val unavailableId = 987654321L

        org.junit.jupiter.api.assertThrows<ProjectNotFoundException> {
            projectAccountService.deleteAccountToProject(owner, unavailableId, stranger.id!!)
        }
    }

    @Test
    fun `deleteAccountToProject throw UnAuthorizedProjectException if account has no auth`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)
        projectAccountService.addAccountToProject(owner, projectId, stranger.id!!, true)

        org.junit.jupiter.api.assertThrows<UnAuthorizedProjectException> {
            projectAccountService.deleteAccountToProject(stranger, projectId, owner.id!!)
        }
    }
}