package com.webClipBoard.controller

import com.webClipBoard.Account
import com.webClipBoard.CreateProjectDTO
import com.webClipBoard.service.ProjectService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class ProjectController(
    private val projectService: ProjectService,
) {

    @GetMapping("/project")
    fun project(
        @AuthenticationPrincipal account: Account,
        model: Model
    ): String {
        model.addAttribute("projects", projectService.getProjects(account))

        return "pages/projects/project"
    }

    @PostMapping("/project")
    fun createProject(
        @AuthenticationPrincipal account: Account,
        @ModelAttribute createProjectDTO: CreateProjectDTO,
        model: Model,
    ): String {
        projectService.createProject(createProjectDTO, account)

        return "redirect:/project"
    }

}