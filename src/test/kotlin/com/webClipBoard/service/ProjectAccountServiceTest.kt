package com.webClipBoard.service

import com.webClipBoard.*
import com.webClipBoard.service.testService.AccountType
import com.webClipBoard.service.testService.TestAccountService
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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

        projectAccountService.addAccountToProject(owner, projectId, CreateProjectAccountDTO(stranger.email, true))

        val projects = projectService.getProjects(stranger)
        assertThat(projects).hasSize(1)
        assertEquals(projects[0].name, "owner_project")
    }

    @Test
    fun `addAccountToProject throw Exception if account is duplicated`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val projectId = projectService.createProject(CreateProjectDTO(
            name = "owner_project"
        ), owner)

        assertThrows<RuntimeException> {
            projectAccountService.addAccountToProject(owner, projectId, CreateProjectAccountDTO(owner.email, true))
        }
    }

    @Test
    fun `addAccountToProject throw NotFoundException if project not exists`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val unavailableId = 987654321L

        assertThrows<NotFoundException> {
            projectAccountService.addAccountToProject(owner, unavailableId, CreateProjectAccountDTO(stranger.email, true))
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

        assertThrows<UnAuthorizedProjectException> {
            projectAccountService.addAccountToProject(anotherStranger, projectId, CreateProjectAccountDTO(stranger.email, true))
        }
    }

    @Test
    fun deleteAccountToProject() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)
        projectAccountService.addAccountToProject(owner, projectId, CreateProjectAccountDTO(stranger.email, true))

        projectAccountService.deleteAccountToProject(owner, projectId, stranger.id!!)

        val projects = projectService.getProjects(stranger)
        assertThat(projects).isEmpty()
    }

    @Test
    fun `deleteAccountToProject throw ProjectNotFoundException if project not exists`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val unavailableId = 987654321L

        assertThrows<ProjectNotFoundException> {
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
        val projectAccountId = projectAccountService.addAccountToProject(owner, projectId, CreateProjectAccountDTO(stranger.email, true))

        assertThrows<UnAuthorizedProjectException> {
            projectAccountService.deleteAccountToProject(stranger, projectId, projectAccountId)
        }
    }

    @Test
    fun `deleteAccountToProject throw UnAuthorizedProjectException if try to delete owner`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val projectId = projectService.createProject(CreateProjectDTO(
            name = "owner_project"
        ), owner)
        val projectAccounts = projectAccountService.getProjectAccounts(owner, projectId)

        assertThrows<UnAuthorizedProjectException> {
            projectAccountService.deleteAccountToProject(owner, projectId, projectAccounts[0].id)
        }
    }

    @Test
    fun getProjectAccounts() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        val projectAccounts = projectAccountService.getProjectAccounts(owner, projectId)

        assertThat(projectAccounts).hasSize(1)
        assertThat(projectAccounts[0].accountName).isEqualTo(AccountType.OWNER.name)
        assertThat(projectAccounts[0].accountType).isEqualTo(ProjectAccountType.OWNER)
    }
}