package com.webClipBoard.service

import com.webClipBoard.Account
import com.webClipBoard.AccountRepository
import com.webClipBoard.CreateProjectDTO
import com.webClipBoard.Role
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@Transactional
@SpringBootTest
internal class ProjectServiceTest {

    @Autowired
    lateinit var projectService: ProjectService
    @Autowired
    lateinit var accountRepository: AccountRepository

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

    @BeforeEach
    fun setUp() {
        createUser(ownerName)
        createUser(strangerName)
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
    }

    @Test
    fun renameProject() {
    }

    @Test
    fun createProject() {
    }
}