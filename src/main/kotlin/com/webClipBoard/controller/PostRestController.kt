package com.webClipBoard.controller

import com.webClipBoard.Account
import com.webClipBoard.CreatePostDTO
import com.webClipBoard.PostDTO
import com.webClipBoard.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/posts")
class PostRestController(
    private val postService: PostService,
) {
    @GetMapping
    fun getPosts(
        @AuthenticationPrincipal account: Account,
    ): ResponseEntity<List<PostDTO>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(postService.getPosts(account))
    }

    @PostMapping
    fun createPost(
        @RequestBody createPostDTO: CreatePostDTO,
        @AuthenticationPrincipal account: Account,
    ): ResponseEntity<Long> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(postService.createPost(account, createPostDTO))
    }

    @DeleteMapping("/{id}")
    fun deletePost(
        @PathVariable id: Long,
        @AuthenticationPrincipal account: Account,
    ): ResponseEntity<Unit> {
        postService.deletePost(account, id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}