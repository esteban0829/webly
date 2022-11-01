package com.webClipBoard.service

import com.webClipBoard.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@Transactional
@SpringBootTest
class ProjectServiceTest {

    @Autowired
    lateinit var projectService: ProjectService
    @Autowired
    lateinit var accountRepository: AccountRepository
    @Autowired
    lateinit var projectRepository: ProjectRepository

    private fun createUser(name: String) {
        val owner = Account(
            email = name,
            userId = name,
            userPassword = "1234",
            name = name,
            role = Role.USER
        )
        accountRepository.save(owner)
    }

    private fun getAccount(name: String): Account {
        return accountRepository.findByEmail(name).get()
    }

    private val ownerName = "owner"
    private val strangerName = "stranger"
    private val anotherStranger = "anotherStranger"

    @BeforeEach
    fun setUp() {
        createUser(ownerName)
        createUser(strangerName)
        createUser(anotherStranger)
    }

    @Test
    fun getProjects() {
        val owner = getAccount(ownerName)
        val stranger = getAccount(strangerName)
        projectService.createProject(CreateProjectDTO(
            name = "owner_project"
        ), owner)
        projectService.createProject(
            CreateProjectDTO(
            name = "stranger_project"
        ), stranger)

        val projects = projectService.getProjects(owner)

        assertEquals(projects.size, 1)
        assertEquals(projects[0].name, "owner_project")
    }

    @Test
    fun deleteProject() {
        val owner = getAccount(ownerName)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        projectService.deleteProject(projectId, owner)

        val exists = projectRepository.existsById(projectId)
        assertFalse(exists)
    }

    @Test
    fun `deleteProject throw UnAuthorizedProjectException if account has no auth`() {
        val stranger = getAccount(strangerName)
        val owner = getAccount(ownerName)
        val projectId = projectService.createProject(CreateProjectDTO(
            name = "owner_project"
        ), owner)

        assertThrows(UnAuthorizedProjectException::class.java) {
            projectService.deleteProject(projectId, stranger)
        }
    }

    @Test
    fun `deleteProject throw ProjectNotFoundException if project not exists`() {
        val owner = getAccount(ownerName)
        val unavailableId = 987654321L
        assertThrows(ProjectNotFoundException::class.java) {
            projectService.deleteProject(unavailableId, owner)
        }
    }

    @Test
    fun renameProject() {
        val owner = getAccount(ownerName)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        projectService.renameProject(projectId, "new_name", owner)

        val project = projectRepository.findById(projectId).get()
        assertEquals(project.name, "new_name")
    }

    @Test
    fun `renameProject throw UnAuthorizedProjectException if account has no auth`() {
        val stranger = getAccount(strangerName)
        val owner = getAccount(ownerName)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        assertThrows(UnAuthorizedProjectException::class.java) {
            projectService.renameProject(projectId, "new_name", stranger)
        }
    }

    @Test
    fun `renameProject throw ProjectNotFoundException if project not exists`() {
        val owner = getAccount(ownerName)
        val unavailableId = 987654321L
        assertThrows(ProjectNotFoundException::class.java) {
            projectService.renameProject(unavailableId, "new_project", owner)
        }
    }

    @Test
    fun createProject() {
        val owner = getAccount(ownerName)

        val projectId = projectService.createProject(CreateProjectDTO(
            name = "project_name"
        ), owner)

        val project = projectRepository.findById(projectId).get()
        assertEquals(project.name, "project_name")
    }

    @Test
    fun addAccountToProject() {
        val owner = getAccount(ownerName)
        val stranger = getAccount(strangerName)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        projectService.addAccountToProject(owner, projectId, stranger.id!!, true)

        val projects = projectService.getProjects(stranger)
        assertAll({
            assertEquals(projects.size, 1)
            assertEquals(projects[0].name, "owner_project")
        })
    }

    @Test
    fun `addAccountToProject throw ProjectNotFoundException if project not exists`() {
        val owner = getAccount(ownerName)
        val stranger = getAccount(strangerName)
        val unavailableId = 987654321L

        assertThrows(ProjectNotFoundException::class.java) {
            projectService.addAccountToProject(owner, unavailableId, stranger.id!!, true)
        }
    }

    @Test
    fun `addAccountToProject only owner can add admin otherwise throws UnAuthorizedProjectException`() {
        val owner = getAccount(ownerName)
        val stranger = getAccount(strangerName)
        val anotherStranger = getAccount(anotherStranger)
        val projectId = projectService.createProject(CreateProjectDTO(
                name = "owner_project"
        ), owner)

        assertThrows(UnAuthorizedProjectException::class.java) {
            projectService.addAccountToProject(anotherStranger, projectId, stranger.id!!, true)
        }
    }
}
