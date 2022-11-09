package com.webClipBoard.service

import com.webClipBoard.CreateProjectDTO
import com.webClipBoard.ProjectNotFoundException
import com.webClipBoard.ProjectRepository
import com.webClipBoard.UnAuthorizedProjectException
import com.webClipBoard.service.testService.AccountType
import com.webClipBoard.service.testService.TestAccountService
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class ProjectServiceTest {

    @Autowired
    lateinit var projectService: ProjectService
    @Autowired
    lateinit var projectRepository: ProjectRepository
    @Autowired
    lateinit var testAccountService: TestAccountService

    @Test
    fun getProjects() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        projectService.createProject(CreateProjectDTO(
            name = "owner_project"
        ), owner)
        projectService.createProject(
            CreateProjectDTO(
            name = "stranger_project"
        ), stranger)

        val projects = projectService.getProjects(owner)

        assertThat(projects).hasSize(1)
        assertThat(projects[0].name).isEqualTo("owner_project")
    }

    @Test
    fun deleteProject() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        projectService.deleteProject(projectId, owner)

        val projects = projectRepository.findAll()
        assertThat(projects).isEmpty()
    }

    @Test
    fun `deleteProject throw UnAuthorizedProjectException if account has no auth`() {
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val owner = testAccountService.createUser(AccountType.OWNER)
        val projectId = projectService.createProject(CreateProjectDTO(
            name = "owner_project"
        ), owner)

        assertThrows<UnAuthorizedProjectException> {
            projectService.deleteProject(projectId, stranger)
        }
    }

    @Test
    fun `deleteProject throw ProjectNotFoundException if project not exists`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val unavailableId = 987654321L

        assertThrows<ProjectNotFoundException> {
            projectService.deleteProject(unavailableId, owner)
        }
    }

    @Test
    fun renameProject() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        projectService.renameProject(projectId, "new_name", owner)

        val project = projectRepository.findById(projectId).get()
        assertThat(project.name).isEqualTo("new_name")
    }

    @Test
    fun `renameProject throw UnAuthorizedProjectException if account has no auth`() {
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val owner = testAccountService.createUser(AccountType.OWNER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        assertThrows<UnAuthorizedProjectException> {
            projectService.renameProject(projectId, "new_name", stranger)
        }
    }

    @Test
    fun `renameProject throw ProjectNotFoundException if project not exists`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val unavailableId = 987654321L
        assertThrows<ProjectNotFoundException> {
            projectService.renameProject(unavailableId, "new_project", owner)
        }
    }

    @Test
    fun createProject() {
        val owner = testAccountService.createUser(AccountType.OWNER)

        val projectId = projectService.createProject(CreateProjectDTO(
            name = "project_name"
        ), owner)

        val project = projectRepository.findById(projectId).get()
        assertThat(project.name).isEqualTo("project_name")
    }

    @Test
    fun addAccountToProject() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        projectService.addAccountToProject(owner, projectId, stranger.id!!, true)

        val projects = projectService.getProjects(stranger)
        assertThat(projects).hasSize(1)
        assertEquals(projects[0].name, "owner_project")
    }

    @Test
    fun `addAccountToProject throw ProjectNotFoundException if project not exists`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val unavailableId = 987654321L

        assertThrows<ProjectNotFoundException> {
            projectService.addAccountToProject(owner, unavailableId, stranger.id!!, true)
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
            projectService.addAccountToProject(anotherStranger, projectId, stranger.id!!, true)
        }
    }

    @Test
    fun deleteAccountToProject() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)
        projectService.addAccountToProject(owner, projectId, stranger.id!!, true)

        projectService.deleteAccountToProject(owner, projectId, stranger.id!!)

        val projects = projectService.getProjects(stranger)
        assertThat(projects).isEmpty()
    }

    @Test
    fun `deleteAccountToProject throw ProjectNotFoundException if project not exists`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val unavailableId = 987654321L

        assertThrows<ProjectNotFoundException> {
            projectService.deleteAccountToProject(owner, unavailableId, stranger.id!!)
        }
    }

    @Test
    fun `deleteAccountToProject throw UnAuthorizedProjectException if account has no auth`() {
        val owner = testAccountService.createUser(AccountType.OWNER)
        val stranger = testAccountService.createUser(AccountType.STRANGER)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)
        projectService.addAccountToProject(owner, projectId, stranger.id!!, true)

        assertThrows<UnAuthorizedProjectException> {
            projectService.deleteAccountToProject(stranger, projectId, owner.id!!)
        }
    }
}
