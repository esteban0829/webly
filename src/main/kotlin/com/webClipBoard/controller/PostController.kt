package com.webClipBoard.controller

import com.webClipBoard.Account
import com.webClipBoard.CreatePostDTO
import com.webClipBoard.CreateProjectDTO
import com.webClipBoard.service.PostService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class PostController(
    private val postService: PostService,
) {

    @GetMapping("/post")
    fun getPosts(
        @AuthenticationPrincipal account: Account,
        model: Model
    ): String {
        val posts = postService.getPosts(account)
        model.addAttribute("posts", posts)

        return "pages/post";
    }

    @PostMapping("/post")
    fun createPost(
        @AuthenticationPrincipal account: Account,
        @ModelAttribute createPostDTO: CreatePostDTO,
    ): String {
        postService.createPost(account, createPostDTO)

        return "redirect:/post"
    }
}